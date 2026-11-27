package com.videocontrol.mediaplayer

/**
 * Чистая JVM-логика политики 24/7.
 *
 * Нет зависимостей от Android — этот же код выполняется в unit-тестах CI
 * и в плеере, поэтому поведение на приставке и в тестах не расходится.
 */
object PlayerPolicy {
    enum class MemoryProfile { MINIMUM, STANDARD, FULL }

    const val LOW_RAM_BYTES = 1024L * 1024L * 1024L
    const val STANDARD_RAM_BYTES = 3072L * 1024L * 1024L
    const val MIN_HEAP_BYTES = 128L * 1024L * 1024L
    const val MIN_CACHE_BYTES = 64L * 1024L * 1024L
    const val STANDARD_CACHE_BYTES = 100L * 1024L * 1024L

    /** Сокет шлёт pong примерно каждые 20 с; 60 с тишины — уже аномалия. */
    const val INBOUND_STALE_MS = 60_000L

    /** EVENT_CONNECT не пришёл за 30 с — реконнект считается застрявшим. */
    const val RECONNECT_STUCK_MS = 30_000L

    /**
     * Профиль выбирается по реальной памяти, а не по разрядности ABI:
     * arm64/x86_64-приставки тоже бывают с 1 ГБ RAM, а 32-bit — с 4 ГБ.
     */
    fun memoryProfile(
        totalMemBytes: Long,
        heapMaxBytes: Long,
        lowMemory: Boolean,
        lowRamDevice: Boolean
    ): MemoryProfile = when {
        lowMemory || lowRamDevice || totalMemBytes <= LOW_RAM_BYTES || heapMaxBytes <= MIN_HEAP_BYTES ->
            MemoryProfile.MINIMUM
        totalMemBytes <= STANDARD_RAM_BYTES -> MemoryProfile.STANDARD
        else -> MemoryProfile.FULL
    }

    /** Дисковый кэш не должен съедать heap на слабом железе. */
    fun effectiveCacheSize(profile: MemoryProfile, configuredBytes: Long): Long = when (profile) {
        MemoryProfile.MINIMUM -> minOf(configuredBytes, MIN_CACHE_BYTES)
        MemoryProfile.STANDARD -> minOf(configuredBytes, STANDARD_CACHE_BYTES)
        MemoryProfile.FULL -> configuredBytes
    }

    /**
     * true — нужно принудительно пересоздать сокет.
     *
     * Важно: без хотя бы одного pong watchdog НЕ срабатывает. Серверы без
     * player/pong не должны дёргаться каждые 60 секунд.
     */
    fun shouldForceReconnect(
        nowMs: Long,
        lastInboundAtMs: Long,
        hasSeenPong: Boolean,
        connected: Boolean
    ): Boolean {
        if (!connected) return true
        if (!hasSeenPong || lastInboundAtMs <= 0L) return false
        return nowMs - lastInboundAtMs > INBOUND_STALE_MS
    }
}
