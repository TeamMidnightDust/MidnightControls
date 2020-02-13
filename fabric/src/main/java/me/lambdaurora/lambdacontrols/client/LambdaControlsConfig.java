/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client;

import com.electronwill.nightconfig.core.file.FileConfig;
import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControlsFeature;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;

/**
 * Represents LambdaControls configuration.
 */
public class LambdaControlsConfig
{
    // General
    private static final ControlsMode   DEFAULT_CONTROLS_MODE          = ControlsMode.DEFAULT;
    private static final boolean        DEFAULT_AUTO_SWITCH_MODE       = false;
    // HUD
    private static final boolean        DEFAULT_HUD_ENABLE             = true;
    private static final HudSide        DEFAULT_HUD_SIDE               = HudSide.LEFT;
    // Gameplay
    private static final boolean        DEFAULT_FRONT_BLOCK_PLACING    = false;
    private static final boolean        DEFAULT_FAST_BLOCK_INTERACTION = true;
    private static final boolean        DEFAULT_FLY_DRIFTING           = false;
    private static final boolean        DEFAULT_FLY_VERTICAL_DRIFTING  = true;
    // Controller
    private static final ControllerType DEFAULT_CONTROLLER_TYPE        = ControllerType.DEFAULT;
    private static final double         DEFAULT_DEAD_ZONE              = 0.25;
    private static final double         DEFAULT_ROTATION_SPEED         = 40.0;
    private static final double         DEFAULT_MOUSE_SPEED            = 25.0;
    private static final boolean        DEFAULT_UNFOCUSED_INPUT        = false;

    private static final Pattern BUTTON_BINDING_PATTERN = Pattern.compile("(-?\\d+)\\+?");

    protected final FileConfig           config = FileConfig.builder("config/lambdacontrols.toml").concurrent().defaultResource("/config.toml").build();
    private final   LambdaControlsClient mod;
    private         ControlsMode         controlsMode;
    private         ControllerType       controllerType;
    // HUD settings.
    private         boolean              hudEnable;
    private         HudSide              hudSide;
    // Controller settings
    private         double               deadZone;
    private         double               rotationSpeed;
    private         double               mouseSpeed;
    private         boolean              unfocusedInput;

    public LambdaControlsConfig(@NotNull LambdaControlsClient mod)
    {
        this.mod = mod;
    }

    /**
     * Loads the configuration
     */
    public void load()
    {
        this.config.load();
        this.checkAndFix();
        this.mod.log("Configuration loaded.");
        this.controlsMode = ControlsMode.byId(this.config.getOrElse("controls", DEFAULT_CONTROLS_MODE.getName())).orElse(DEFAULT_CONTROLS_MODE);
        // HUD settings.
        this.hudEnable = this.config.getOrElse("hud.enable", DEFAULT_HUD_ENABLE);
        this.hudSide = HudSide.byId(this.config.getOrElse("hud.side", DEFAULT_HUD_SIDE.getName())).orElse(DEFAULT_HUD_SIDE);
        // Gameplay
        LambdaControlsFeature.FRONT_BLOCK_PLACING.setEnabled(this.config.getOrElse("gameplay.front_block_placing", DEFAULT_FRONT_BLOCK_PLACING));
        LambdaControlsFeature.FAST_BLOCK_INTERACTION.setEnabled(this.config.getOrElse("gameplay.fast_block_interaction", DEFAULT_FAST_BLOCK_INTERACTION));
        // Controller settings.
        this.controllerType = ControllerType.byId(this.config.getOrElse("controller.type", DEFAULT_CONTROLLER_TYPE.getName())).orElse(DEFAULT_CONTROLLER_TYPE);
        this.deadZone = this.config.getOrElse("controller.dead_zone", DEFAULT_DEAD_ZONE);
        this.rotationSpeed = this.config.getOrElse("controller.rotation_speed", DEFAULT_ROTATION_SPEED);
        this.mouseSpeed = this.config.getOrElse("controller.mouse_speed", DEFAULT_MOUSE_SPEED);
        this.unfocusedInput = this.config.getOrElse("controller.unfocused_input", DEFAULT_UNFOCUSED_INPUT);
        // Controller controls.
        InputManager.loadButtonBindings(this);
    }

    /**
     * Saves the configuration.
     */
    public void save()
    {
        this.config.set("controller.dead_zone", this.deadZone);
        this.config.set("controller.rotation_speed", this.rotationSpeed);
        this.config.set("controller.mouse_speed", this.mouseSpeed);
        this.config.set("controller.unfocused_input", this.unfocusedInput);
        this.config.save();
        this.mod.log("Configuration saved.");
    }

    public void checkAndFix()
    {
        InputManager.streamBindings().forEach(binding -> {
            String path = "controller.controls." + binding.getName();
            Object raw = this.config.getRaw(path);
            if (raw instanceof Number) {
                this.mod.warn("Invalid data at \"" + path + "\", fixing...");
                this.config.set(path, String.valueOf(raw));
            }
        });
    }

    /**
     * Resets the configuration to default values.
     */
    public void reset()
    {
        // General
        this.setControlsMode(DEFAULT_CONTROLS_MODE);
        this.setAutoSwitchMode(DEFAULT_AUTO_SWITCH_MODE);
        // HUD
        this.setHudEnabled(DEFAULT_HUD_ENABLE);
        this.setHudSide(DEFAULT_HUD_SIDE);
        // Gameplay
        this.setFrontBlockPlacing(DEFAULT_FRONT_BLOCK_PLACING);
        this.setFastBlockInteraction(DEFAULT_FAST_BLOCK_INTERACTION);
        this.setFlyDrifting(DEFAULT_FLY_DRIFTING);
        this.setFlyVerticalDrifting(DEFAULT_FLY_VERTICAL_DRIFTING);
        // Controller
        this.setControllerType(DEFAULT_CONTROLLER_TYPE);
        this.setDeadZone(DEFAULT_DEAD_ZONE);
        this.setRotationSpeed(DEFAULT_ROTATION_SPEED);
        this.setMouseSpeed(DEFAULT_MOUSE_SPEED);
        this.setUnfocusedInput(DEFAULT_UNFOCUSED_INPUT);

        // Collect prevents concurrent modification.
        InputManager.streamBindings().collect(Collectors.toList()).forEach(binding -> this.setButtonBinding(binding, binding.getDefaultButton()));
    }

    /**
     * Gets the controls mode from the configuration.
     *
     * @return The controls mode.
     */
    public @NotNull ControlsMode getControlsMode()
    {
        return this.controlsMode;
    }

    /**
     * Sets the controls mode in the configuration.
     *
     * @param controlsMode The controls mode.
     */
    public void setControlsMode(@NotNull ControlsMode controlsMode)
    {
        this.controlsMode = controlsMode;
        this.config.set("controls", controlsMode.getName());
    }

    /**
     * Returns whether the auto switch mode is enabled or not.
     *
     * @return True if the auto switch mode is enabled, else false.
     */
    public boolean hasAutoSwitchMode()
    {
        return this.config.getOrElse("auto_switch_mode", DEFAULT_AUTO_SWITCH_MODE);
    }

    /**
     * Sets whether the auto switch mode is enabled or not.
     *
     * @param autoSwitchMode True if the auto switch mode is enabled, else false.
     */
    public void setAutoSwitchMode(boolean autoSwitchMode)
    {
        this.config.set("auto_switch_mode", autoSwitchMode);
    }

    /*
            HUD settings
     */

    /**
     * Returns whether the HUD is enabled.
     *
     * @return True if the HUD is enabled, else false.
     */
    public boolean isHudEnabled()
    {
        return this.hudEnable;
    }

    /**
     * Sets whether the HUD is enabled.
     *
     * @param enable True if the HUD is enabled, else false.
     */
    public void setHudEnabled(boolean enable)
    {
        this.hudEnable = enable;
        this.config.set("hud.enable", this.hudEnable);
    }

    /**
     * Gets the HUD side from the configuration.
     *
     * @return The HUD side.
     */
    public @NotNull HudSide getHudSide()
    {
        return this.hudSide;
    }

    /**
     * Sets the HUD side in the configuration.
     *
     * @param hudSide The HUD side.
     */
    public void setHudSide(@NotNull HudSide hudSide)
    {
        this.hudSide = hudSide;
        this.config.set("hud.side", hudSide.getName());
    }

    /*
            Gameplay settings
     */

    /**
     * Gets whether fast block interaction is enabled or not.
     *
     * @return True if fast block interaction is enabled, else false.
     */
    public boolean hasFastBlockInteraction()
    {
        return LambdaControlsFeature.FAST_BLOCK_INTERACTION.isEnabled();
    }

    /**
     * Sets whether fast block interaction is enabled or not.
     *
     * @param enable True if fast block interaction is enabled, else false.
     */
    public void setFastBlockInteraction(boolean enable)
    {
        LambdaControlsFeature.FAST_BLOCK_INTERACTION.setEnabled(enable);
        this.config.set("gameplay.fast_block_interaction", enable);
    }

    /**
     * Returns whether front block placing is enabled or not.
     *
     * @return True if front block placing is enabled, else false.
     */
    public boolean hasFrontBlockPlacing()
    {
        return LambdaControlsFeature.FRONT_BLOCK_PLACING.isEnabled();
    }

    /**
     * Sets whether front block placing is enabled or not.
     *
     * @param enable True if front block placing is enabled, else false.
     */
    public void setFrontBlockPlacing(boolean enable)
    {
        LambdaControlsFeature.FRONT_BLOCK_PLACING.setEnabled(enable);
        this.config.set("gameplay.front_block_placing", enable);
    }

    /**
     * Returns whether fly drifting is enabled or not.
     *
     * @return True if fly drifting is enabled, else false.
     */
    public boolean hasFlyDrifting()
    {
        return this.config.getOrElse("gameplay.fly.drifting", DEFAULT_FLY_DRIFTING);
    }

    /**
     * Sets whether fly drifting is enabled or not.
     *
     * @param flyDrifting True if fly drifting is enabled, else false.
     */
    public void setFlyDrifting(boolean flyDrifting)
    {
        this.config.set("gameplay.fly.drifting", flyDrifting);
    }

    /**
     * Returns whether vertical fly drifting is enabled or not.
     *
     * @return True if vertical fly drifting is enabled, else false.
     */
    public boolean hasFlyVerticalDrifting()
    {
        return this.config.getOrElse("gameplay.fly.vertical_drifting", DEFAULT_FLY_VERTICAL_DRIFTING);
    }

    /**
     * Sets whether vertical fly drifting is enabled or not.
     *
     * @param flyDrifting True if vertical fly drifting is enabled, else false.
     */
    public void setFlyVerticalDrifting(boolean flyDrifting)
    {
        this.config.set("gameplay.fly.vertical_drifting", flyDrifting);
    }

    /*
            Controller settings
     */

    /**
     * Gets the used controller.
     *
     * @return The used controller.
     */
    public @NotNull Controller getController()
    {
        Object raw = this.config.getRaw("controller.id");
        if (raw instanceof Number) {
            return Controller.byId((Integer) raw);
        } else if (raw instanceof String) {
            return Controller.byGuid((String) raw).orElse(Controller.byId(GLFW.GLFW_JOYSTICK_1));
        }
        return Controller.byId(GLFW.GLFW_JOYSTICK_1);
    }

    /**
     * Sets the used controller.
     *
     * @param controller The used controller.
     */
    public void setController(@NotNull Controller controller)
    {
        this.config.set("controller.id", controller.getId());
    }

    /**
     * Gets the second controller (for Joy-Con supports).
     *
     * @return The second controller.
     */
    public @NotNull Optional<Controller> getSecondController()
    {
        Object raw = this.config.getRaw("controller.id2");
        if (raw instanceof Number) {
            if ((int) raw == -1)
                return Optional.empty();
            return Optional.of(Controller.byId((Integer) raw));
        } else if (raw instanceof String) {
            return Optional.of(Controller.byGuid((String) raw).orElse(Controller.byId(GLFW.GLFW_JOYSTICK_1)));
        }
        return Optional.empty();
    }

    /**
     * Sets the second controller.
     *
     * @param controller The second controller.
     */
    public void setSecondController(@Nullable Controller controller)
    {
        this.config.set("controller.id2", controller == null ? -1 : controller.getId());
    }

    /**
     * Gets the controller's type.
     *
     * @return The controller's type.
     */
    public @NotNull ControllerType getControllerType()
    {
        return this.controllerType;
    }

    /**
     * Sets the controller's type.
     *
     * @param controllerType The controller's type.
     */
    public void setControllerType(@NotNull ControllerType controllerType)
    {
        this.controllerType = controllerType;
        this.config.set("controller.type", controllerType.getName());
    }

    /**
     * Gets the controller's dead zone from the configuration.
     *
     * @return The controller's dead zone value.
     */
    public double getDeadZone()
    {
        return this.deadZone;
    }

    /**
     * Sets the controller's dead zone in the configuration.
     *
     * @param deadZone The new controller's dead zone value.
     */
    public void setDeadZone(double deadZone)
    {
        this.deadZone = deadZone;
    }

    /**
     * Gets the controller's rotation speed.
     *
     * @return The rotation speed.
     */
    public double getRotationSpeed()
    {
        return this.rotationSpeed;
    }

    /**
     * Sets the controller's rotation speed.
     *
     * @param rotationSpeed The rotation speed.
     */
    public void setRotationSpeed(double rotationSpeed)
    {
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * Gets the controller's mouse speed.
     *
     * @return The mouse speed.
     */
    public double getMouseSpeed()
    {
        return this.mouseSpeed;
    }

    /**
     * Sets the controller's mouse speed.
     *
     * @param mouseSpeed The mouse speed.
     */
    public void setMouseSpeed(double mouseSpeed)
    {
        this.mouseSpeed = mouseSpeed;
    }

    /**
     * Returns whether the right X axis is inverted or not.
     *
     * @return True if the right X axis is inverted, else false.
     */
    public boolean doesInvertRightXAxis()
    {
        return this.config.getOrElse("controller.invert_right_x_axis", false);
    }

    /**
     * Sets whether the right X axis is inverted or not.
     *
     * @param invert True if the right X axis is inverted, else false.
     */
    public void setInvertRightXAxis(boolean invert)
    {
        this.config.set("controller.invert_right_x_axis", invert);
    }

    /**
     * Returns whether the right Y axis is inverted or not.
     *
     * @return True if the right Y axis is inverted, else false.
     */
    public boolean doesInvertRightYAxis()
    {
        return this.config.getOrElse("controller.invert_right_y_axis", false);
    }

    /**
     * Sets whether the right Y axis is inverted or not.
     *
     * @param invert True if the right Y axis is inverted, else false.
     */
    public void setInvertRightYAxis(boolean invert)
    {
        this.config.set("controller.invert_right_y_axis", invert);
    }

    /**
     * Returns whether unfocused controller input is allowed or not.
     *
     * @return True if unfocused controller input is allowed, else false.
     */
    public boolean hasUnfocusedInput()
    {
        return this.unfocusedInput;
    }

    /**
     * Sets whether unfocused controller input is allowed or not.
     *
     * @param unfocusedInput True if unfocused controller input is allowed, else false.
     */
    public void setUnfocusedInput(boolean unfocusedInput)
    {
        this.unfocusedInput = unfocusedInput;
    }

    /**
     * Gets the right X axis sign.
     *
     * @return The right X axis sign.
     */
    public double getRightXAxisSign()
    {
        return this.doesInvertRightXAxis() ? -1.0 : 1.0;
    }

    /**
     * Gets the right Y axis sign.
     *
     * @return The right Y axis sign.
     */
    public double getRightYAxisSign()
    {
        return this.doesInvertRightYAxis() ? -1.0 : 1.0;
    }

    /**
     * Loads the button binding from configuration.
     *
     * @param button The button binding.
     */
    public void loadButtonBinding(@NotNull ButtonBinding button)
    {
        button.setButton(button.getDefaultButton());
        String code = this.config.getOrElse("controller.controls." + button.getName(), button.getButtonCode());

        Matcher matcher = BUTTON_BINDING_PATTERN.matcher(code);

        try {
            int[] buttons = new int[1];
            int count = 0;
            while (matcher.find()) {
                count++;
                if (count > buttons.length)
                    buttons = Arrays.copyOf(buttons, count);
                String current;
                if (!this.checkValidity(button, code, current = matcher.group(1)))
                    return;
                buttons[count - 1] = Integer.parseInt(current);
            }
            if (count == 0) {
                this.mod.warn("Malformed config value \"" + code + "\" for binding \"" + button.getName() + "\".");
                this.setButtonBinding(button, new int[]{-1});
            }

            button.setButton(buttons);
        } catch (Exception e) {
            this.mod.warn("Malformed config value \"" + code + "\" for binding \"" + button.getName() + "\".");
            this.config.set("controller.controls." + button.getName(), button.getButtonCode());
        }
    }

    private boolean checkValidity(@NotNull ButtonBinding binding, @NotNull String input, String group)
    {
        if (group == null) {
            this.mod.warn("Malformed config value \"" + input + "\" for binding \"" + binding.getName() + "\".");
            this.config.set("controller.controls." + binding.getName(), binding.getButtonCode());
            return false;
        }
        return true;
    }

    /**
     * Sets the button binding in configuration.
     *
     * @param binding The button binding.
     * @param button  The button.
     */
    public void setButtonBinding(@NotNull ButtonBinding binding, int[] button)
    {
        binding.setButton(button);
        this.config.set("controller.controls." + binding.getName(), binding.getButtonCode());
    }

    public boolean isBackButton(int btn, boolean isBtn, int state)
    {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, false) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    public boolean isForwardButton(int btn, boolean isBtn, int state)
    {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, true) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    public boolean isLeftButton(int btn, boolean isBtn, int state)
    {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, false) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    public boolean isRightButton(int btn, boolean isBtn, int state)
    {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, true) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    /**
     * Returns whether the specified axis is an axis used for movements.
     *
     * @param axis The axis index.
     * @return True if the axis is used for movements, else false.
     */
    public boolean isMovementAxis(int axis)
    {
        return axis == GLFW_GAMEPAD_AXIS_LEFT_Y || axis == GLFW_GAMEPAD_AXIS_LEFT_X;
    }
}
