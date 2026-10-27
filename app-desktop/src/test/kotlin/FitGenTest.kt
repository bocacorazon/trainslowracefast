package com.trainerapp.desktop

import com.trainerapp.core.fit.ActivitySample
import com.trainerapp.core.fit.FitEncoder
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Writes sample ride files to $FIT_GEN_DIR (set in the test env) so an
 * independent parser (fitparse) can verify the encoder output end-to-end.
 */
class FitGenTest {

    @Test
    fun writeSampleFiles() {
        val dir = System.getenv("FIT_GEN_DIR")?.let { java.nio.file.Path.of(it) } ?: return
        val encoder = FitEncoder()
        for (s in 0..59) {
            encoder.add(
                ActivitySample(
                    timestamp = 1_761_576_000 + s,
                    watts = 200 + s,
                    heartRateBpm = 130 + (s / 10),
                    speedKph = if (s % 5 == 0) null else 32.0,
                    distanceMeters = s * (32.0 / 3.6),
                ),
            )
        }
        val out = Files.createDirectories(dir)
        Files.write(out.resolve("sample.fit"), encoder.toCompleteFile())
        val partial = FitEncoder()
        partial.add(ActivitySample(0, 150, 120, null, 0.0))
        Files.write(out.resolve("sample-inprogress.fit"), partial.toInProgressFile())
        assertTrue(Files.exists(out.resolve("sample.fit")))
    }
}
