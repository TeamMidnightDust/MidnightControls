/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.gui;

import dev.lambdaurora.lambdacontrols.ControlsMode;
import dev.lambdaurora.lambdacontrols.LambdaControls;
import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.controller.Controller;
import dev.lambdaurora.lambdacontrols.client.gui.widget.ControllerControlsWidget;
import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.option.*;
import me.lambdaurora.spruceui.screen.SpruceScreen;
import me.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import me.lambdaurora.spruceui.widget.SpruceLabelWidget;
import me.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import me.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import me.lambdaurora.spruceui.widget.container.tabbed.SpruceTabbedWidget;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls settings screen.
 */
public class LambdaControlsSettingsScreen extends SpruceScreen {
    private static final Text SDL2_GAMEPAD_TOOL = new LiteralText("SDL2 Gamepad Tool").formatted(Formatting.GREEN);
    public static final String GAMEPAD_TOOL_URL = "https://generalarcade.com/gamepadtool/";
    final LambdaControlsClient mod;
    private final Screen parent;
    // General options
    private final SpruceOption inputModeOption;
    private final SpruceOption autoSwitchModeOption;
    private final SpruceOption rotationSpeedOption;
    private final SpruceOption mouseSpeedOption;
    private final SpruceOption resetOption;
    // Gameplay options
    private final SpruceOption autoJumpOption;
    private final SpruceOption fastBlockPlacingOption;
    private final SpruceOption frontBlockPlacingOption;
    private final SpruceOption verticalReacharoundOption;
    private final SpruceOption flyDriftingOption;
    private final SpruceOption flyVerticalDriftingOption;
    // Controller options
    private final SpruceOption controllerOption;
    private final SpruceOption secondControllerOption;
    private final SpruceOption controllerTypeOption;
    private final SpruceOption deadZoneOption;
    private final SpruceOption invertsRightXAxis;
    private final SpruceOption invertsRightYAxis;
    private final SpruceOption unfocusedInputOption;
    private final SpruceOption virtualMouseOption;
    private final SpruceOption virtualMouseSkinOption;
    // Hud options
    private final SpruceOption hudEnableOption;
    private final SpruceOption hudSideOption;
    private final MutableText controllerMappingsUrlText = new LiteralText("(")
            .append(new LiteralText(GAMEPAD_TOOL_URL).formatted(Formatting.GOLD))
            .append("),");

    public LambdaControlsSettingsScreen(Screen parent, boolean hideControls) {
        super(new TranslatableText("lambdacontrols.title.settings"));
        this.mod = LambdaControlsClient.get();
        this.parent = parent;
        // General options
        this.inputModeOption = new SpruceCyclingOption("lambdacontrols.menu.controls_mode",
                amount -> {
                    ControlsMode next = this.mod.config.getControlsMode().next();
                    this.mod.config.setControlsMode(next);
                    this.mod.config.save();

                    if (this.client.player != null) {
                        ClientSidePacketRegistry.INSTANCE.sendToServer(LambdaControls.CONTROLS_MODE_CHANNEL, this.mod.makeControlsModeBuffer(next));
                    }
                }, option -> option.getDisplayText(new TranslatableText(this.mod.config.getControlsMode().getTranslationKey())),
                new TranslatableText("lambdacontrols.tooltip.controls_mode"));
        this.autoSwitchModeOption = new SpruceBooleanOption("lambdacontrols.menu.auto_switch_mode", this.mod.config::hasAutoSwitchMode,
                this.mod.config::setAutoSwitchMode, new TranslatableText("lambdacontrols.tooltip.auto_switch_mode"), true);
        this.rotationSpeedOption = new SpruceDoubleOption("lambdacontrols.menu.rotation_speed", 0.0, 100.0, 0.5F, this.mod.config::getRotationSpeed,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setRotationSpeed(newValue);
                    }
                }, option -> option.getDisplayText(new LiteralText(String.valueOf(option.get()))),
                new TranslatableText("lambdacontrols.tooltip.rotation_speed"));
        this.mouseSpeedOption = new SpruceDoubleOption("lambdacontrols.menu.mouse_speed", 0.0, 150.0, 0.5F, this.mod.config::getMouseSpeed,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setMouseSpeed(newValue);
                    }
                }, option -> option.getDisplayText(new LiteralText(String.valueOf(option.get()))),
                new TranslatableText("lambdacontrols.tooltip.mouse_speed"));
        this.resetOption = SpruceSimpleActionOption.reset(btn -> {
            this.mod.config.reset();
            MinecraftClient client = MinecraftClient.getInstance();
            this.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        });
        // Gameplay options
        this.autoJumpOption = new SpruceCheckboxBooleanOption("options.autoJump",
                () -> this.client.options.autoJump,
                newValue -> this.client.options.autoJump = newValue,
                null, true);
        this.fastBlockPlacingOption = new SpruceCheckboxBooleanOption("lambdacontrols.menu.fast_block_placing", this.mod.config::hasFastBlockPlacing,
                this.mod.config::setFastBlockPlacing, new TranslatableText("lambdacontrols.tooltip.fast_block_placing"), true);
        this.frontBlockPlacingOption = new SpruceCheckboxBooleanOption("lambdacontrols.menu.reacharound.horizontal", this.mod.config::hasFrontBlockPlacing,
                this.mod.config::setFrontBlockPlacing, new TranslatableText("lambdacontrols.tooltip.reacharound.horizontal"), true);
        this.verticalReacharoundOption = new SpruceCheckboxBooleanOption("lambdacontrols.menu.reacharound.vertical", this.mod.config::hasVerticalReacharound,
                this.mod.config::setVerticalReacharound, new TranslatableText("lambdacontrols.tooltip.reacharound.vertical"), true);
        this.flyDriftingOption = new SpruceCheckboxBooleanOption("lambdacontrols.menu.fly_drifting", this.mod.config::hasFlyDrifting,
                this.mod.config::setFlyDrifting, new TranslatableText("lambdacontrols.tooltip.fly_drifting"), true);
        this.flyVerticalDriftingOption = new SpruceCheckboxBooleanOption("lambdacontrols.menu.fly_drifting_vertical", this.mod.config::hasFlyVerticalDrifting,
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
                return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.RED));
            else if (!this.mod.config.getController().isGamepad())
                return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.GOLD));
            else
                return option.getDisplayText(new LiteralText(controllerName));
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
                return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.RED));
            else if (!controller.isGamepad())
                return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.GOLD));
            else
                return option.getDisplayText(new LiteralText(controllerName));
        }).orElse(option.getDisplayText(SpruceTexts.OPTIONS_OFF.shallowCopy().formatted(Formatting.RED))),
                new TranslatableText("lambdacontrols.tooltip.controller2"));
        this.controllerTypeOption = new SpruceCyclingOption("lambdacontrols.menu.controller_type",
                amount -> this.mod.config.setControllerType(this.mod.config.getControllerType().next()),
                option -> option.getDisplayText(this.mod.config.getControllerType().getTranslatedText()),
                new TranslatableText("lambdacontrols.tooltip.controller_type"));
        this.deadZoneOption = new SpruceDoubleOption("lambdacontrols.menu.dead_zone", 0.05, 1.0, 0.05F, this.mod.config::getDeadZone,
                newValue -> {
                    synchronized (this.mod.config) {
                        this.mod.config.setDeadZone(newValue);
                    }
                }, option -> {
            String value = String.valueOf(option.get());
            return option.getDisplayText(new LiteralText(value.substring(0, Math.min(value.length(), 5))));
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
                option -> option.getDisplayText(this.mod.config.getVirtualMouseSkin().getTranslatedText()),
                null);
        // HUD options
        this.hudEnableOption = new SpruceBooleanOption("lambdacontrols.menu.hud_enable", this.mod.config::isHudEnabled,
                this.mod::setHudEnabled, new TranslatableText("lambdacontrols.tooltip.hud_enable"), true);
        this.hudSideOption = new SpruceCyclingOption("lambdacontrols.menu.hud_side",
                amount -> this.mod.config.setHudSide(this.mod.config.getHudSide().next()),
                option -> option.getDisplayText(this.mod.config.getHudSide().getTranslatedText()),
                new TranslatableText("lambdacontrols.tooltip.hud_side"));
    }

    @Override
    public void removed() {
        this.mod.config.save();
        super.removed();
    }

    @Override
    public void onClose() {
        this.mod.config.save();
        super.onClose();
    }

    private int getTextHeight() {
        return (5 + this.textRenderer.fontHeight) * 3 + 5;
    }

    @Override
    protected void init() {
        super.init();
        /*int buttonHeight = 20;
        SpruceButtonWidget controlsModeBtn = new SpruceButtonWidget(Position.of(this.width / 2 - 155, 18), this.hideControls ? 310 : 150, buttonHeight,
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
        this.addChild(controlsModeBtn);
        if (!this.hideControls)
            this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 18, 150, buttonHeight, new TranslatableText("options.controls"),
                    btn -> {
                        if (this.mod.config.getControlsMode() == ControlsMode.CONTROLLER)
                            this.client.openScreen(new ControllerControlsScreen(this, true));
                        else
                            this.client.openScreen(new ControlsOptionsScreen(this, this.client.options));
                    }));

        this.list = new SpruceOptionListWidget(Position.of(this, 0, 43), this.width, this.height - 29 - this.getTextHeight() - 43);
        // General options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.general", true, null));
        this.list.addOptionEntry(this.rotationSpeedOption, this.mouseSpeedOption);
        this.list.addSingleOptionEntry(this.autoSwitchModeOption);
        // Gameplay options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.gameplay", true, null));
        //this.list.addOptionEntry(this.autoJumpOption, this.fastBlockPlacingOption);
        this.list.addOptionEntry(this.frontBlockPlacingOption, this.verticalReacharoundOption);
        this.list.addSingleOptionEntry(this.flyDriftingOption);
        this.list.addSingleOptionEntry(this.flyVerticalDriftingOption);
        //this.list.addOptionEntry(Option.SNEAK_TOGGLED, Option.SPRINT_TOGGLED);
        // Controller options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.controller", true, null));
        this.list.addSingleOptionEntry(this.controllerOption);
        this.list.addSingleOptionEntry(this.secondControllerOption);
        this.list.addOptionEntry(this.controllerTypeOption, this.deadZoneOption);
        this.list.addOptionEntry(this.invertsRightXAxis, this.invertsRightYAxis);
        this.list.addOptionEntry(this.unfocusedInputOption, this.virtualMouseOption);
        this.list.addSingleOptionEntry(this.virtualMouseSkinOption);
        this.list.addSingleOptionEntry(ReloadControllerMappingsOption.newOption(null));
        this.list.addSingleOptionEntry(SpruceSimpleActionOption.of("lambdacontrols.menu.mappings.open_input_str",
                btn -> this.client.openScreen(new MappingsStringInputScreen(this))));
        // HUD options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.hud", true, null));
        this.list.addOptionEntry(this.hudEnableOption, this.hudSideOption);
        this.addChild(this.list);*/

        this.buildTabs();

        this.addChild(this.resetOption.createWidget(Position.of(this.width / 2 - 155, this.height - 29), 150));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, SpruceTexts.GUI_DONE,
                btn -> this.client.openScreen(this.parent)));
    }

    public void buildTabs() {
        SpruceTabbedWidget tabs = new SpruceTabbedWidget(Position.of(0, 24), this.width, this.height - 32 - 24,
                null,
                Math.max(110, this.width / 8), 0);
        this.addChild(tabs);

        tabs.addSeparatorEntry(new TranslatableText("lambdacontrols.menu.separator.general"));
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.general"), null,
                this::buildGeneralTab);
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.gameplay"), null,
                this::buildGameplayTab);
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.hud"), null,
                this::buildHudTab);

        tabs.addSeparatorEntry(new TranslatableText("options.controls"));
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.controller_controls"), null,
                this::buildControllerControlsTab);

        tabs.addSeparatorEntry(new TranslatableText("lambdacontrols.menu.separator.controller"));
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.controller"), null,
                this::buildControllerTab);
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.mappings.string"), null,
                this::buildMappingsStringEditorTab);
    }

    public SpruceOptionListWidget buildGeneralTab(int width, int height) {
        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.origin(), width, height);
        list.addSingleOptionEntry(this.inputModeOption);
        list.addSingleOptionEntry(this.autoSwitchModeOption);
        return list;
    }

    public SpruceOptionListWidget buildGameplayTab(int width, int height) {
        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.origin(), width, height);
        list.addSingleOptionEntry(this.fastBlockPlacingOption);
        list.addSingleOptionEntry(this.frontBlockPlacingOption);
        list.addSingleOptionEntry(this.verticalReacharoundOption);
        list.addSingleOptionEntry(this.flyDriftingOption);
        list.addSingleOptionEntry(this.flyVerticalDriftingOption);
        list.addSingleOptionEntry(this.autoJumpOption);
        return list;
    }

    public SpruceOptionListWidget buildHudTab(int width, int height) {
        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.origin(), width, height);
        list.addSingleOptionEntry(this.hudEnableOption);
        list.addSingleOptionEntry(this.hudSideOption);
        return list;
    }

    public ControllerControlsWidget buildControllerControlsTab(int width, int height) {
        return new ControllerControlsWidget(Position.origin(), width, height);
    }

    public AbstractSpruceWidget buildControllerTab(int width, int height) {
        SpruceContainerWidget root = new SpruceContainerWidget(Position.origin(), width, height);

        SpruceLabelWidget aboutMappings1 = new SpruceLabelWidget(Position.of(width / 2, 2),
                new TranslatableText("lambdacontrols.controller.mappings.1", SDL2_GAMEPAD_TOOL),
                width, true);

        SpruceLabelWidget gamepadToolUrlLabel = new SpruceLabelWidget(Position.of(width / 2, aboutMappings1.getHeight() + 4),
                this.controllerMappingsUrlText, width,
                label -> Util.getOperatingSystem().open(GAMEPAD_TOOL_URL), true);
        gamepadToolUrlLabel.setTooltip(new TranslatableText("chat.link.open"));

        SpruceLabelWidget aboutMappings3 = new SpruceLabelWidget(Position.of(width / 2,
                aboutMappings1.getHeight() + gamepadToolUrlLabel.getHeight() + 6),
                new TranslatableText("lambdacontrols.controller.mappings.3", Formatting.GREEN.toString(), Formatting.RESET.toString()),
                width, true);

        int listHeight = height - 8 - aboutMappings1.getHeight() - aboutMappings3.getHeight() - gamepadToolUrlLabel.getHeight();
        SpruceContainerWidget labels = new SpruceContainerWidget(Position.of(0,
                listHeight),
                width, height - listHeight);
        labels.addChild(aboutMappings1);
        labels.addChild(gamepadToolUrlLabel);
        labels.addChild(aboutMappings3);

        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.origin(), width, listHeight);
        list.addSingleOptionEntry(this.controllerOption);
        list.addSingleOptionEntry(this.secondControllerOption);

        root.addChild(list);
        root.addChild(labels);
        return root;
    }

    public SpruceContainerWidget buildMappingsStringEditorTab(int width, int height) {
        return new MappingsStringInputWidget(Position.origin(), width, height);
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredString(matrices, this.textRenderer, I18n.translate("lambdacontrols.menu.title"), this.width / 2, 8, 16777215);
//        drawCenteredString(matrices, this.textRenderer, I18n.translate("lambdacontrols.controller.mappings.1", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.textRenderer.fontHeight) * 3, 10526880);
//        drawCenteredString(matrices, this.textRenderer, I18n.translate("lambdacontrols.controller.mappings.3", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.textRenderer.fontHeight), 10526880);
    }
}
