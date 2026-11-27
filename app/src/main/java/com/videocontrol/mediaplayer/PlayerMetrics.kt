package com.videocontrol.mediaplayer

import java.util.concurrent.atomic.AtomicLong

/**
 * Счётчики 24/7. Чистый JVM-объект: те же методы доступны в unit-тестах.
 * Раз в 5 минут плеер пишет snapshot в лог — по нему видно деградацию
 * без доступа к внутреннему состоянию процесса.
 */
object PlayerMetrics {
    private val pongs = AtomicLong()
    private val forcedReconnects = AtomicLong()
    private val sourceErrors = AtomicLong()
    private val screenWakes = AtomicLong()
    private val networkChanges = AtomicLong()
    private val readyTransitions = AtomicLong()

    fun notePong() { pongs.incrementAndGet() }

    fun noteForcedReconnect() { forcedReconnects.incrementAndGet() }

    fun noteSourceError() { sourceErrors.incrementAndGet() }

    fun noteScreenWake() { screenWakes.incrementAndGet() }

    fun noteNetworkChange() { networkChanges.incrementAndGet() }

    fun noteReady() { readyTransitions.incrementAndGet() }

    fun snapshot(uptimeMs: Long): String =
        "uptime=${uptimeMs / 1000}s pongs=${pongs.get()} " +
            "forcedReconnects=${forcedReconnects.get()} sourceErrors=${sourceErrors.get()} " +
            "screenWakes=${screenWakes.get()} networkChanges=${networkChanges.get()} " +
            "ready=${readyTransitions.get()}"
}
