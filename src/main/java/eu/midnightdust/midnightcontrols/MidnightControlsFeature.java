/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols;

import dev.lambdaurora.spruceui.util.Nameable;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a feature.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.1.0
 */
public class MidnightControlsFeature implements Nameable {
    private static final List<MidnightControlsFeature> FEATURES = new ArrayList<>();
    public static final MidnightControlsFeature FAST_BLOCK_PLACING = new MidnightControlsFeature("fast_block_placing", true, MidnightControlsConfig.fastBlockPlacing);
    public static final MidnightControlsFeature HORIZONTAL_REACHAROUND = new MidnightControlsFeature("horizontal_reacharound", true, MidnightControlsConfig.horizontalReacharound);
    public static final MidnightControlsFeature VERTICAL_REACHAROUND = new MidnightControlsFeature("vertical_reacharound", true, MidnightControlsConfig.verticalReacharound);

    private final String key;
    private final boolean defaultAllowed;
    private boolean allowed;
    private final boolean defaultEnabled;
    private boolean enabled;

    public MidnightControlsFeature(@NotNull String key, boolean allowed, boolean enabled) {
        Objects.requireNonNull(key, "Feature key cannot be null.");
        this.key = key;
        this.setAllowed(this.defaultAllowed = allowed);
        this.setEnabled(this.defaultEnabled = enabled);
    }

    public MidnightControlsFeature(@NotNull String key) {
        this(key, false, false);
    }

    /**
     * Allows the feature.
     */
    public void allow() {
        this.setAllowed(true);
    }

    /**
     * Returns whether this feature is allowed.
     *
     * @return {@code true} if this feature is allowed, else {@code false}
     */
    public boolean isAllowed() {
        return this.allowed;
    }

    /**
     * Sets whether this feature is allowed.
     *
     * @param allowed {@code true} if this feature is allowed, else {@code false}
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * Resets allowed state to default.
     */
    public void resetAllowed() {
        this.setAllowed(this.defaultAllowed);
    }

    /**
     * Returns whether this feature is enabled.
     *
     * @return {@code true} if this feature is enabled, else {@code false}
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Returns whether this feature is enabled.
     *
     * @param enabled {@code true} if this feature is enabled, else {@code false}
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether this feature is available or not.
     *
     * @return {@code true} if this feature is available, else {@code false}
     * @see #isAllowed()
     * @see #isEnabled()
     */
    public boolean isAvailable() {
        return this.isAllowed() && this.isEnabled();
    }

    /**
     * Resets the feature to its default values.
     */
    public void reset() {
        this.resetAllowed();
        this.setEnabled(this.defaultEnabled);
    }

    @Override
    public @NotNull String getName() {
        return this.key;
    }

    public static @NotNull Optional<MidnightControlsFeature> fromName(@NotNull String key) {
        Objects.requireNonNull(key, "Cannot find features with a null name.");
        return FEATURES.parallelStream().filter(feature -> feature.getName().equals(key)).findFirst();
    }

    /**
     * Resets all features to their default values.
     */
    public static void resetAll() {
        FEATURES.parallelStream().forEach(MidnightControlsFeature::reset);
    }

    /**
     * Resets all features to allow state.
     */
    public static void resetAllAllowed() {
        FEATURES.parallelStream().forEach(MidnightControlsFeature::resetAllowed);
    }

    static {
        FEATURES.add(FAST_BLOCK_PLACING);
        FEATURES.add(HORIZONTAL_REACHAROUND);
        FEATURES.add(VERTICAL_REACHAROUND);
    }
}
