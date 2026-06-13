package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Small thread-safe time-to-live cache.
 *
 * <p>
 * Used to shield the rate-limited AgroMonitoring account from repeated identical
 * weather requests (e.g. dashboard refreshes). A non-positive TTL disables
 * caching entirely.
 * </p>
 *
 * @param <V> Cached value type.
 */
class ExpiringCache<V> {

    private final Duration ttl;
    private final Map<String, Entry<V>> entries = new ConcurrentHashMap<>();

    ExpiringCache(Duration ttl) {
        this.ttl = ttl;
    }

    Optional<V> get(String key) {
        if (ttl.isZero() || ttl.isNegative()) {
            return Optional.empty();
        }
        var entry = entries.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(entry.expiresAt())) {
            entries.remove(key, entry);
            return Optional.empty();
        }
        return Optional.of(entry.value());
    }

    void put(String key, V value) {
        if (ttl.isZero() || ttl.isNegative()) {
            return;
        }
        entries.put(key, new Entry<>(value, Instant.now().plus(ttl)));
    }

    private record Entry<V>(V value, Instant expiresAt) {
    }
}
