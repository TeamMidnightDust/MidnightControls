package eu.midnightdust.midnightcontrols.client.gui.cursor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.cursor.CursorType;
import eu.midnightdust.midnightcontrols.client.mixin.DrawContextAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public abstract class CursorRenderer {
    public static CursorType currentCursorStyle = CursorType.DEFAULT;

    public abstract void renderCursor(@NotNull GuiGraphicsExtractor context, @NotNull Minecraft client);

    public static void drawUnalignedTexturedQuad(RenderPipeline pipeline, Identifier texture, GuiGraphicsExtractor context, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2) {
        DrawContextAccessor accessor = (DrawContextAccessor) context;
        var tex = client.getTextureManager().getTexture(texture);
        //~ if >= 26.1 '.submitGuiElement(' -> '.addGuiElement('
        accessor.getState().addGuiElement(new UnalignedTexturedQuadGuiElementRenderState(pipeline, TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()), new Matrix3x2f(context.pose()), x1, y1, x2, y2, u1, u2, v1, v2, 0xffffffff, /*? fabric {*/ accessor.getScissorStack().peek() /*?} else {*/ /*context.peekScissorStack()*//*?}*/));
    }
}
