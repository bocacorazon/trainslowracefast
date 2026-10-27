package com.trainerapp.desktop

import com.trainerapp.core.fit.ActivitySample
import com.trainerapp.core.fit.FitDecoder
import com.trainerapp.core.fit.FitEncoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Ride recording with incremental FIT persistence (increment 004).
 *
 * The ride is flushed to disk EVERY SAMPLE (1 Hz) as a whole-file rewrite:
 * header + all records so far, WITHOUT the CRC trailer. A crash therefore
 * costs at most one second of ride, and the file on disk is one trailer
 * short of importable — [findIncomplete] finds it on next launch so the
 * rider can resume (records keep flowing into the same file, timestamps
 * continuous) or discard it.
 *
 * [stop] writes the trailer, at which point the file is a complete,
 * importable FIT file (FIT 2.0, little-endian: file_id + software_version +
 * records with timestamp, heart_rate, distance, speed, power).
 */
class RideRecorder(
    val ridesDir: Path,
    private val scope: CoroutineScope,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {

    sealed interface RecorderState {
        data object Idle : RecorderState
        data class Recording(val file: Path, val startedAtMillis: Long, val sampleCount: Int) : RecorderState
        data class Stopped(val file: Path, val durationMillis: Long, val sampleCount: Int) : RecorderState
    }

    private val _state = kotlinx.coroutines.flow.MutableStateFlow<RecorderState>(RecorderState.Idle)
    val state: kotlinx.coroutines.flow.StateFlow<RecorderState> = _state

    private var encoder: FitEncoder? = null
    private var sampleJob: Job? = null
    private var startedAtMillis: Long = 0L
    private var nextAbsTs: Int = 0
    private var distanceMeters: Double = 0.0
    /** Records already in the file before a resume (the encoder only counts new ones). */
    private var baseSampleCount: Int = 0
    /** Raw message region of a resumed file, kept verbatim (re-emitted with the updated header size). */
    private var preData: ByteArray = ByteArray(0)

    val isRecording: Boolean get() = _state.value is RecorderState.Recording

    /**
     * Starts recording from [snapshotProvider], polled once per second.
     * The provider may return all-null snapshots (no trainer, no strap) —
     * the ride is still recorded, just with FIT-invalid fields.
     */
    fun start(snapshotProvider: () -> TelemetrySnapshot): Path {
        check(!isRecording) { "already recording" }
        Files.createDirectories(ridesDir)
        startedAtMillis = clock()
        val fitStart = fitTimestampFor(startedAtMillis)
        nextAbsTs = fitStart
        distanceMeters = 0.0
        baseSampleCount = 0
        preData = ByteArray(0)
        val file = ridesDir.resolve(fileNameFor(startedAtMillis))
        encoder = FitEncoder()
        _state.value = RecorderState.Recording(file, startedAtMillis, 0)
        sampleJob = scope.launch {
            while (isActive && isRecording) {
                val snap = snapshotProvider()
                encoder?.add(
                    ActivitySample(
                        timestamp = nextAbsTs,
                        watts = snap.watts,
                        heartRateBpm = snap.heartRateBpm,
                        speedKph = snap.speedKph,
                        distanceMeters = distanceMeters,
                    ),
                )
                nextAbsTs++
                distanceMeters += (snap.speedKph ?: 0.0) / 3.6 // per 1 s sample
                flushInProgress()
                _state.value = RecorderState.Recording(file, startedAtMillis, baseSampleCount + (encoder?.count ?: 0))
                delay(SAMPLE_INTERVAL_MS)
            }
        }
        return file
    }

    /**
     * Resumes a file left in progress by a crash (or an earlier stop without
     * trailer). The old records are kept verbatim; new samples continue at
     * the last record's absolute timestamp + 1.
     */
    fun resume(file: Path, snapshotProvider: () -> TelemetrySnapshot): Path {
        check(!isRecording) { "already recording" }
        val bytes = Files.readAllBytes(file)
        val parsed = FitDecoder.parse(bytes)
            ?: error("not a ride file: $file")
        check(!parsed.complete) { "file already complete: $file" }
        preData = bytes.copyOfRange(12, bytes.size) // everything after the 12-byte header
        distanceMeters = lastDistanceMeters(preData)
        baseSampleCount = parsed.sampleCount
        nextAbsTs = (parsed.lastTimestamp ?: fitTimestampFor(clock())) + 1
        // UI duration label only: estimate the start from the record count.
        startedAtMillis = clock() - parsed.sampleCount * SAMPLE_INTERVAL_MS
        encoder = FitEncoder(preData)
        _state.value = RecorderState.Recording(file, startedAtMillis, baseSampleCount)
        sampleJob = scope.launch {
            while (isActive && isRecording) {
                val snap = snapshotProvider()
                encoder?.add(
                    ActivitySample(
                        timestamp = nextAbsTs,
                        watts = snap.watts,
                        heartRateBpm = snap.heartRateBpm,
                        speedKph = snap.speedKph,
                        distanceMeters = distanceMeters,
                    ),
                )
                nextAbsTs++
                distanceMeters += (snap.speedKph ?: 0.0) / 3.6
                flushInProgress()
                _state.value = RecorderState.Recording(file, startedAtMillis, baseSampleCount + (encoder?.count ?: 0))
                delay(SAMPLE_INTERVAL_MS)
            }
        }
        return file
    }

    /** Finalizes the ride: writes the trailer, making the file importable. */
    fun stop() {
        val recording = _state.value as? RecorderState.Recording ?: return
        sampleJob?.cancel()
        sampleJob = null
        val enc = encoder ?: return
        val complete = enc.toCompleteFile()
        writeAtomically(recording.file, complete)
        _state.value = RecorderState.Stopped(recording.file, clock() - recording.startedAtMillis, baseSampleCount + enc.count)
        encoder = null
        baseSampleCount = 0
        preData = ByteArray(0)
    }

    /** Rides left in progress on a previous launch (crashed or never finalized). */
    fun findIncomplete(): List<IncompleteRide> {
        if (!Files.isDirectory(ridesDir)) return emptyList()
        return Files.list(ridesDir).use { stream ->
            stream.toList().asSequence()
                .filter { it.toString().endsWith(".fit") }
                .sortedBy { it.toString() }
                .map { p ->
                    val bytes = try { Files.readAllBytes(p) } catch (_: java.io.IOException) { null }
                    val parsed = bytes?.let { FitDecoder.parse(it) }
                    if (parsed != null && !parsed.complete) {
                        IncompleteRide(p, parsed.lastTimestamp, parsed.sampleCount)
                    } else null
                }
                .filterNotNull()
                .toList()
        }
    }

    private fun flushInProgress() {
        val recording = _state.value as? RecorderState.Recording ?: return
        val enc = encoder ?: return
        writeAtomically(recording.file, enc.toInProgressFile())
    }

    private fun writeAtomically(file: Path, bytes: ByteArray) {
        val tmp = ridesDir.resolve(file.fileName.toString() + ".part")
        Files.write(tmp, bytes)
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING)
    }

    private fun fileNameFor(millis: Long): String {
        val ts = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(millis))
        return "$ts.fit"
    }

    /** FIT epoch (2012-12-31T00:00:00Z) is unix 1356994800. */
    private fun fitTimestampFor(millis: Long): Int = ((millis / 1000) - FIT_EPOCH_UNIX_SECONDS).toInt()

    /**
     * Last record's distance field (0.01 m units) from a raw message region,
     * walking the 1-byte message headers (our fixed record layout).
     */
    private fun lastDistanceMeters(data: ByteArray): Double {
        var pos = 0
        var last = 0L
        var recordPayload = 0
        while (pos < data.size) {
            val head = data[pos].toInt() and 0xFF
            if (head and 0x40 != 0) {
                if (pos + 6 > data.size) break
                val numFields = data[pos + 5].toInt() and 0xFF
                if ((head and 0x0F) == 2) {
                    recordPayload = 0
                    for (n in 0 until numFields) {
                        recordPayload += data[pos + 6 + 3 * n + 1].toInt() and 0xFF
                    }
                }
                pos += 6 + 3 * numFields
            } else {
                if ((head and 0x0F) != 2 || recordPayload == 0) break
                val payloadStart = pos + 1
                if (payloadStart + recordPayload > data.size) break
                // record layout: heart_rate(1) distance(4) speed(2) power(2) timestamp(4)
                last = (data[payloadStart + 1].toInt() and 0xFF).toLong() or
                    ((data[payloadStart + 2].toInt() and 0xFF).toLong() shl 8) or
                    ((data[payloadStart + 3].toInt() and 0xFF).toLong() shl 16) or
                    ((data[payloadStart + 4].toInt() and 0xFF).toLong() shl 24)
                pos = payloadStart + recordPayload
            }
        }
        return last / 100.0
    }

    companion object {
        const val SAMPLE_INTERVAL_MS = 1_000L

        /** Default ride storage: `~/Rides/trainerapp/` — plain to find, easy to import. */
        fun defaultRidesDir(): Path = Path.of(System.getProperty("user.home"), "Rides", "trainerapp")
    }
}

/** One poll of the live telemetry, all sources merged. Null = source silent. */
data class TelemetrySnapshot(
    val watts: Int?,
    val heartRateBpm: Int?,
    val speedKph: Double?,
)

/** FIT epoch (2012-12-31T00:00:00Z) as unix seconds. */
internal const val FIT_EPOCH_UNIX_SECONDS = 1_356_994_800L

/** A ride file that never got its CRC trailer (crashed launch). */
data class IncompleteRide(
    val file: Path,
    val lastTimestamp: Int?,
    val sampleCount: Int,
) {
    /** 1 Hz sampling, so the record count is the ride length in seconds. */
    val durationSeconds: Int get() = sampleCount
}
