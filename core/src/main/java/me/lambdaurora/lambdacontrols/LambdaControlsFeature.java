/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a feature.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.1.0
 */
public class LambdaControlsFeature implements Nameable
{
    private static final List<LambdaControlsFeature> FEATURES            = new ArrayList<>();
    public static final  LambdaControlsFeature       FRONT_BLOCK_PLACING = new LambdaControlsFeature("front_block_placing", true, false);
    public static final  LambdaControlsFeature       FAST_BLOCK_PLACING  = new LambdaControlsFeature("fast_block_placing", true, true);

    private final String  key;
    private final boolean defaultAllowed;
    private       boolean allowed;
    private final boolean defaultEnabled;
    private       boolean enabled;

    public LambdaControlsFeature(@NotNull String key, boolean allowed, boolean enabled)
    {
        Objects.requireNonNull(key, "Feature key cannot be null.");
        this.key = key;
        this.setAllowed(this.defaultAllowed = allowed);
        this.setEnabled(this.defaultEnabled = enabled);
    }

    public LambdaControlsFeature(@NotNull String key)
    {
        this(key, false, false);
    }

    /**
     * Allows the feature.
     */
    public void allow()
    {
        this.setAllowed(true);
    }

    /**
     * Returns whether this feature is allowed.
     *
     * @return True if this feature is allowed, else false.
     */
    public boolean isAllowed()
    {
        return this.allowed;
    }

    /**
     * Sets whether this feature is allowed.
     *
     * @param allowed True if this feature is allowed, else false.
     */
    public void setAllowed(boolean allowed)
    {
        this.allowed = allowed;
    }

    /**
     * Resets allowed state to default.
     */
    public void resetAllowed()
    {
        this.setAllowed(this.defaultAllowed);
    }

    /**
     * Returns whether this feature is enabled.
     *
     * @return True if this feature is enabled, else false.
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * Returns whether this feature is enabled.
     *
     * @param enabled True if this feature is enabled, else false.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Returns whether this feature is available or not.
     *
     * @return True if this feature is available, else false.
     * @see #isAllowed()
     * @see #isEnabled()
     */
    public boolean isAvailable()
    {
        return this.isAllowed() && this.isEnabled();
    }

    /**
     * Resets the feature to its default values.
     */
    public void reset()
    {
        this.resetAllowed();
        this.setEnabled(this.defaultEnabled);
    }

    @Override
    public @NotNull String getName()
    {
        return this.key;
    }

    public static @NotNull Optional<LambdaControlsFeature> fromName(@NotNull String key)
    {
        Objects.requireNonNull(key, "Cannot find features with a null name.");
        return FEATURES.parallelStream().filter(feature -> feature.getName().equals(key)).findFirst();
    }

    /**
     * Resets all features to their default values.
     */
    public static void resetAll()
    {
        FEATURES.parallelStream().forEach(LambdaControlsFeature::reset);
    }

    /**
     * Resets all features to allow state.
     */
    public static void resetAllAllowed()
    {
        FEATURES.parallelStream().forEach(LambdaControlsFeature::resetAllowed);
    }

    static {
        FEATURES.add(FRONT_BLOCK_PLACING);
        FEATURES.add(FAST_BLOCK_PLACING);
    }
}
