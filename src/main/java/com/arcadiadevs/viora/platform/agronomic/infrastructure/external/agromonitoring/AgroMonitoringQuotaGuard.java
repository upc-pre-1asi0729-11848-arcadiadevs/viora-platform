package com.arcadiadevs.viora.platform.agronomic.infrastructure.external.agromonitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Account-wide quota guard for the AgroMonitoring integration.
 *
 * <p>
 * AgroMonitoring rate-limits per account, so a quota rejection (HTTP 429) on any
 * endpoint means subsequent calls will also be rejected. When that happens the
 * guard opens a cooldown window during which callers short-circuit instead of
 * hammering the rate-limited API, and report the source as
 * {@code QUOTA_EXCEEDED}. The window closes automatically once it elapses.
 * </p>
 */
@Component
@Slf4j
public class AgroMonitoringQuotaGuard {

    private final Duration cooldown;
    private final AtomicReference<Instant> cooldownUntil = new AtomicReference<>(Instant.EPOCH);

    public AgroMonitoringQuotaGuard(AgroMonitoringProperties properties) {
        this.cooldown = Duration.ofMinutes(Math.max(0, properties.getQuotaCooldownMinutes()));
    }

    /**
     * @return True while the quota cooldown window is still open.
     */
    public boolean isQuotaExhausted() {
        return Instant.now().isBefore(cooldownUntil.get());
    }

    /**
     * Opens (or extends) the cooldown window after a provider quota rejection.
     */
    public void recordQuotaExceeded() {
        if (cooldown.isZero()) {
            return;
        }
        var until = Instant.now().plus(cooldown);
        cooldownUntil.set(until);
        log.warn("AgroMonitoring quota exhausted; pausing provider calls until {}.", until);
    }
}
