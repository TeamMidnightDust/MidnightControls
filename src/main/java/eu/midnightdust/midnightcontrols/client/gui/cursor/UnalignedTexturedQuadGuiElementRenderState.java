package eu.midnightdust.midnightcontrols.client.gui.cursor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record UnalignedTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x1, float y1, float x2, float y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
    public UnalignedTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x1, float y1, float x2, float y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRectangle scissorArea) {
        this(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices) {
        vertices.addVertexWith2DPose(pose(), x1(), y1()).setUv(u1(), v1()).setColor(color());
        vertices.addVertexWith2DPose(pose(), x1(), y2()).setUv(u1(), v2()).setColor(color());
        vertices.addVertexWith2DPose(pose(), x2(), y2()).setUv(u2(), v2()).setColor(color());
        vertices.addVertexWith2DPose(pose(), x2(), y1()).setUv(u2(), v1()).setColor(color());
    }

    @Nullable
    private static ScreenRectangle createBounds(float x1, float y1, float x2, float y2, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle screenRect = (new ScreenRectangle((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1))).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
