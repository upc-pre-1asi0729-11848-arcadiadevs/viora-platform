package com.arcadiadevs.viora.platform.agronomic.domain.model.services;

import com.arcadiadevs.viora.platform.agronomic.domain.model.aggregates.Plot;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirement;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.ChillRequirementPolicy;
import org.springframework.stereotype.Service;

/**
 * Domain service that resolves a plot's winter-chill requirement.
 *
 * <p>
 * The requirement is a property of the crop (and, in the future, variety), so it
 * is derived from the plot's crop type against the configured
 * {@link ChillRequirementPolicy}. The single value it returns is the one shared
 * by the trend chart's reference line and the yield-forecast chill modifier,
 * removing the previous split between an unrealistic 600-portion chart threshold
 * and a separate 40-portion yield requirement.
 * </p>
 */
@Service
public class ChillRequirementResolver {

    private final ChillRequirementPolicy policy;

    public ChillRequirementResolver(ChillRequirementPolicy policy) {
        this.policy = policy;
    }

    /**
     * Resolves the chill requirement for a plot, preferring an explicit
     * grower/agronomist declaration over the crop-derived system default.
     *
     * @param plot The plot whose requirement is resolved.
     * @return The chill requirement with its provenance and model.
     */
    public ChillRequirement resolveFor(Plot plot) {
        if (plot != null && plot.getChillRequirementOverride() != null) {
            return plot.getChillRequirementOverride();
        }
        String cropType = plot == null ? null : plot.getCropType();
        return policy.resolveFor(cropType);
    }

    /** Resolves the neutral default requirement when no plot context applies. */
    public ChillRequirement resolveDefault() {
        return policy.resolveFor(null);
    }
}
