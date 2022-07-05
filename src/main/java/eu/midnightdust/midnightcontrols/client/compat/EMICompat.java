package eu.midnightdust.midnightcontrols.client.compat;

import dev.emi.emi.screen.EmiScreen;
import dev.emi.emi.screen.EmiScreenManager;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class EMICompat implements CompatHandler {
    public static boolean handleTabs(boolean direction) {
        if (MidnightControlsClient.get().input.actionGuiCooldown == 0 &&EmiScreenManager.searchLeft != null && EmiScreenManager.searchRight != null) {
            if (direction) EmiScreenManager.searchRight.onPress();
            else EmiScreenManager.searchLeft.onPress();
            MidnightControlsClient.get().input.actionGuiCooldown = 5;
            return true;
        }
        return false;
    }
    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        ButtonCategory category = new ButtonCategory(new Identifier("midnightcontrols","category.emi"));
        InputManager.registerCategory(category);
        new ButtonBinding.Builder("emi_page_left")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true))
                .category(category)
                .action((client,action,value,buttonState)->handleTabs(false)).cooldown()
                .filter(((client, buttonBinding) -> client.currentScreen instanceof HandledScreen<?> || client.currentScreen instanceof EmiScreen))
                .register();
        new ButtonBinding.Builder("emi_page_right")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true))
                .category(category)
                .action((client,action,value,buttonState)->handleTabs(true)).cooldown()
                .filter(((client, buttonBinding) -> client.currentScreen instanceof HandledScreen<?> || client.currentScreen instanceof EmiScreen))
                .register();
    }
}
