/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

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
    private final Consumer<LabelWidget> click_action;
    private final int                   x;
    private final int                   y;
    private final int                   max_width;
    //private final int                   max_height;
    private       String                text;
    public        boolean               visible;
    private       int                   width;
    private       int                   height;
    private       boolean               centered;

    public LabelWidget(int x, int y, @NotNull String text, int max_width, @NotNull Consumer<LabelWidget> click_action, boolean centered)
    {
        this.visible = true;
        this.x = x;
        this.y = y;
        this.max_width = max_width;
        this.click_action = click_action;
        this.centered = centered;
        this.set_text(text);
    }

    public LabelWidget(int x, int y, @NotNull String text, int max_width, @NotNull Consumer<LabelWidget> click_action)
    {
        this(x, y, text, max_width, click_action, false);
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
        if (width > this.max_width) {
            while (width > this.max_width) {
                text = text.substring(0, text.length() - 1);
                width = this.client.textRenderer.getStringWidth(text);
            }
        }

        this.text = text;
        this.width = width;
        this.height = this.client.textRenderer.fontHeight;
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

    @Override
    public void render(int mouse_x, int mouse_y, float delta)
    {
        if (this.centered)
            this.drawCenteredString(this.client.textRenderer, this.text, this.x, this.y, 10526880);
        else
            this.drawString(this.client.textRenderer, this.text, this.x, this.y, 10526880);
    }

    @Override
    public boolean mouseClicked(double mouse_x, double mouse_y, int button)
    {
        if (this.visible && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (mouse_x >= (double) this.x && mouse_y >= (double) this.y && mouse_x < (double) (this.x + this.width) && mouse_y < (double) (this.y + this.height)) {
                this.click_action.accept(this);
                return true;
            }
        }
        return false;
    }
}
