package eu.midnightdust.midnightcontrols.client.util;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class ToggleSneakSprintUtil {
    public static boolean toggleSneak(ButtonBinding button) {
        if (client.player == null) return false;
        boolean isFlying = client.player.getAbilities().flying;
        var option = client.options.toggleCrouch();

        button.asKeyBinding().ifPresent(binding -> {
            boolean sneakToggled = option.get();
            if (isFlying && sneakToggled)
                option.set(false);
            else if (MidnightControlsConfig.controllerToggleSneak != sneakToggled)
                option.set(!sneakToggled);
            binding.setDown(button.isPressed());
            if (isFlying && sneakToggled)
                option.set(true);
            else if (MidnightControlsConfig.controllerToggleSneak != sneakToggled)
                option.set(sneakToggled);
        });
        return true;
    }
    public static boolean toggleSprint(ButtonBinding button) {
        if (client.player == null) return false;
        boolean isFlying = client.player.getAbilities().flying;
        var option = client.options.toggleSprint();

        button.asKeyBinding().ifPresent(binding -> {
            boolean sprintToggled = option.get();
            if (isFlying && sprintToggled)
                option.set(false);
            else if (MidnightControlsConfig.controllerToggleSprint != sprintToggled)
                option.set(!sprintToggled);
            binding.setDown(button.isPressed());
            if (client.player.getAbilities().flying && sprintToggled)
                option.set(true);
            else if (MidnightControlsConfig.controllerToggleSprint != sprintToggled)
                option.set(sprintToggled);
        });
        return true;
    }
}
