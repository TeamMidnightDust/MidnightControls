package eu.midnightdust.midnightcontrols.client.gui.cursor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import eu.midnightdust.midnightcontrols.client.mixin.DrawContextAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.Cursor;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public abstract class CursorRenderer {
    public static Cursor currentCursorStyle = Cursor.DEFAULT;

    public abstract void renderCursor(@NotNull DrawContext context, @NotNull MinecraftClient client);

    public static void drawUnalignedTexturedQuad(RenderPipeline pipeline, Identifier texture, DrawContext context, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2) {
        DrawContextAccessor accessor = (DrawContextAccessor) context;
        accessor.getState().addSimpleElement(new UnalignedTexturedQuadGuiElementRenderState(pipeline, TextureSetup.withoutGlTexture(client.getTextureManager().getTexture(texture).getGlTextureView()), new Matrix3x2f(context.getMatrices()), x1, y1, x2, y2, u1, u2, v1, v2, 0xffffffff, accessor.getScissorStack().peekLast()));
    }
}
