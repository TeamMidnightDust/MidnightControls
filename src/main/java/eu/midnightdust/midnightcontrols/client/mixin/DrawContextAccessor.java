/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
@Mixin(GuiGraphicsExtractor.class)
public interface DrawContextAccessor {
    @Accessor("guiRenderState")
    GuiRenderState getState();

    //? if fabric {
    @Accessor("scissorStack")
    //~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
    GuiGraphicsExtractor.ScissorStack getScissorStack();
    //?}
}
