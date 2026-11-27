package com.videocontrol.mediaplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Тесты политики 24/7 — запускаются в CI через `./gradlew test`. */
class PlayerPolicyTest {

    private val gb = 1024L * 1024L * 1024L
    private val mb = 1024L * 1024L

    @Test
    fun `low heap selects minimum profile even on 64-bit device`() {
        val profile = PlayerPolicy.memoryProfile(
            totalMemBytes = 4 * gb,
            heapMaxBytes = 128 * mb,
            lowMemory = false,
            lowRamDevice = false
        )
        assertEquals(PlayerPolicy.MemoryProfile.MINIMUM, profile)
    }

    @Test
    fun `one gigabyte device selects minimum profile`() {
        val profile = PlayerPolicy.memoryProfile(
            totalMemBytes = gb,
            heapMaxBytes = 512 * mb,
            lowMemory = false,
            lowRamDevice = false
        )
        assertEquals(PlayerPolicy.MemoryProfile.MINIMUM, profile)
    }

    @Test
    fun `low ram flag selects minimum profile`() {
        val profile = PlayerPolicy.memoryProfile(
            totalMemBytes = 8 * gb,
            heapMaxBytes = 2 * gb,
            lowMemory = false,
            lowRamDevice = true
        )
        assertEquals(PlayerPolicy.MemoryProfile.MINIMUM, profile)
    }

    @Test
    fun `two gigabytes selects standard and four gigabytes selects full`() {
        assertEquals(
            PlayerPolicy.MemoryProfile.STANDARD,
            PlayerPolicy.memoryProfile(2 * gb, 512 * mb, false, false)
        )
        assertEquals(
            PlayerPolicy.MemoryProfile.FULL,
            PlayerPolicy.memoryProfile(4 * gb, 1024 * mb, false, false)
        )
    }

    @Test
    fun `cache is capped for weak devices and untouched for full profile`() {
        val configured = 500 * mb
        assertEquals(64 * mb, PlayerPolicy.effectiveCacheSize(PlayerPolicy.MemoryProfile.MINIMUM, configured))
        assertEquals(100 * mb, PlayerPolicy.effectiveCacheSize(PlayerPolicy.MemoryProfile.STANDARD, configured))
        assertEquals(configured, PlayerPolicy.effectiveCacheSize(PlayerPolicy.MemoryProfile.FULL, configured))
    }

    @Test
    fun `watchdog ignores servers that never send pong`() {
        assertFalse(
            PlayerPolicy.shouldForceReconnect(
                nowMs = 10 * 60_000L,
                lastInboundAtMs = 0L,
                hasSeenPong = false,
                connected = true
            )
        )
    }

    @Test
    fun `watchdog keeps fresh pong and reconnects after stale inbound`() {
        val now = 1_000_000L
        assertFalse(
            PlayerPolicy.shouldForceReconnect(now, now - 20_000L, true, true)
        )
        assertTrue(
            PlayerPolicy.shouldForceReconnect(now, now - 61_000L, true, true)
        )
    }

    @Test
    fun `disconnected socket always requires reconnect`() {
        assertTrue(
            PlayerPolicy.shouldForceReconnect(
                nowMs = 1000L,
                lastInboundAtMs = 900L,
                hasSeenPong = true,
                connected = false
            )
        )
    }
}
