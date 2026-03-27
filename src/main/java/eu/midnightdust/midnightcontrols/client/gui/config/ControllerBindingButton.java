package eu.midnightdust.midnightcontrols.client.gui.config;

import com.google.common.collect.Lists;
import dev.lambdaurora.spruceui.SpruceTexts;
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class ControllerBindingButton extends Button implements ControlsInput {
    private static final int[] UNBOUND = new int[]{-1};
    static boolean waiting = false;
    static List<Integer> currentButtons = new ArrayList<>();
    private int iconWidth;

    public static void add(ButtonBinding binding, MidnightConfigListWidget list, MidnightConfigScreen screen) {
        ControllerBindingButton editButton = new ControllerBindingButton(screen.width - 185 + 22, 0, 128, 20, binding);
        SpriteIconButton resetButton = SpriteIconButton.builder(net.minecraft.network.chat.Component.translatable("controls.reset"), (button -> {
            MidnightControlsConfig.setButtonBinding(binding, binding.getDefaultButton());
            MidnightControlsClient.input.beginControlsInput(null);
            editButton.updateMessage(false);
        }), true).sprite(Identifier.fromNamespaceAndPath("midnightlib","icon/reset"), 12, 12).size(20, 20).build();
        resetButton.setPosition(screen.width - 205 + 150 + 25, 0);
        editButton.resetButton = resetButton;
        editButton.updateMessage(false);
        EntryInfo info = new EntryInfo(null, screen.modid);

        SpriteIconButton unbindButton = SpriteIconButton.builder(net.minecraft.network.chat.Component.translatable("midnightcontrols.narrator.unbound", binding.getText()), (button -> {
            MidnightControlsConfig.setButtonBinding(binding, UNBOUND);
            MidnightControlsClient.input.beginControlsInput(null);
            editButton.updateMessage(false);
        }), true).sprite(Identifier.fromNamespaceAndPath("midnightcontrols","icon/unbind"), 12, 12).size(20, 20).build();
        unbindButton.setPosition(screen.width - 205 + 20, 0);
        unbindButton.setTooltip(Tooltip.create(SpruceTexts.GUI_UNBIND));
        unbindButton.active = !binding.isNotBound();
        editButton.unbindButton = unbindButton;

        list.addButton(Lists.newArrayList(editButton, resetButton, unbindButton), net.minecraft.network.chat.Component.translatable(binding.getTranslationKey()), info);
    }

    private final ButtonBinding binding;
    private @Nullable AbstractWidget resetButton;
    private @Nullable AbstractWidget unbindButton;
    public ControllerBindingButton(int x, int y, int width, int height, ButtonBinding binding) {
        super(x, y, width, height, binding.getText(), (button) -> {},
                (textSupplier) -> binding.isNotBound() ? net.minecraft.network.chat.Component.translatable("narrator.controls.unbound", binding.getTranslationKey()) : net.minecraft.network.chat.Component.translatable("narrator.controls.bound", binding.getTranslationKey(), textSupplier.get()));
        this.binding = binding;
        updateMessage(false);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        MidnightControlsClient.input.beginControlsInput(this);
        this.updateMessage(true);
    }


    public void updateMessage(boolean focused) {
        AtomicBoolean hasConflicts = new AtomicBoolean(false);
        MutableComponent conflictingBindings = net.minecraft.network.chat.Component.empty();
        if (focused) this.setMessage(net.minecraft.network.chat.Component.literal("> ").append(getTranslatedButtons().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW));
        else {
            this.setMessage(getTranslatedButtons());

            if (!this.binding.isNotBound()) {
                InputManager.streamBindings().forEach(keyBinding -> {
                    if (keyBinding != this.binding && this.binding.equals(keyBinding)) {
                        if (hasConflicts.get()) conflictingBindings.append(", ");

                        hasConflicts.set(true);
                        conflictingBindings.append(net.minecraft.network.chat.Component.translatable(keyBinding.getTranslationKey()));
                    }
                });
            }
        }

        if (this.resetButton != null) this.resetButton.active = !this.binding.isDefault();
        if (this.unbindButton != null) this.unbindButton.active = !binding.isNotBound();

        if (hasConflicts.get()) {
            this.setMessage(net.minecraft.network.chat.Component.literal("[ ").append(this.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
            this.setTooltip(Tooltip.create(net.minecraft.network.chat.Component.translatable("controls.keybinds.duplicateKeybinds", conflictingBindings)));
        } else {
            this.setTooltip(null);
        }
    }

    private net.minecraft.network.chat.Component getTranslatedButtons() {
        return this.binding.isNotBound() ? SpruceTexts.NOT_BOUND.copy() :
                (binding.getButton().length > 0 ? ButtonBinding.getLocalizedButtonName(binding.getButton()[0]) : net.minecraft.network.chat.Component.literal("..."));
    }

    @Override
    //~ if >=26.1 'renderDefaultLabel' -> 'extractDefaultLabel' {
    public void extractDefaultLabel(ActiveTextCollector consumer) {
        if (this.binding.getButton().length < 2) super.extractDefaultLabel(consumer);
    }
    //~}

    @Override
    protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        //~ if >=26.1 'renderDefault' -> 'extractDefault' {
        this.extractDefaultSprite(context);
        this.extractDefaultLabel(context.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        //~}
        int x = this.getX();
        if (this.binding.getButton().length > 1) {
            x += (this.width / 2 - iconWidth / 2) - 4;
        }
        var size = MidnightControlsRenderer.drawButton(context, x, this.getY(), this.binding, Minecraft.getInstance());
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