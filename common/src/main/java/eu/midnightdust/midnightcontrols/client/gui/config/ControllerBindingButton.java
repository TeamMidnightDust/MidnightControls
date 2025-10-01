package eu.midnightdust.midnightcontrols.client.gui.config;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.thinkingstudio.obsidianui.SpruceTexts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerBindingButton extends ButtonWidget implements ControlsInput {
    private static final int[] UNBOUND = new int[]{-1};
    static boolean waiting = false;
    static List<Integer> currentButtons = new ArrayList<>();
    private int iconWidth;

    public static void add(ButtonBinding binding, MidnightConfigListWidget list, MidnightConfigScreen screen) {
        ControllerBindingButton editButton = new ControllerBindingButton(screen.width - 185 + 22, 0, 128, 20, binding);
        TextIconButtonWidget resetButton = TextIconButtonWidget.builder(Text.translatable("controls.reset"), (button -> {
            MidnightControlsConfig.setButtonBinding(binding, binding.getDefaultButton());
            screen.updateList();
        }), true).texture(Identifier.of("midnightlib","icon/reset"), 12, 12).dimension(20, 20).build();
        resetButton.setPosition(screen.width - 205 + 150 + 25, 0);
        editButton.resetButton = resetButton;
        editButton.updateMessage(false);
        EntryInfo info = new EntryInfo(null, screen.modid);

        TextIconButtonWidget unbindButton = TextIconButtonWidget.builder(Text.translatable("midnightcontrols.narrator.unbound", binding.getText()), (button -> {
            MidnightControlsConfig.setButtonBinding(binding, UNBOUND);
            screen.updateList();
        }), true).texture(Identifier.of("midnightcontrols","icon/unbind"), 12, 12).dimension(20, 20).build();
        unbindButton.setPosition(screen.width - 205 + 20, 0);
        unbindButton.setTooltip(Tooltip.of(SpruceTexts.GUI_UNBIND));

        list.addButton(Lists.newArrayList(editButton, resetButton, unbindButton), Text.translatable(binding.getTranslationKey()), info);
    }

    private final ButtonBinding binding;
    private @Nullable ClickableWidget resetButton;
    public ControllerBindingButton(int x, int y, int width, int height, ButtonBinding binding) {
        super(x, y, width, height, binding.getText(), (button) -> {},
                (textSupplier) -> binding.isNotBound() ? Text.translatable("narrator.controls.unbound", binding.getTranslationKey()) : Text.translatable("narrator.controls.bound", binding.getTranslationKey(), textSupplier.get()));
        this.binding = binding;
        updateMessage(false);
    }

    @Override
    public void onPress(AbstractInput input) {
        MidnightControlsClient.input.beginControlsInput(this);
        this.updateMessage(true);
    }


    public void updateMessage(boolean focused) {
        AtomicBoolean hasConflicts = new AtomicBoolean(false);
        MutableText conflictingBindings = Text.empty();
        if (focused) this.setMessage(Text.literal("> ").append(getTranslatedButtons().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE)).append(" <").formatted(Formatting.YELLOW));
        else {
            this.setMessage(getTranslatedButtons());

            if (!this.binding.isNotBound()) {
                InputManager.streamBindings().forEach(keyBinding -> {
                    if (keyBinding != this.binding && this.binding.equals(keyBinding)) {
                        if (hasConflicts.get()) conflictingBindings.append(", ");

                        hasConflicts.set(true);
                        conflictingBindings.append(Text.translatable(keyBinding.getTranslationKey()));
                    }
                });
            }
        }

        if (this.resetButton != null) this.resetButton.active = !this.binding.isDefault();

        if (hasConflicts.get()) {
            this.setMessage(Text.literal("[ ").append(this.getMessage().copy().formatted(Formatting.WHITE)).append(" ]").formatted(Formatting.RED));
            this.setTooltip(Tooltip.of(Text.translatable("controls.keybinds.duplicateKeybinds", conflictingBindings)));
        } else {
            this.setTooltip(null);
        }
    }

    private Text getTranslatedButtons() {
        return this.binding.isNotBound() ? SpruceTexts.NOT_BOUND.copy() :
                (binding.getButton().length > 0 ? ButtonBinding.getLocalizedButtonName(binding.getButton()[0]) : Text.literal("..."));
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        if (this.binding.getButton().length < 2) super.drawMessage(context, textRenderer, color);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        int x = this.getX();
        if (this.binding.getButton().length > 1) {
            x += (this.width / 2 - iconWidth / 2) - 4;
        }
        var size = MidnightControlsRenderer.drawButton(context, x, this.getY(), this.binding, MinecraftClient.getInstance());
        iconWidth = size.length();
    }

    @Override
    public void finishBindingEdit(int... buttons) {
        MidnightControlsConfig.setButtonBinding(binding, buttons);
        updateMessage(false);
    }

    @Override
    public void update() {
        this.updateMessage(true);
    }

    @Override
    public void setWaiting(boolean value) {
        waiting = value;
    }

    @Override
    public boolean isWaiting() {
        return waiting;
    }

    @Override
    public List<Integer> getCurrentButtons() {
        return currentButtons;
    }

    @Override
    public ButtonBinding getFocusedBinding() {
        return this.binding;
    }
}