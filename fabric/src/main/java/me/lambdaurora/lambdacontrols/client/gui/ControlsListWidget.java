/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.ButtonCategory;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
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
    private static final int[]                    UNBOUND = new int[0];
    private final        ControllerControlsScreen gui;
    private              int                      field_2733;

    public ControlsListWidget(@NotNull ControllerControlsScreen gui, @NotNull MinecraftClient client)
    {
        super(client, gui.width + 45, gui.height, 43, gui.height - 32, 24);
        this.gui = gui;

        InputManager.streamCategories()
                .sorted(Comparator.comparingInt(ButtonCategory::getPriority))
                .forEach(category -> {
                    this.addEntry(new CategoryEntry(category));

                    category.getBindings().forEach(binding -> {
                        int i = client.textRenderer.getStringWidth(I18n.translate(binding.getTranslationKey()));
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
        private final String                 bindingName;
        private final ControllerButtonWidget editButton;
        private final ButtonWidget           resetButton;
        private final ButtonWidget           unboundButton;

        ButtonBindingEntry(@NotNull ButtonBinding binding)
        {
            this.binding = binding;
            this.bindingName = I18n.translate(this.binding.getTranslationKey());
            this.editButton = new ControllerButtonWidget(0, 0, 110, this.binding, btn -> {
                gui.focusedBinding = binding;
                gui.currentButtons.clear();
                gui.waiting = true;
            })
            {
                protected String getNarrationMessage()
                {
                    return binding.isNotBound() ? I18n.translate("narrator.controls.unbound", bindingName) : I18n.translate("narrator.controls.bound", bindingName, super.getNarrationMessage());
                }
            };
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"),
                    btn -> gui.mod.config.setButtonBinding(binding, binding.getDefaultButton()))
            {
                protected String getNarrationMessage()
                {
                    return I18n.translate("narrator.controls.reset", bindingName);
                }
            };
            this.unboundButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("lambdacontrols.menu.unbound"),
                    btn -> {
                        gui.mod.config.setButtonBinding(binding, UNBOUND);
                        gui.focusedBinding = null;
                    })
            {
                protected String getNarrationMessage()
                {
                    return I18n.translate("lambdacontrols.narrator.unbound", bindingName);
                }
            };
        }

        @Override
        public List<? extends Element> children()
        {
            return Collections.unmodifiableList(Arrays.asList(this.editButton, this.resetButton));
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta)
        {
            boolean focused = gui.focusedBinding == this.binding;
            TextRenderer textRenderer = ControlsListWidget.this.minecraft.textRenderer;
            String bindingName = this.bindingName;
            float var10002 = (float) (x + 70 - ControlsListWidget.this.field_2733);
            int var10003 = y + height / 2;
            textRenderer.draw(bindingName, var10002, (float) (var10003 - 9 / 2), 16777215);
            this.resetButton.x = this.unboundButton.x = x + 190;
            this.resetButton.y = this.unboundButton.y = y;
            this.resetButton.active = !this.binding.isDefault();
            if (focused)
                this.unboundButton.render(mouseX, mouseY, delta);
            else
                this.resetButton.render(mouseX, mouseY, delta);
            this.editButton.x = x + 75;
            this.editButton.y = y;
            this.editButton.update();

            if (focused) {
                this.editButton.setMessage(Formatting.WHITE + "> " + Formatting.YELLOW + this.editButton.getMessage() + Formatting.WHITE + " <");
            } else if (!this.binding.isNotBound() && InputManager.hasDuplicatedBindings(this.binding)) {
                this.editButton.setMessage(Formatting.RED + this.editButton.getMessage());
            } else if (this.binding.isNotBound()) {
                this.editButton.setMessage(Formatting.GOLD + this.editButton.getMessage());
            }

            this.editButton.render(mouseX, mouseY, delta);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            boolean focused = gui.focusedBinding == this.binding;
            if (this.editButton.mouseClicked(mouseX, mouseY, button))
                return true;
            else
                return focused ? this.unboundButton.mouseClicked(mouseX, mouseY, button) : this.resetButton.mouseClicked(mouseX, mouseY, button);
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button)
        {
            return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button)
                    || this.unboundButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    public class CategoryEntry extends Entry
    {
        private final String name;
        private final int    nameWidth;

        public CategoryEntry(@NotNull ButtonCategory category)
        {
            this.name = category.getTranslatedName();
            this.nameWidth = ControlsListWidget.this.minecraft.textRenderer.getStringWidth(this.name);
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta)
        {
            ControlsListWidget.this.minecraft.textRenderer.draw(this.name, (float) (ControlsListWidget.this.minecraft.currentScreen.width / 2 - this.nameWidth / 2),
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
