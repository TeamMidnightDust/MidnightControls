/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.spruceui.SpruceButtonWidget;
import me.lambdaurora.spruceui.SpruceLabelWidget;
import me.lambdaurora.spruceui.Tooltip;
import me.lambdaurora.spruceui.option.*;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls settings screen.
 */
public class LambdaControlsSettingsScreen extends Screen
{
    public static final String               GAMEPAD_TOOL_URL          = "https://generalarcade.com/gamepadtool/";
    final               LambdaControlsClient mod;
    private final       Screen               parent;
    private final       boolean              hideControls;
    // General options
    private final       Option               autoSwitchModeOption;
    private final       Option               rotationSpeedOption;
    private final       Option               mouseSpeedOption;
    private final       Option               resetOption;
    // Gameplay options
    private final       Option               autoJumpOption;
    private final       Option               fastBlockPlacingOption;
    private final       Option               frontBlockPlacingOption;
    private final       Option               flyDriftingOption;
    private final       Option               flyVerticalDriftingOption;
    // Controller options
    private final       Option               controllerOption;
    private final       Option               secondControllerOption;
    private final       Option               controllerTypeOption;
    private final       Option               deadZoneOption;
    private final       Option               invertsRightXAxis;
    private final       Option               invertsRightYAxis;
    private final       Option               unfocusedInputOption;
    private final       Option               virtualMouseOption;
    private final       Option               virtualMouseSkinOption;
    // Hud options
    private final       Option               hudEnableOption;
    private final       Option               hudSideOption;
    private final       MutableText          controllerMappingsUrlText = new LiteralText("(")
            .append(new LiteralText(GAMEPAD_TOOL_URL).formatted(Formatting.GOLD))
            .append("),");
    private             ButtonListWidget     list;
    private             SpruceLabelWidget    gamepadToolUrlLabel;

    public LambdaControlsSettingsScreen(Screen parent, boolean hideControls)
    {
        super(new TranslatableText("lambdacontrols.title.settings"));
        this.mod = LambdaControlsClient.get();
        this.parent = parent;
        this.hideControls = hideControls;
        // General options
        this.autoSwitchModeOption = new SpruceBooleanOption("lambdacontrols.menu.auto_switch_mode", this.mod.config::hasAutoSwitchMode,
                this.mod.config::setAutoSwitchMode, new TranslatableText("lambdacontrols.tooltip.auto_switch_mode"), true);
        this.rotationSpeedOption = new SpruceDoubleOption("lambdacontrols.menu.rotation_speed", 0.0, 100.0, 0.5F, this.mod.config::getRotationSpeed,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setRotationSpeed(newValue);
                    }
                }, option -> option.getDisplayPrefix().append(String.valueOf(option.get())),
                new TranslatableText("lambdacontrols.tooltip.rotation_speed"));
        this.mouseSpeedOption = new SpruceDoubleOption("lambdacontrols.menu.mouse_speed", 0.0, 150.0, 0.5F, this.mod.config::getMouseSpeed,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setMouseSpeed(newValue);
                    }
                }, option -> option.getDisplayPrefix().append(String.valueOf(option.get())),
                new TranslatableText("lambdacontrols.tooltip.mouse_speed"));
        this.resetOption = new SpruceResetOption(btn -> {
            this.mod.config.reset();
            MinecraftClient client = MinecraftClient.getInstance();
            this.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        });
        // Gameplay options
        this.autoJumpOption = SpruceBooleanOption.fromVanilla("options.autoJump", Option.AUTO_JUMP, null, true);
        this.fastBlockPlacingOption = new SpruceBooleanOption("lambdacontrols.menu.fast_block_placing", this.mod.config::hasFastBlockPlacing,
                this.mod.config::setFastBlockPlacing, new TranslatableText("lambdacontrols.tooltip.fast_block_placing"), true);
        this.frontBlockPlacingOption = new SpruceBooleanOption("lambdacontrols.menu.front_block_placing", this.mod.config::hasFrontBlockPlacing,
                this.mod.config::setFrontBlockPlacing, new TranslatableText("lambdacontrols.tooltip.front_block_placing"), true);
        this.flyDriftingOption = new SpruceBooleanOption("lambdacontrols.menu.fly_drifting", this.mod.config::hasFlyDrifting,
                this.mod.config::setFlyDrifting, new TranslatableText("lambdacontrols.tooltip.fly_drifting"), true);
        this.flyVerticalDriftingOption = new SpruceBooleanOption("lambdacontrols.menu.fly_drifting_vertical", this.mod.config::hasFlyVerticalDrifting,
                this.mod.config::setFlyVerticalDrifting, new TranslatableText("lambdacontrols.tooltip.fly_drifting_vertical"), true);
        // Controller options
        this.controllerOption = new SpruceCyclingOption("lambdacontrols.menu.controller", amount -> {
            int id = this.mod.config.getController().getId();
            id += amount;
            if (id > GLFW.GLFW_JOYSTICK_LAST)
                id = GLFW.GLFW_JOYSTICK_1;
            this.mod.config.setController(Controller.byId(id));
        }, option -> {
            String controllerName = this.mod.config.getController().getName();
            if (!this.mod.config.getController().isConnected())
                return option.getDisplayPrefix().append(new LiteralText(controllerName).formatted(Formatting.RED));
            else if (!this.mod.config.getController().isGamepad())
                return option.getDisplayPrefix().append(new LiteralText(controllerName).formatted(Formatting.GOLD));
            else
                return option.getDisplayPrefix().append(controllerName);
        }, null);
        this.secondControllerOption = new SpruceCyclingOption("lambdacontrols.menu.controller2",
                amount -> {
                    int id = this.mod.config.getSecondController().map(Controller::getId).orElse(-1);
                    id += amount;
                    if (id > GLFW.GLFW_JOYSTICK_LAST)
                        id = -1;
                    this.mod.config.setSecondController(id == -1 ? null : Controller.byId(id));
                }, option -> this.mod.config.getSecondController().map(controller -> {
            String controllerName = controller.getName();
            if (!controller.isConnected())
                return option.getDisplayPrefix().append(new LiteralText(controllerName).formatted(Formatting.RED));
            else if (!controller.isGamepad())
                return option.getDisplayPrefix().append(new LiteralText(controllerName).formatted(Formatting.GOLD));
            else
                return option.getDisplayPrefix().append(controllerName);
        }).orElse(option.getDisplayPrefix().append(new TranslatableText("options.off").formatted(Formatting.RED))),
                new TranslatableText("lambdacontrols.tooltip.controller2"));
        this.controllerTypeOption = new SpruceCyclingOption("lambdacontrols.menu.controller_type",
                amount -> this.mod.config.setControllerType(this.mod.config.getControllerType().next()),
                option -> option.getDisplayPrefix().append(this.mod.config.getControllerType().getTranslatedName()),
                new TranslatableText("lambdacontrols.tooltip.controller_type"));
        this.deadZoneOption = new SpruceDoubleOption("lambdacontrols.menu.dead_zone", 0.05, 1.0, 0.05F, this.mod.config::getDeadZone,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setDeadZone(newValue);
                    }
                }, option -> {
            String value = String.valueOf(option.get());
            return option.getDisplayPrefix().append(value.substring(0, Math.min(value.length(), 5)));
        }, new TranslatableText("lambdacontrols.tooltip.dead_zone"));
        this.invertsRightXAxis = new SpruceBooleanOption("lambdacontrols.menu.invert_right_x_axis", this.mod.config::doesInvertRightXAxis,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setInvertRightXAxis(newValue);
                    }
                }, null, true);
        this.invertsRightYAxis = new SpruceBooleanOption("lambdacontrols.menu.invert_right_y_axis", this.mod.config::doesInvertRightYAxis,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setInvertRightYAxis(newValue);
                    }
                }, null, true);
        this.unfocusedInputOption = new SpruceBooleanOption("lambdacontrols.menu.unfocused_input", this.mod.config::hasUnfocusedInput,
                this.mod.config::setUnfocusedInput, new TranslatableText("lambdacontrols.tooltip.unfocused_input"), true);
        this.virtualMouseOption = new SpruceBooleanOption("lambdacontrols.menu.virtual_mouse", this.mod.config::hasVirtualMouse,
                this.mod.config::setVirtualMouse, new TranslatableText("lambdacontrols.tooltip.virtual_mouse"), true);
        this.virtualMouseSkinOption = new SpruceCyclingOption("lambdacontrols.menu.virtual_mouse.skin",
                amount -> this.mod.config.setVirtualMouseSkin(this.mod.config.getVirtualMouseSkin().next()),
                option -> option.getDisplayPrefix().append(this.mod.config.getVirtualMouseSkin().getTranslatedName()),
                null);
        // HUD options
        this.hudEnableOption = new SpruceBooleanOption("lambdacontrols.menu.hud_enable", this.mod.config::isHudEnabled,
                this.mod::setHudEnabled, new TranslatableText("lambdacontrols.tooltip.hud_enable"), true);
        this.hudSideOption = new SpruceCyclingOption("lambdacontrols.menu.hud_side",
                amount -> this.mod.config.setHudSide(this.mod.config.getHudSide().next()),
                option -> option.getDisplayPrefix().append(this.mod.config.getHudSide().getTranslatedName()),
                new TranslatableText("lambdacontrols.tooltip.hud_side"));
    }

    @Override
    public void removed()
    {
        this.mod.config.save();
        super.removed();
    }

    @Override
    public void onClose()
    {
        this.mod.config.save();
        super.onClose();
    }

    private int getTextHeight()
    {
        return (5 + this.textRenderer.fontHeight) * 3 + 5;
    }

    @Override
    protected void init()
    {
        super.init();
        int buttonHeight = 20;
        SpruceButtonWidget controlsModeBtn = new SpruceButtonWidget(this.width / 2 - 155, 18, this.hideControls ? 310 : 150, buttonHeight,
                new TranslatableText("lambdacontrols.menu.controls_mode").append(": ").append(new TranslatableText(this.mod.config.getControlsMode().getTranslationKey())),
                btn -> {
                    ControlsMode next = this.mod.config.getControlsMode().next();
                    btn.setMessage(new TranslatableText("lambdacontrols.menu.controls_mode").append(": ").append(new TranslatableText(next.getTranslationKey())));
                    this.mod.config.setControlsMode(next);
                    this.mod.config.save();

                    if (this.client.player != null) {
                        ClientSidePacketRegistry.INSTANCE.sendToServer(LambdaControls.CONTROLS_MODE_CHANNEL, this.mod.makeControlsModeBuffer(next));
                    }
                });
        controlsModeBtn.setTooltip(new TranslatableText("lambdacontrols.tooltip.controls_mode"));
        this.addButton(controlsModeBtn);
        if (!this.hideControls)
            this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 18, 150, buttonHeight, new TranslatableText("options.controls"),
                    btn -> {
                        if (this.mod.config.getControlsMode() == ControlsMode.CONTROLLER)
                            this.client.openScreen(new ControllerControlsScreen(this, true));
                        else
                            this.client.openScreen(new ControlsOptionsScreen(this, this.client.options));
                    }));

        this.list = new ButtonListWidget(this.client, this.width, this.height, 43, this.height - 29 - this.getTextHeight(), 25);
        // General options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.general", true, null));
        this.list.addOptionEntry(this.rotationSpeedOption, this.mouseSpeedOption);
        this.list.addSingleOptionEntry(this.autoSwitchModeOption);
        // Gameplay options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.gameplay", true, null));
        this.list.addSingleOptionEntry(this.autoJumpOption);
        this.list.addOptionEntry(this.fastBlockPlacingOption, this.frontBlockPlacingOption);
        this.list.addSingleOptionEntry(this.flyDriftingOption);
        this.list.addSingleOptionEntry(this.flyVerticalDriftingOption);
        // Controller options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.controller", true, null));
        this.list.addSingleOptionEntry(this.controllerOption);
        this.list.addSingleOptionEntry(this.secondControllerOption);
        this.list.addOptionEntry(this.controllerTypeOption, this.deadZoneOption);
        this.list.addOptionEntry(this.invertsRightXAxis, this.invertsRightYAxis);
        this.list.addOptionEntry(this.unfocusedInputOption, this.virtualMouseOption);
        this.list.addSingleOptionEntry(this.virtualMouseSkinOption);
        this.list.addSingleOptionEntry(new ReloadControllerMappingsOption());
        // HUD options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.hud", true, null));
        this.list.addOptionEntry(this.hudEnableOption, this.hudSideOption);
        this.children.add(this.list);

        this.gamepadToolUrlLabel = new SpruceLabelWidget(this.width / 2, this.height - 29 - (5 + this.textRenderer.fontHeight) * 2, this.controllerMappingsUrlText, this.width,
                label -> Util.getOperatingSystem().open(GAMEPAD_TOOL_URL), true);
        this.gamepadToolUrlLabel.setTooltip(new TranslatableText("chat.link.open"));
        this.children.add(this.gamepadToolUrlLabel);

        this.addButton(this.resetOption.createButton(this.client.options, this.width / 2 - 155, this.height - 29, 150));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, buttonHeight, new TranslatableText("gui.done"),
                (buttonWidget) -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("lambdacontrols.menu.title"), this.width / 2, 8, 16777215);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("lambdacontrols.controller.mappings.1", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.textRenderer.fontHeight) * 3, 10526880);
        this.gamepadToolUrlLabel.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("lambdacontrols.controller.mappings.3", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.textRenderer.fontHeight), 10526880);

        Tooltip.renderAll(matrices);
    }
}
