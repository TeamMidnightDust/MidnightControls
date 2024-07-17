package eu.midnightdust.midnightcontrols.client.compat;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.screen.EmiScreenManager;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EMICompat implements CompatHandler {
    public static boolean handleEmiPages(boolean direction) {
        if (isEMIEnabled() && MidnightControlsClient.input.actionGuiCooldown == 0 && EmiScreenManager.getSearchPanel() != null && EmiScreenManager.getSearchPanel().pageLeft != null && EmiScreenManager.getSearchPanel().pageRight != null) {
            if (direction) EmiScreenManager.getSearchPanel().pageRight.onPress();
            else EmiScreenManager.getSearchPanel().pageLeft.onPress();
            MidnightControlsClient.input.actionGuiCooldown = 5;
            return true;
        }
        return false;
    }
    @Override
    public void handle() {
        ButtonCategory category = new ButtonCategory(Identifier.of("midnightcontrols","category.emi"));
        InputManager.registerCategory(category);
        new ButtonBinding.Builder("emi_page_left")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true))
                .category(category)
                .action((client,action,value,buttonState)->handleEmiPages(false)).cooldown()
                .filter(((client, buttonBinding) -> EmiApi.getHandledScreen() != null))
                .register();
        new ButtonBinding.Builder("emi_page_right")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true))
                .category(category)
                .action((client,action,value,buttonState)->handleEmiPages(true)).cooldown()
                .filter(((client, buttonBinding) -> EmiApi.getHandledScreen() != null))
                .register();
    }
    public static boolean isEMIEnabled() {
        return EmiConfig.enabled;
    }
    public static boolean isSearchBarCentered() {
        return EmiConfig.centerSearchBar;
    }
}
