/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.ButtonBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a control list widget.
 */
public class ControlsListWidget extends ElementListWidget<ControlsListWidget.Entry>
{
    private final LambdaControlsControlsScreen gui;
    private       int                          field_2733;

    public ControlsListWidget(@NotNull LambdaControlsControlsScreen gui, @NotNull MinecraftClient client)
    {
        super(client, gui.width + 45, gui.height, 43, gui.height - 32, 24);
        this.gui = gui;

        ButtonBinding.stream_categories()
                .sorted(Comparator.comparingInt(ButtonBinding.Category::get_priority))
                .forEach(category -> {
                    this.addEntry(new CategoryEntry(category));

                    category.get_bindings().forEach(binding -> {
                        int i = client.textRenderer.getStringWidth(I18n.translate(binding.get_translation_key()));
                        if (i > this.field_2733) {
                            this.field_2733 = i;
                        }

                        this.addEntry(new ControlsListWidget.ButtonBindingEntry(binding));
                    });
                });
    }

    @Override
    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 15;
    }

    @Override
    public int getRowWidth()
    {
        return super.getRowWidth() + 32;
    }

    public class ButtonBindingEntry extends Entry
    {
        private final ButtonBinding          binding;
        private final String                 binding_name;
        private final ControllerButtonWidget edit_button;
        private final ButtonWidget           reset_button;

        ButtonBindingEntry(@NotNull ButtonBinding binding)
        {
            this.binding = binding;
            this.binding_name = I18n.translate(this.binding.get_translation_key());
            this.edit_button = new ControllerButtonWidget(0, 0, 90, this.binding, btn -> gui.focused_binding = binding)
            {
                protected String getNarrationMessage()
                {
                    return binding.is_not_bound() ? I18n.translate("narrator.controls.unbound", binding_name) : I18n.translate("narrator.controls.bound", binding_name, super.getNarrationMessage());
                }
            };
            this.reset_button = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"),
                    btn -> gui.mod.config.set_button_binding(binding, binding.get_default_button()))
            {
                protected String getNarrationMessage()
                {
                    return I18n.translate("narrator.controls.reset", binding_name);
                }
            };
        }

        @Override
        public List<? extends Element> children()
        {
            return Collections.unmodifiableList(Arrays.asList(this.edit_button, this.reset_button));
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouse_x, int mouse_y, boolean hovering, float delta)
        {
            boolean focused = gui.focused_binding == this.binding;
            TextRenderer text_renderer = ControlsListWidget.this.minecraft.textRenderer;
            String binding_name = this.binding_name;
            float var10002 = (float) (x + 70 - ControlsListWidget.this.field_2733);
            int var10003 = y + height / 2;
            text_renderer.draw(binding_name, var10002, (float) (var10003 - 9 / 2), 16777215);
            this.reset_button.x = x + 190;
            this.reset_button.y = y;
            this.reset_button.active = !this.binding.is_default();
            this.reset_button.render(mouse_x, mouse_y, delta);
            this.edit_button.x = x + 85;
            this.edit_button.y = y;
            this.edit_button.update();

            if (focused) {
                this.edit_button.setMessage(Formatting.WHITE + "> " + Formatting.YELLOW + this.edit_button.getMessage() + Formatting.WHITE + " <");
            } else if (!this.binding.is_not_bound() && ButtonBinding.has_duplicates(this.binding.get_button())) {
                this.edit_button.setMessage(Formatting.RED + this.edit_button.getMessage());
            } else if (this.binding.is_not_bound()) {
                this.edit_button.setMessage(Formatting.GOLD + edit_button.getMessage());
            }

            this.edit_button.render(mouse_x, mouse_y, delta);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (this.edit_button.mouseClicked(mouseX, mouseY, button))
                return true;
            else
                return this.reset_button.mouseClicked(mouseX, mouseY, button);
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button)
        {
            return this.edit_button.mouseReleased(mouseX, mouseY, button) || this.reset_button.mouseReleased(mouseX, mouseY, button);
        }
    }

    public class CategoryEntry extends Entry
    {
        private final String name;
        private final int    name_width;

        public CategoryEntry(@NotNull ButtonBinding.Category category)
        {
            this.name = category.get_translated_name();
            this.name_width = ControlsListWidget.this.minecraft.textRenderer.getStringWidth(this.name);
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouse_x, int mouse_y, boolean hovering, float delta)
        {
            ControlsListWidget.this.minecraft.textRenderer.draw(this.name, (float) (ControlsListWidget.this.minecraft.currentScreen.width / 2 - this.name_width / 2),
                    (float) ((y + height) - 9 - 1), 16777215);
        }

        @Override
        public boolean changeFocus(boolean bl)
        {
            return false;
        }

        @Override
        public List<? extends Element> children()
        {
            return Collections.emptyList();
        }
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.Entry<Entry>
    {
    }
}
