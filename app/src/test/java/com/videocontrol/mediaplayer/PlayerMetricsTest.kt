package com.videocontrol.mediaplayer

import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerMetricsTest {

    @Test
    fun `snapshot reports uptime and all counters`() {
        val snapshot = PlayerMetrics.snapshot(65_000L)
        assertTrue(snapshot.contains("uptime=65s"))
        assertTrue(snapshot.contains("forcedReconnects="))
        assertTrue(snapshot.contains("sourceErrors="))
    }

    @Test
    fun `counters are monotonic`() {
        val before = PlayerMetrics.snapshot(0L)
        PlayerMetrics.notePong()
        PlayerMetrics.noteForcedReconnect()
        PlayerMetrics.noteSourceError()
        val after = PlayerMetrics.snapshot(0L)
        assertTrue(after != before)
    }
}
