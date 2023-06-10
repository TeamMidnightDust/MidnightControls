/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui.widget;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.navigation.NavigationDirection;
import dev.lambdaurora.spruceui.navigation.NavigationUtils;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceSeparatorWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceParentWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
        this.maxTextLength = InputManager.streamBindings().mapToInt(binding -> this.client.textRenderer.getWidth(binding.getText())).max().orElse(0);

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

    public class ButtonBindingEntry extends Entry implements SpruceParentWidget<SpruceWidget> {
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
            this.bindingName = I18n.translate(this.binding.getTranslationKey());
            this.editButton = new ControllerButtonWidget(Position.of(this, parent.getWidth() / 2 - 8, 0), 110, this.binding, btn -> {
                gui.focusedBinding = binding;
                MidnightControlsClient.get().input.beginControlsInput(gui);
            }) {
                protected Text getNarrationMessage() {
                    return binding.isNotBound() ? Text.translatable("narrator.controls.unbound", bindingName)
                            : Text.translatable("narrator.controls.bound", bindingName, super.getNarrationMessage());
                }
            };
            this.children.add(editButton);
            this.resetButton = new SpruceButtonWidget(Position.of(this,
                    this.editButton.getPosition().getRelativeX() + this.editButton.getWidth() + 2, 0),
                    44, 20, Text.translatable("controls.reset"),
                    btn -> MidnightControlsConfig.setButtonBinding(binding, binding.getDefaultButton())) {
                protected Text getNarrationMessage() {
                    return Text.translatable("narrator.controls.reset", bindingName);
                }
            };
            this.children.add(this.resetButton);
            this.unbindButton = new SpruceButtonWidget(Position.of(this,
                    this.editButton.getPosition().getRelativeX() + this.editButton.getWidth() + 2, 0),
                    this.resetButton.getWidth(), this.resetButton.getHeight(), SpruceTexts.GUI_UNBIND,
                    btn -> {
                        MidnightControlsConfig.setButtonBinding(binding, UNBOUND);
                        gui.focusedBinding = null;
                        MidnightControlsClient.get().input.beginControlsInput(null);
                    }) {
                protected Text getNarrationMessage() {
                    return Text.translatable("midnightcontrols.narrator.unbound", bindingName);
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
        protected boolean onMouseClick(double mouseX, double mouseY, int button) {
            var it = this.children().iterator();

            SpruceWidget element;
            do {
                if (!it.hasNext()) {
                    return false;
                }

                element = it.next();
            } while (!element.mouseClicked(mouseX, mouseY, button));

            this.setFocused(element);
            if (button == GLFW.GLFW_MOUSE_BUTTON_1)
                this.dragging = true;

            return true;
        }

        @Override
        protected boolean onMouseRelease(double mouseX, double mouseY, int button) {
            this.dragging = false;
            return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseReleased(mouseX, mouseY, button)).isPresent();
        }

        @Override
        protected boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return this.getFocused() != null && this.dragging && button == GLFW.GLFW_MOUSE_BUTTON_1
                    && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        protected boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
            return this.focused != null && this.focused.keyPressed(keyCode, scanCode, modifiers);
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
        public boolean onNavigation(@NotNull NavigationDirection direction, boolean tab) {
            if (this.requiresCursor()) return false;
            if (!tab && direction.isVertical()) {
                if (this.isFocused()) {
                    this.setFocused(null);
                    return false;
                }
                int lastIndex = this.parent.lastIndex;
                if (lastIndex >= this.children.size())
                    lastIndex = this.children.size() - 1;
                if (!this.children.get(lastIndex).onNavigation(direction, tab))
                    return false;
                this.setFocused(this.children.get(lastIndex));
                return true;
            }

            boolean result = NavigationUtils.tryNavigate(direction, tab, this.children, this.focused, this::setFocused, true);
            if (result) {
                this.setFocused(true);
                if (direction.isHorizontal() && this.getFocused() != null) {
                    this.parent.lastIndex = this.children.indexOf(this.getFocused());
                }
            }
            return result;
        }

        /* Rendering */

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean focused = gui.focusedBinding == this.binding;

            var textRenderer = ControlsListWidget.this.client.textRenderer;
            int height = this.getHeight();
            //float textX = (float) (this.getX() + 70 - ControlsListWidget.this.maxTextLength);
            int textY = this.getY() + height / 2;
            context.drawText(textRenderer, this.bindingName, this.getX(), (textY - 9 / 2), 16777215, true);

            this.resetButton.setVisible(!focused);
            this.unbindButton.setVisible(focused);
            this.resetButton.setActive(!this.binding.isDefault());

            this.editButton.update();
            if (focused) {
                var text = Text.literal("> ").formatted(Formatting.WHITE);
                text.append(this.editButton.getMessage().copy().formatted(Formatting.YELLOW));
                this.editButton.setMessage(text.append(Text.literal(" <").formatted(Formatting.WHITE)));
            } else if (!this.binding.isNotBound() && InputManager.hasDuplicatedBindings(this.binding)) {
                var text = this.editButton.getMessage().copy();
                this.editButton.setMessage(text.formatted(Formatting.RED));
            } else if (this.binding.isNotBound()) {
                var text = this.editButton.getMessage().copy();
                this.editButton.setMessage(text.formatted(Formatting.GOLD));
            }

            this.children.forEach(widget -> widget.render(context, mouseX, mouseY, delta));
        }
    }

    public static class CategoryEntry extends Entry {
        private final SpruceSeparatorWidget separatorWidget;

        protected CategoryEntry(ControlsListWidget parent, ButtonCategory category) {
            super(parent);
            this.separatorWidget = new SpruceSeparatorWidget(Position.of(this, 2, 0), this.getWidth() - 4,
                    Text.literal(category.getTranslatedName())) {
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
        public boolean onNavigation(@NotNull NavigationDirection direction, boolean tab) {
            return this.separatorWidget.onNavigation(direction, tab);
        }

        /* Rendering */

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            this.separatorWidget.render(context, mouseX, mouseY, delta);
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

    @Environment(EnvType.CLIENT)
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
