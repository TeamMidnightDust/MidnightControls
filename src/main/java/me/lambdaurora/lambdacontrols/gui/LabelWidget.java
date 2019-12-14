/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a label widget.
 */
// @TODO move this to a GUI library.
public class LabelWidget extends DrawableHelper implements Element, Drawable
{
    public static final Consumer<LabelWidget> DEFAULT_ACTION = label -> {
    };

    private final MinecraftClient       client = MinecraftClient.getInstance();
    private final Consumer<LabelWidget> press_action;
    private final int                   x;
    private final int                   y;
    private final int                   max_width;
    //private final int                   max_height;
    private       String                text;
    private       String                tooltip_text;
    public        boolean               visible;
    private       int                   width;
    private       int                   height;
    private       boolean               centered;
    protected     boolean               hovered;
    protected     boolean               focused;

    public LabelWidget(int x, int y, @NotNull String text, int max_width, @NotNull Consumer<LabelWidget> press_action, boolean centered)
    {
        this.visible = true;
        this.x = x;
        this.y = y;
        this.max_width = max_width;
        this.press_action = press_action;
        this.centered = centered;
        this.set_text(text);
    }

    public LabelWidget(int x, int y, @NotNull String text, int max_width, @NotNull Consumer<LabelWidget> press_action)
    {
        this(x, y, text, max_width, press_action, false);
    }

    public LabelWidget(int x, int y, @NotNull String text, int max_width, boolean centered)
    {
        this(x, y, text, max_width, DEFAULT_ACTION, centered);
    }

    public LabelWidget(int x, int y, @NotNull String text, int max_width)
    {
        this(x, y, text, max_width, DEFAULT_ACTION);
    }

    /**
     * Sets the text of this label.
     *
     * @param text The text to set.
     */
    public void set_text(@NotNull String text)
    {
        int width = this.client.textRenderer.getStringWidth(text);
        while (width > this.max_width) {
            text = text.substring(0, text.length() - 1);
            width = this.client.textRenderer.getStringWidth(text);
        }

        this.text = text;
        this.width = width;
        this.height = this.client.textRenderer.fontHeight;
    }

    /**
     * Sets the tooltip text of this label.
     *
     * @param text The tooltip text.
     */
    public void set_tooltip_text(String text)
    {
        this.tooltip_text = text;
    }

    /**
     * Gets the width of this label widget.
     *
     * @return The width of this label widget.
     */
    public int get_width()
    {
        return this.width;
    }

    /**
     * Gets the height of this label widget.
     *
     * @return The height of this label widget.
     */
    public int get_height()
    {
        return this.height;
    }

    /**
     * Fires the press event on this label widget.
     */
    public void on_press()
    {
        this.press_action.accept(this);
    }

    @Override
    public void render(int mouse_x, int mouse_y, float delta)
    {
        if (this.visible) {
            int x = this.centered ? this.x - this.client.textRenderer.getStringWidth(this.text) / 2 : this.x;
            this.hovered = mouse_x >= x && mouse_y >= this.y && mouse_x < x + this.width && mouse_y < this.y + this.height;
            this.drawString(this.client.textRenderer, this.text, x, this.y, 10526880);

            if (this.tooltip_text != null && !this.tooltip_text.isEmpty()) {
                List<String> wrapped_tooltip_text = this.client.textRenderer.wrapStringToWidthAsList(this.tooltip_text, Math.max(this.width / 2, 200));
                if (this.hovered)
                    this.render_tooltip(wrapped_tooltip_text, mouse_x, mouse_y);
                else if (this.focused)
                    this.render_tooltip(wrapped_tooltip_text, this.x - 12, this.y);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouse_x, double mouse_y, int button)
    {
        if (this.visible && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (this.hovered) {
                this.on_press();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean changeFocus(boolean down)
    {
        if (this.visible) {
            this.focused = !this.focused;
            return this.focused;
        } else {
            return false;
        }
    }

    public void render_tooltip(List<String> text, int x, int y)
    {
        if (!text.isEmpty()) {
            RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            int i = 0;

            for (String string : text) {
                int j = this.client.textRenderer.getStringWidth(string);
                if (j > i) {
                    i = j;
                }
            }

            int k = x + 12;
            int l = y - 12;
            int n = 8;
            if (text.size() > 1) {
                n += 2 + (text.size() - 1) * 10;
            }

            if (k + i > this.client.getWindow().getScaledWidth()) {
                k -= 28 + i;
            }

            if (l + n + 6 > this.client.getWindow().getScaledHeight()) {
                l = this.client.getWindow().getScaledHeight() - n - 6;
            }

            this.setBlitOffset(300);
            this.client.getItemRenderer().zOffset = 300.0F;
            this.fillGradient(k - 3, l - 4, k + i + 3, l - 3, -267386864, -267386864);
            this.fillGradient(k - 3, l + n + 3, k + i + 3, l + n + 4, -267386864, -267386864);
            this.fillGradient(k - 3, l - 3, k + i + 3, l + n + 3, -267386864, -267386864);
            this.fillGradient(k - 4, l - 3, k - 3, l + n + 3, -267386864, -267386864);
            this.fillGradient(k + i + 3, l - 3, k + i + 4, l + n + 3, -267386864, -267386864);
            this.fillGradient(k - 3, l - 3 + 1, k - 3 + 1, l + n + 3 - 1, 1347420415, 1344798847);
            this.fillGradient(k + i + 2, l - 3 + 1, k + i + 3, l + n + 3 - 1, 1347420415, 1344798847);
            this.fillGradient(k - 3, l - 3, k + i + 3, l - 3 + 1, 1347420415, 1347420415);
            this.fillGradient(k - 3, l + n + 2, k + i + 3, l + n + 3, 1344798847, 1344798847);
            MatrixStack matrix_stack = new MatrixStack();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrix_stack.translate(0.0D, 0.0D, this.client.getItemRenderer().zOffset);
            Matrix4f matrix4f = matrix_stack.peek().getModel();

            for (int r = 0; r < text.size(); ++r) {
                String string2 = text.get(r);
                if (string2 != null) {
                    this.client.textRenderer.draw(string2, (float) k, (float) l, -1, true, matrix4f, immediate, false, 0, 15728880);
                }

                if (r == 0) {
                    l += 2;
                }

                l += 10;
            }

            immediate.draw();
            this.setBlitOffset(0);
            this.client.getItemRenderer().zOffset = 0.0F;
            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }
}
