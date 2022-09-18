/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.ring;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a key binding ring.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.4.0
 */
public final class MidnightRing {
    public static final int ELEMENT_SIZE = 75;

    private final Map<String, RingAction.Factory> actionFactories = new Object2ObjectOpenHashMap<>();
    private final List<RingPage> pages = new ArrayList<>(Collections.singletonList(RingPage.DEFAULT));
    private final MidnightControlsClient mod;
    private int currentPage = 0;

    public MidnightRing(@NotNull MidnightControlsClient mod) {
        this.mod = mod;
    }

    public void registerAction(@NotNull String name, @NotNull RingAction.Factory factory) {
        if (this.actionFactories.containsKey(name)) {
            this.mod.warn("Tried to register a ring action twice: \"" + name + "\".");
            return;
        }
        this.actionFactories.put(name, factory);
    }

    /**
     * Loads the ring from configuration.
     */
    public void loadFromConfig() {
        List<String> configBindings = MidnightControlsConfig.ringBindings;
        if (configBindings != null) {
            this.pages.clear();
            int bindingIndex = 0;
            for (int i = 0; i < MathHelper.ceil(configBindings.size() / 8f); ++i) {
                this.pages.add(new RingPage(i+1 + " / " + MathHelper.ceil(configBindings.size() / 8f)));
            }

            for (String binding : configBindings) {
                ButtonBinding buttonBinding = InputManager.getBinding(binding);
                if (buttonBinding != null) {
                    RingPage page = this.pages.get(MathHelper.fastFloor(bindingIndex / 8f));
                    page.actions[bindingIndex - 8 * (MathHelper.fastFloor(bindingIndex / 8f))] = (new ButtonBindingRingAction(buttonBinding));
                    ++bindingIndex;
                }
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add(RingPage.DEFAULT);
        }
    }
    /**
     * Loads the ring from all unbound keys.
     */
    public void loadFromUnbound() {
        List<ButtonBinding> unboundBindings = InputManager.getUnboundBindings();
        if (unboundBindings != null) {
            this.pages.clear();
            int bindingIndex = 0;
            for (int i = 0; i < MathHelper.ceil(unboundBindings.size() / 8f); ++i) {
                this.pages.add(new RingPage(i+1 + " / " + MathHelper.ceil(unboundBindings.size() / 8f)));
            }

            for (ButtonBinding buttonBinding : unboundBindings) {
                if (buttonBinding != null) {
                    RingPage page = this.pages.get(MathHelper.fastFloor(bindingIndex / 8f));
                    page.actions[bindingIndex - 8 * (MathHelper.fastFloor(bindingIndex / 8f))] = (new ButtonBindingRingAction(buttonBinding));
                    ++bindingIndex;
                }
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add(RingPage.DEFAULT);
        }
    }
    public int getMaxPages() {
        return this.pages.size();
    }

    public @NotNull RingPage getCurrentPage() {
        if (this.currentPage >= this.pages.size())
            this.currentPage = this.pages.size() - 1;
        else if (this.currentPage < 0)
            this.currentPage = 0;
        return this.pages.get(this.currentPage);
    }
    public void cyclePage(boolean forwards) {
        if (forwards) {
            if (currentPage < pages.size()-1) ++currentPage;
            else currentPage = 0;
        } else {
            if (currentPage > 0) --currentPage;
            else currentPage = pages.size();
        }
    }
}
