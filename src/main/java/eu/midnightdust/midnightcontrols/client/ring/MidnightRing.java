/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.ring;

import com.electronwill.nightconfig.core.Config;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
    public static final int ELEMENT_SIZE = 50;

    private final Map<String, RingAction.Factory> actionFactories = new Object2ObjectOpenHashMap<>();
    private final List<RingPage> pages = new ArrayList<>(Collections.singletonList(RingPage.DEFAULT));
    private final MidnightControlsClient mod;
    private int currentPage = 0;

    public MidnightRing(@NotNull MidnightControlsClient mod) {
        this.mod = mod;
    }

    public void registerAction(@NotNull String name, @NotNull RingAction.Factory factory) {
        if (this.actionFactories.containsKey(name)) {
            this.mod.warn("Tried to register twice a ring action: \"" + name + "\".");
            return;
        }
        this.actionFactories.put(name, factory);
    }

    /**
     * Loads the ring from configuration.
     */
    public void load() {
//        List<Config> configPages = MidnightControlsConfig.ringPages;
//        if (configPages != null) {
//            this.pages.clear();
//            for (var configPage : configPages) {
//                RingPage.parseRingPage(configPage).ifPresent(this.pages::add);
//            }
//        }
//        if (this.pages.isEmpty()) {
//            this.pages.add(RingPage.DEFAULT);
//        }
    }

    public @NotNull RingPage getCurrentPage() {
        if (this.currentPage >= this.pages.size())
            this.currentPage = this.pages.size() - 1;
        else if (this.currentPage < 0)
            this.currentPage = 0;
        return this.pages.get(this.currentPage);
    }
}
