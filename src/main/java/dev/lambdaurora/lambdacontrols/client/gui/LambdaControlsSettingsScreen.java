/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
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
import dev.lambdaurora.lambdacontrols.client.LambdaControlsConfig;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
    final LambdaControlsClient mod = LambdaControlsClient.get();
    private final LambdaControlsConfig config = this.mod.config;
    private final Screen parent;
    // General options
    private final SpruceOption inputModeOption;
    private final SpruceOption autoSwitchModeOption;
    private final SpruceOption rotationSpeedOption;
    private final SpruceOption mouseSpeedOption;
    private final SpruceOption virtualMouseOption;
    private final SpruceOption resetOption;
    // Gameplay options
    private final SpruceOption analogMovementOption;
    private final SpruceOption autoJumpOption;
    private final SpruceOption fastBlockPlacingOption;
    private final SpruceOption frontBlockPlacingOption;
    private final SpruceOption verticalReacharoundOption;
    private final SpruceOption flyDriftingOption;
    private final SpruceOption flyVerticalDriftingOption;
    // Appearance options
    private final SpruceOption controllerTypeOption;
    private final SpruceOption virtualMouseSkinOption;
    private final SpruceOption hudEnableOption;
    private final SpruceOption hudSideOption;
    // Controller options
    private final SpruceOption controllerOption =
            new SpruceCyclingOption("lambdacontrols.menu.controller",
                    amount -> {
                        int id = this.config.getController().getId();
                        id += amount;
                        if (id > GLFW.GLFW_JOYSTICK_LAST)
                            id = GLFW.GLFW_JOYSTICK_1;
                        id = searchNextAvailableController(id, false);
                        this.config.setController(Controller.byId(id));
                    },
                    option -> {
                        Controller controller = this.config.getController();
                        String controllerName = controller.getName();
                        if (!controller.isConnected())
                            return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.RED));
                        else if (!controller.isGamepad())
                            return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.GOLD));
                        else
                            return option.getDisplayText(new LiteralText(controllerName));
                    }, null);
    private final SpruceOption secondControllerOption = new SpruceCyclingOption("lambdacontrols.menu.controller2",
            amount -> {
                int id = this.config.getSecondController().map(Controller::getId).orElse(-1);
                id += amount;
                if (id > GLFW.GLFW_JOYSTICK_LAST)
                    id = -1;
                id = searchNextAvailableController(id, true);
                this.config.setSecondController(id == -1 ? null : Controller.byId(id));
            },
            option -> this.config.getSecondController().map(controller -> {
                String controllerName = controller.getName();
                if (!controller.isConnected())
                    return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.RED));
                else if (!controller.isGamepad())
                    return option.getDisplayText(new LiteralText(controllerName).formatted(Formatting.GOLD));
                else
                    return option.getDisplayText(new LiteralText(controllerName));
            }).orElse(option.getDisplayText(SpruceTexts.OPTIONS_OFF.shallowCopy().formatted(Formatting.RED))),
            new TranslatableText("lambdacontrols.tooltip.controller2"));
    private final SpruceOption unfocusedInputOption;
    private final SpruceOption invertsRightXAxis;
    private final SpruceOption invertsRightYAxis;
    private final SpruceOption rightDeadZoneOption;
    private final SpruceOption leftDeadZoneOption;
    private final SpruceOption[] maxAnalogValueOptions = new SpruceOption[]{
            maxAnalogValueOption(this.config, "lambdacontrols.menu.max_left_x_value", GLFW.GLFW_GAMEPAD_AXIS_LEFT_X),
            maxAnalogValueOption(this.config, "lambdacontrols.menu.max_left_y_value", GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y),
            maxAnalogValueOption(this.config, "lambdacontrols.menu.max_right_x_value", GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X),
            maxAnalogValueOption(this.config, "lambdacontrols.menu.max_right_y_value", GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y)
    };

    private static SpruceOption maxAnalogValueOption(LambdaControlsConfig config, String key, int axis) {
        return new SpruceDoubleOption(key, .25f, 1.f, 0.05f,
                () -> config.getAxisMaxValue(axis),
                newValue -> config.setAxisMaxValue(axis, newValue),
                option -> option.getDisplayText(new LiteralText(String.format("%.2f", option.get()))),
                new TranslatableText(key.replace("menu", "tooltip"))
        );
    }

    private final MutableText controllerMappingsUrlText = new LiteralText("(")
            .append(new LiteralText(GAMEPAD_TOOL_URL).formatted(Formatting.GOLD))
            .append("),");

    private static int searchNextAvailableController(int newId, boolean allowNone) {
        if ((allowNone && newId == -1) || newId == 0) return newId;

        boolean connected = Controller.byId(newId).isConnected();
        if (!connected) {
            newId++;
        }

        if (newId > GLFW.GLFW_JOYSTICK_LAST)
            newId = allowNone ? -1 : GLFW.GLFW_JOYSTICK_1;

        return connected ? newId : searchNextAvailableController(newId, allowNone);
    }

    public LambdaControlsSettingsScreen(Screen parent, boolean hideControls) {
        super(new TranslatableText("lambdacontrols.title.settings"));
        this.parent = parent;
        // General options
        this.inputModeOption = new SpruceCyclingOption("lambdacontrols.menu.controls_mode",
                amount -> {
                    ControlsMode next = this.config.getControlsMode().next();
                    this.config.setControlsMode(next);
                    this.config.save();

                    if (this.client.player != null) {
                        ClientPlayNetworking.getSender().sendPacket(LambdaControls.CONTROLS_MODE_CHANNEL, this.mod.makeControlsModeBuffer(next));
                    }
                }, option -> option.getDisplayText(new TranslatableText(this.config.getControlsMode().getTranslationKey())),
                new TranslatableText("lambdacontrols.tooltip.controls_mode"));
        this.autoSwitchModeOption = new SpruceToggleBooleanOption("lambdacontrols.menu.auto_switch_mode", this.config::hasAutoSwitchMode,
                this.config::setAutoSwitchMode, new TranslatableText("lambdacontrols.tooltip.auto_switch_mode"));
        this.rotationSpeedOption = new SpruceDoubleOption("lambdacontrols.menu.rotation_speed", 0.0, 100.0, .5f,
                this.config::getRotationSpeed,
                newValue -> {
                    synchronized (this.config) {
                        this.config.setRotationSpeed(newValue);
                    }
                }, option -> option.getDisplayText(new LiteralText(String.valueOf(option.get()))),
                new TranslatableText("lambdacontrols.tooltip.rotation_speed"));
        this.mouseSpeedOption = new SpruceDoubleOption("lambdacontrols.menu.mouse_speed", 0.0, 150.0, .5f,
                this.config::getMouseSpeed,
                newValue -> {
                    synchronized (this.config) {
                        this.config.setMouseSpeed(newValue);
                    }
                }, option -> option.getDisplayText(new LiteralText(String.valueOf(option.get()))),
                new TranslatableText("lambdacontrols.tooltip.mouse_speed"));
        this.resetOption = SpruceSimpleActionOption.reset(btn -> {
            this.config.reset();
            MinecraftClient client = MinecraftClient.getInstance();
            this.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        });
        // Gameplay options
        this.analogMovementOption = new SpruceToggleBooleanOption("lambdacontrols.menu.analog_movement",
                this.config::hasAnalogMovement, this.config::setAnalogMovement,
                new TranslatableText("lambdacontrols.tooltip.analog_movement"));
        this.autoJumpOption = new SpruceToggleBooleanOption("options.autoJump",
                () -> this.client.options.autoJump,
                newValue -> this.client.options.autoJump = newValue,
                null);
        this.fastBlockPlacingOption = new SpruceToggleBooleanOption("lambdacontrols.menu.fast_block_placing", this.config::hasFastBlockPlacing,
                this.config::setFastBlockPlacing, new TranslatableText("lambdacontrols.tooltip.fast_block_placing"));
        this.frontBlockPlacingOption = new SpruceToggleBooleanOption("lambdacontrols.menu.reacharound.horizontal", this.config::hasFrontBlockPlacing,
                this.config::setFrontBlockPlacing, new TranslatableText("lambdacontrols.tooltip.reacharound.horizontal"));
        this.verticalReacharoundOption = new SpruceToggleBooleanOption("lambdacontrols.menu.reacharound.vertical", this.config::hasVerticalReacharound,
                this.config::setVerticalReacharound, new TranslatableText("lambdacontrols.tooltip.reacharound.vertical"));
        this.flyDriftingOption = new SpruceToggleBooleanOption("lambdacontrols.menu.fly_drifting", this.config::hasFlyDrifting,
                this.config::setFlyDrifting, new TranslatableText("lambdacontrols.tooltip.fly_drifting"));
        this.flyVerticalDriftingOption = new SpruceToggleBooleanOption("lambdacontrols.menu.fly_drifting_vertical", this.config::hasFlyVerticalDrifting,
                this.config::setFlyVerticalDrifting, new TranslatableText("lambdacontrols.tooltip.fly_drifting_vertical"));
        // Appearance options
        this.controllerTypeOption = new SpruceCyclingOption("lambdacontrols.menu.controller_type",
                amount -> this.config.setControllerType(this.config.getControllerType().next()),
                option -> option.getDisplayText(this.config.getControllerType().getTranslatedText()),
                new TranslatableText("lambdacontrols.tooltip.controller_type"));
        this.virtualMouseSkinOption = new SpruceCyclingOption("lambdacontrols.menu.virtual_mouse.skin",
                amount -> this.config.setVirtualMouseSkin(this.config.getVirtualMouseSkin().next()),
                option -> option.getDisplayText(this.config.getVirtualMouseSkin().getTranslatedText()),
                null);
        this.hudEnableOption = new SpruceToggleBooleanOption("lambdacontrols.menu.hud_enable", this.config::isHudEnabled,
                this.mod::setHudEnabled, new TranslatableText("lambdacontrols.tooltip.hud_enable"));
        this.hudSideOption = new SpruceCyclingOption("lambdacontrols.menu.hud_side",
                amount -> this.config.setHudSide(this.config.getHudSide().next()),
                option -> option.getDisplayText(this.config.getHudSide().getTranslatedText()),
                new TranslatableText("lambdacontrols.tooltip.hud_side"));
        // Controller options
        this.rightDeadZoneOption = new SpruceDoubleOption("lambdacontrols.menu.right_dead_zone", 0.05, 1.0, .05f,
                this.config::getRightDeadZone,
                newValue -> {
                    synchronized (this.config) {
                        this.config.setRightDeadZone(newValue);
                    }
                }, option -> {
            String value = String.valueOf(option.get());
            return option.getDisplayText(new LiteralText(value.substring(0, Math.min(value.length(), 5))));
        }, new TranslatableText("lambdacontrols.tooltip.right_dead_zone"));
        this.leftDeadZoneOption = new SpruceDoubleOption("lambdacontrols.menu.left_dead_zone", 0.05, 1.0, .05f,
                this.config::getLeftDeadZone,
                newValue -> {
                    synchronized (this.config) {
                        this.config.setLeftDeadZone(newValue);
                    }
                }, option -> {
            String value = String.valueOf(option.get());
            return option.getDisplayText(new LiteralText(value.substring(0, Math.min(value.length(), 5))));
        }, new TranslatableText("lambdacontrols.tooltip.left_dead_zone"));
        this.invertsRightXAxis = new SpruceToggleBooleanOption("lambdacontrols.menu.invert_right_x_axis", this.config::doesInvertRightXAxis,
                newValue -> {
                    synchronized (this.config) {
                        this.config.setInvertRightXAxis(newValue);
                    }
                }, null);
        this.invertsRightYAxis = new SpruceToggleBooleanOption("lambdacontrols.menu.invert_right_y_axis", this.config::doesInvertRightYAxis,
                newValue -> {
                    synchronized (this.config) {
                        this.config.setInvertRightYAxis(newValue);
                    }
                }, null);
        this.unfocusedInputOption = new SpruceToggleBooleanOption("lambdacontrols.menu.unfocused_input", this.config::hasUnfocusedInput,
                this.config::setUnfocusedInput, new TranslatableText("lambdacontrols.tooltip.unfocused_input"));
        this.virtualMouseOption = new SpruceToggleBooleanOption("lambdacontrols.menu.virtual_mouse", this.config::hasVirtualMouse,
                this.config::setVirtualMouse, new TranslatableText("lambdacontrols.tooltip.virtual_mouse"));
    }

    @Override
    public void removed() {
        this.config.save();
        super.removed();
    }

    @Override
    public void onClose() {
        this.config.save();
        super.onClose();
    }

    private int getTextHeight() {
        return (5 + this.textRenderer.fontHeight) * 3 + 5;
    }

    @Override
    protected void init() {
        super.init();

        this.buildTabs();

        this.addChild(this.resetOption.createWidget(Position.of(this.width / 2 - 155, this.height - 29), 150));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, SpruceTexts.GUI_DONE,
                btn -> this.client.openScreen(this.parent)));
    }

    public void buildTabs() {
        SpruceTabbedWidget tabs = new SpruceTabbedWidget(Position.of(0, 24), this.width, this.height - 32 - 24,
                null,
                Math.max(116, this.width / 8), 0);
        this.addChild(tabs);

        tabs.addSeparatorEntry(new TranslatableText("lambdacontrols.menu.separator.general"));
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.general"), null,
                this::buildGeneralTab);
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.gameplay"), null,
                this::buildGameplayTab);
        tabs.addTabEntry(new TranslatableText("lambdacontrols.menu.title.visual"), null,
                this::buildVisualTab);

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
        list.addSingleOptionEntry(this.rotationSpeedOption);
        list.addSingleOptionEntry(this.mouseSpeedOption);
        list.addSingleOptionEntry(this.virtualMouseOption);
        return list;
    }

    public SpruceOptionListWidget buildGameplayTab(int width, int height) {
        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.origin(), width, height);
        list.addSingleOptionEntry(this.analogMovementOption);
        list.addSingleOptionEntry(this.fastBlockPlacingOption);
        list.addSingleOptionEntry(this.frontBlockPlacingOption);
        list.addSingleOptionEntry(this.verticalReacharoundOption);
        list.addSingleOptionEntry(this.flyDriftingOption);
        list.addSingleOptionEntry(this.flyVerticalDriftingOption);
        list.addSingleOptionEntry(this.autoJumpOption);
        return list;
    }

    public SpruceOptionListWidget buildVisualTab(int width, int height) {
        SpruceOptionListWidget list = new SpruceOptionListWidget(Position.origin(), width, height);
        list.addSingleOptionEntry(this.controllerTypeOption);
        list.addSingleOptionEntry(this.virtualMouseSkinOption);
        list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.hud", true, null));
        list.addSingleOptionEntry(this.hudEnableOption);
        list.addSingleOptionEntry(this.hudSideOption);
        return list;
    }

    public ControllerControlsWidget buildControllerControlsTab(int width, int height) {
        return new ControllerControlsWidget(Position.origin(), width, height);
    }

    public AbstractSpruceWidget buildControllerTab(int width, int height) {
        SpruceContainerWidget root = new SpruceContainerWidget(Position.origin(), width, height);

        SpruceLabelWidget aboutMappings1 = new SpruceLabelWidget(Position.of(0, 2),
                new TranslatableText("lambdacontrols.controller.mappings.1", SDL2_GAMEPAD_TOOL),
                width, true);

        SpruceLabelWidget gamepadToolUrlLabel = new SpruceLabelWidget(Position.of(0, aboutMappings1.getHeight() + 4),
                this.controllerMappingsUrlText, width,
                label -> Util.getOperatingSystem().open(GAMEPAD_TOOL_URL), true);
        gamepadToolUrlLabel.setTooltip(new TranslatableText("chat.link.open"));

        SpruceLabelWidget aboutMappings3 = new SpruceLabelWidget(Position.of(0,
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
        list.addSingleOptionEntry(this.unfocusedInputOption);
        list.addOptionEntry(this.invertsRightXAxis, this.invertsRightYAxis);
        list.addSingleOptionEntry(this.rightDeadZoneOption);
        list.addSingleOptionEntry(this.leftDeadZoneOption);
        for (SpruceOption option : this.maxAnalogValueOptions) {
            list.addSingleOptionEntry(option);
        }

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
    }
}
