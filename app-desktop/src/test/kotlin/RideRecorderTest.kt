package com.trainerapp.desktop

import com.trainerapp.core.fit.FitCrc
import com.trainerapp.core.fit.FitDecoder
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RideRecorderTest {

    private val snapshot = TelemetrySnapshot(watts = 250, heartRateBpm = 145, speedKph = 30.0)
    private var clockMs: Long = 1_761_576_000_000L // any value works

    /** The clock never advances in tests, so the ride's absolute FIT start is fixed. */
    private val fitStart: Int = ((clockMs / 1000) - FIT_EPOCH_UNIX_SECONDS).toInt()

    /**
     * Recorder on the test scheduler so [advanceTimeBy] drives its 1 Hz loop.
     * The first sample lands at t=0, so N ms of virtual time yields N/1000 + 1 samples.
     */
    private fun TestScope.makeRecorder(dir: java.nio.file.Path): Pair<RideRecorder, CoroutineScope> {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        return RideRecorder(dir, scope, clock = { clockMs }) to scope
    }

    @Test
    fun startStopProducesCompleteImportableFile() = runTest {
        val dir = Files.createTempDirectory("ride")
        val (recorder, scope) = makeRecorder(dir)
        scope.launch { recorder.start({ snapshot }) }
        runCurrent()
        advanceTimeBy(2_000) // 3 samples at 1 Hz (t=0,1,2)
        runCurrent()

        val recording = recorder.state.value as RideRecorder.RecorderState.Recording
        assertEquals(3, recording.sampleCount)
        // Incremental: the on-disk file exists and is one trailer short of complete.
        assertFalse(FitCrc.isComplete(Files.readAllBytes(recording.file)))

        recorder.stop()
        val stopped = recorder.state.value as RideRecorder.RecorderState.Stopped
        val file = Files.readAllBytes(stopped.file)
        assertTrue(FitCrc.isComplete(file), "stopped file must be a complete FIT file")
        val parsed = FitDecoder.parse(file)
        assertNotNull(parsed)
        assertEquals(3, parsed.sampleCount)
        assertEquals(fitStart + 2, parsed.lastTimestamp, "absolute FIT timestamps continue from ride start")
        assertEquals(3, stopped.sampleCount)
    }

    @Test
    fun crashLeavesDetectableIncompleteRide() = runTest {
        val dir = Files.createTempDirectory("ride")
        val (recorder, scope) = makeRecorder(dir)
        scope.launch { recorder.start({ snapshot }) }
        runCurrent()
        advanceTimeBy(4_000) // 5 samples (t=0..4)
        runCurrent()
        // No stop(): simulate the app dying mid-ride.
        scope.cancel()
        val incomplete = recorder.findIncomplete()
        assertEquals(1, incomplete.size)
        assertEquals(5, incomplete.single().sampleCount)
        assertEquals(fitStart + 4, incomplete.single().lastTimestamp)
    }

    @Test
    fun resumeContinuesSameFileWithContinuousTimestamps() = runTest {
        val dir = Files.createTempDirectory("ride")
        val (recorder, scope) = makeRecorder(dir)
        scope.launch { recorder.start({ snapshot }) }
        runCurrent()
        advanceTimeBy(3_000) // 4 samples (t=0..3)
        runCurrent()
        val file = (recorder.state.value as RideRecorder.RecorderState.Recording).file
        scope.cancel() // "crash" the first launch so it can't keep writing the file

        // New app launch: new recorder on the same dir.
        val (recorder2, scope2) = makeRecorder(dir)
        val leftover = recorder2.findIncomplete().single()
        assertEquals(4, leftover.sampleCount)
        scope2.launch { recorder2.resume(leftover.file, { snapshot }) }
        runCurrent()
        advanceTimeBy(2_000) // 3 more samples (t=4..6)
        runCurrent()
        recorder2.stop()

        val parsed = FitDecoder.parse(Files.readAllBytes(file))
        assertNotNull(parsed)
        assertTrue(parsed.complete)
        assertEquals(4 + 3, parsed.sampleCount, "resumed file keeps the old records and appends the new ones")
        assertEquals(fitStart + 6, parsed.lastTimestamp, "absolute timestamps stay continuous across the crash")
    }

    @Test
    fun nullSnapshotsStillRecord() = runTest {
        val dir = Files.createTempDirectory("ride")
        val (recorder, scope) = makeRecorder(dir)
        scope.launch { recorder.start({ TelemetrySnapshot(null, null, null) }) }
        runCurrent()
        advanceTimeBy(1_000) // 2 samples (t=0,1)
        runCurrent()
        recorder.stop()
        val stopped = recorder.state.value as RideRecorder.RecorderState.Stopped
        assertEquals(2, stopped.sampleCount)
        assertTrue(FitCrc.isComplete(Files.readAllBytes(stopped.file)))
    }
}
