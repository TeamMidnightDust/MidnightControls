/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.navigation.NavigationEvent;
import dev.lambdaurora.spruceui.navigation.NavigationUtils;
import dev.lambdaurora.spruceui.render.SpruceGuiGraphics;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceSeparatorWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceParentWidget;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a control list widget.
 */
public class ControlsListWidget extends SpruceEntryListWidget<ControlsListWidget.Entry> {
    private static final int[] UNBOUND = new int[]{-1};
    private final ControllerControlsWidget gui;
    protected int lastIndex = 0;
    private final int maxTextLength;

    public ControlsListWidget(Position position, int width, int height, ControllerControlsWidget gui) {
        super(position, width, height, 4, ControlsListWidget.Entry.class);
        this.gui = gui;
        this.maxTextLength = InputManager.streamBindings().mapToInt(binding -> this.client.font.width(binding.getText())).max().orElse(0);

        InputManager.streamCategories()
                .sorted(Comparator.comparingInt(ButtonCategory::getPriority))
                .forEach(category -> {
                    this.addEntry(new CategoryEntry(this, category));

                    category.getBindings().forEach(binding -> {
                        this.addEntry(new ControlsListWidget.ButtonBindingEntry(this, binding));
                    });
                });

        this.setAllowOutsideHorizontalNavigation(true);
    }

    private int getRowWidth() {
        return this.getWidth() - 6 - this.getRowLeft() * 2;
    }

    public int getRowLeft() {
        int baseWidth = 220 + 32;
        return this.getWidth() / 2 - baseWidth / 2 + 72 - this.maxTextLength;
    }

    public class ButtonBindingEntry extends eu.midnightdust.midnightcontrols.client.gui.widget.ControlsListWidget.Entry implements SpruceParentWidget<SpruceWidget> {
        private final List<SpruceWidget> children = new ArrayList<>();
        private @Nullable SpruceWidget focused;
        private final ButtonBinding binding;
        private final String bindingName;
        private final ControllerButtonWidget editButton;
        private final SpruceButtonWidget resetButton;
        private final SpruceButtonWidget unbindButton;

        ButtonBindingEntry(@NotNull ControlsListWidget parent, @NotNull ButtonBinding binding) {
            super(parent);
            this.binding = binding;
            this.bindingName = I18n.get(this.binding.getTranslationKey());
            this.editButton = new ControllerButtonWidget(Position.of(this, parent.getWidth() / 2 - 8, 0), 120, this.binding, btn -> {
                gui.focusedBinding = binding;
                MidnightControlsClient.input.beginControlsInput(gui);
            }) {
                protected Component getNarrationMessage() {
                    return binding.isNotBound() ? Component.translatable("narrator.controls.unbound", bindingName)
                            : Component.translatable("narrator.controls.bound", bindingName, super.getNarrationMessage());
                }
            };
            this.children.add(editButton);
            this.resetButton = new SpruceIconButtonWidget(Position.of(this,
                    this.editButton.getPosition().getRelativeX() + this.editButton.getWidth() + 2, 0),
                    37, 20, Component.empty(),
                    btn -> MidnightControlsConfig.setButtonBinding(binding, binding.getDefaultButton())) {
                protected Component getNarrationMessage() {
                    return Component.translatable("narrator.controls.reset", bindingName);
                }

                private final Identifier resetTexture = Identifier.fromNamespaceAndPath("midnightlib","icon/reset");

                @Override
                protected int renderIcon(SpruceGuiGraphics context, int mouseX, int mouseY, float delta) {
                    int size = 12;
                    int x = this.getX() + this.getWidth() / 2 - size / 2;
                    int y = this.getY() + this.getHeight() / 2 - size / 2;
                    context.vanilla().blitSprite(RenderPipelines.GUI_TEXTURED, resetTexture, x, y, size, size);
                    return 1;
                }
            };
            this.children.add(this.resetButton);
            this.unbindButton = new SpruceButtonWidget(Position.of(this,
                    this.editButton.getPosition().getRelativeX() + this.editButton.getWidth() + 2, 0),
                    this.resetButton.getWidth(), this.resetButton.getHeight(), SpruceTexts.GUI_UNBIND,
                    btn -> {
                        MidnightControlsConfig.setButtonBinding(binding, UNBOUND);
                        gui.focusedBinding = null;
                        MidnightControlsClient.input.beginControlsInput(null);
                    }) {
                protected Component getNarrationMessage() {
                    return Component.translatable("midnightcontrols.narrator.unbound", bindingName);
                }
            };
            this.children.add(this.unbindButton);

            this.position.setRelativeX(4);
            this.width -= 10;
        }

        @Override
        public List<SpruceWidget> children() {
            return this.children;
        }

        @Override
        public @Nullable SpruceWidget getFocused() {
            return this.focused;
        }

        @Override
        public void setFocused(@Nullable SpruceWidget focused) {
            if (this.focused == focused)
                return;
            if (this.focused != null)
                this.focused.setFocused(false);
            this.focused = focused;
        }

        @Override
        public int getHeight() {
            return this.children.stream().mapToInt(SpruceWidget::getHeight).reduce(Integer::max).orElse(0) + 4;
        }

        /* Input */

        @Override
        protected boolean onMouseClick(MouseButtonEvent click, boolean doubleClick) {
            var it = this.children().iterator();

            SpruceWidget element;
            do {
                if (!it.hasNext()) {
                    return false;
                }

                element = it.next();
            } while (!element.mouseClicked(click, false));

            this.setFocused(element);
            if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1)
                this.dragging = true;

            return true;
        }

        @Override
        protected boolean onMouseRelease(MouseButtonEvent click) {
            this.dragging = false;
            return this.hoveredElement(click.x(), click.y()).filter(element -> element.mouseReleased(click)).isPresent();
        }

        @Override
        protected boolean onMouseDrag(@NotNull MouseButtonEvent click, double deltaX, double deltaY) {
            return this.getFocused() != null && this.dragging && click.button() == GLFW.GLFW_MOUSE_BUTTON_1
                    && this.getFocused().mouseDragged(click, deltaX, deltaY);
        }

        @Override
        protected boolean onKeyPress(@NotNull KeyEvent input) {
            return this.focused != null && this.focused.keyPressed(input);
        }

        /* Navigation */

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                this.setFocused(null);
            }
        }

        @Override
        public boolean onNavigation(NavigationEvent event) {
            if (this.requiresCursor()) return false;
            if (!event.tab() && event.direction().getAxis() == ScreenAxis.VERTICAL) {
                if (this.isFocused()) {
                    this.setFocused(null);
                    return false;
                }
                int lastIndex = this.parent.lastIndex;
                if (lastIndex >= this.children.size())
                    lastIndex = this.children.size() - 1;
                if (!this.children.get(lastIndex).onNavigation(event))
                    return false;
                this.setFocused(this.children.get(lastIndex));
                return true;
            }

            boolean result = NavigationUtils.tryNavigate(event, this.children, this.focused, this::setFocused, true);
            if (result) {
                this.setFocused(true);
                if (event.direction().getAxis() == ScreenAxis.HORIZONTAL && this.getFocused() != null) {
                    this.parent.lastIndex = this.children.indexOf(this.getFocused());
                }
            }
            return result;
        }

        /* Rendering */

        @Override
        protected void extractWidgetRenderState(SpruceGuiGraphics context, int mouseX, int mouseY, float delta) {
            boolean focused = gui.focusedBinding == this.binding;

            var textRenderer = ControlsListWidget.this.client.font;
            int height = this.getHeight();
            //float textX = (float) (this.getX() + 70 - ControlsListWidget.this.maxTextLength);
            int textY = this.getY() + height / 2;
            //~ if >= 26.1 '.drawText(' -> '.text('
            context.text(textRenderer, this.bindingName, this.getX(), (textY - 9 / 2), 0xFFFFFFFF, true);

            this.resetButton.setVisible(!focused);
            this.unbindButton.setVisible(focused);
            this.resetButton.setActive(!this.binding.isDefault());

            this.editButton.update();
            if (focused) {
                var text = Component.literal("> ").withStyle(ChatFormatting.WHITE);
                text.append(this.editButton.getMessage().copy().withStyle(ChatFormatting.YELLOW));
                this.editButton.setMessage(text.append(Component.literal(" <").withStyle(ChatFormatting.WHITE)));
            } else if (!this.binding.isNotBound() && InputManager.hasDuplicatedBindings(this.binding)) {
                var text = this.editButton.getMessage().copy();
                this.editButton.setMessage(text.withStyle(ChatFormatting.RED));
            } else if (this.binding.isNotBound()) {
                var text = this.editButton.getMessage().copy();
                this.editButton.setMessage(text.withStyle(ChatFormatting.GOLD));
            }

            this.children.forEach(widget -> widget.extractRenderState(context, mouseX, mouseY, delta));
        }
    }

    public static class CategoryEntry extends eu.midnightdust.midnightcontrols.client.gui.widget.ControlsListWidget.Entry {
        private final SpruceSeparatorWidget separatorWidget;

        protected CategoryEntry(ControlsListWidget parent, ButtonCategory category) {
            super(parent);
            this.separatorWidget = new SpruceSeparatorWidget(Position.of(this, 2, 0), this.getWidth() - 4,
                    Component.literal(category.getTranslatedName())) {
                @Override
                public int getWidth() {
                    return CategoryEntry.this.getWidth() - 4;
                }
            };
        }

        public SpruceSeparatorWidget getSeparatorWidget() {
            return this.separatorWidget;
        }

        @Override
        public int getHeight() {
            return this.separatorWidget.getHeight() + 4;
        }

        /* Navigation */

        @Override
        public boolean onNavigation(NavigationEvent event) {
            return this.separatorWidget.onNavigation(event);
        }

        /* Rendering */

        @Override
        protected void extractWidgetRenderState(SpruceGuiGraphics context, int mouseX, int mouseY, float delta) {
            this.separatorWidget.extractRenderState(context, mouseX, mouseY, delta);
        }

        @Override
        public String toString() {
            return "SpruceTabbedWidget$SeparatorEntry{" +
                    "position=" + this.getPosition() +
                    ", width=" + this.getWidth() +
                    ", height=" + this.getHeight() +
                    '}';
        }
    }

    public abstract static class Entry extends SpruceEntryListWidget.Entry {
        protected final ControlsListWidget parent;

        protected Entry(ControlsListWidget parent) {
            this.parent = parent;
        }

        @Override
        public int getWidth() {
            return this.parent.getInnerWidth();
        }
    }
}
