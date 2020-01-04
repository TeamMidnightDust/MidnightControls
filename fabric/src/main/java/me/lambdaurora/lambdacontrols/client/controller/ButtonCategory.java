/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.controller;

import net.minecraft.client.resource.language.I18n;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a button binding category
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class ButtonCategory implements Identifiable
{
    private final List<ButtonBinding> bindings = new ArrayList<>();
    private final Identifier          id;
    private       int                 priority;

    public ButtonCategory(@NotNull Identifier id, int priority)
    {
        this.id = id;
        this.priority = priority;
    }

    public ButtonCategory(@NotNull Identifier id)
    {
        this(id, 100);
    }

    public void register_binding(@NotNull ButtonBinding binding)
    {
        if (this.bindings.contains(binding))
            throw new IllegalStateException("Cannot register twice a button binding in the same category.");
        this.bindings.add(binding);
    }

    public void register_all_bindings(@NotNull ButtonBinding... bindings)
    {
        this.register_all_bindings(Arrays.asList(bindings));
    }

    public void register_all_bindings(@NotNull List<ButtonBinding> bindings)
    {
        bindings.forEach(this::register_binding);
    }

    /**
     * Gets the bindings assigned to this category.
     *
     * @return The bindings assigned to this category.
     */
    public @NotNull List<ButtonBinding> get_bindings()
    {
        return Collections.unmodifiableList(this.bindings);
    }

    /**
     * Gets the translated name of this category.
     * <p>
     * The translation key should be `modid.identifier_name`.
     *
     * @return The translated name.
     */
    public @NotNull String get_translated_name()
    {
        if (this.id.get_namespace().equals("minecraft"))
            return I18n.translate(this.id.get_name());
        else
            return I18n.translate(this.id.get_namespace() + "." + this.id.get_name());
    }

    /**
     * Gets the priority display of this category.
     * It will defines in which order the categories will display on the controls screen.
     *
     * @return The priority of this category.
     */
    public int get_priority()
    {
        return this.priority;
    }

    @Override
    public @NotNull Identifier get_identifier()
    {
        return this.id;
    }
}
