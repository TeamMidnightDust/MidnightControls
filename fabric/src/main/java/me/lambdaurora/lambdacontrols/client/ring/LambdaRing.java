/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.ring;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a key binding ring.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.4.0
 */
public class LambdaRing
{
    public static final int            ELEMENT_SIZE = 50;
    private final       List<RingPage> pages        = new ArrayList<>(Collections.singletonList(new RingPage()));
    private             int            currentPage  = 0;

    public LambdaRing()
    {
    }

    public @NotNull RingPage getCurrentPage()
    {
        if (this.currentPage >= this.pages.size())
            this.currentPage = this.pages.size() - 1;
        else if (this.currentPage < 0)
            this.currentPage = 0;
        return this.pages.get(this.currentPage);
    }
}
