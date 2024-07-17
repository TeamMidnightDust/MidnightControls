package eu.midnightdust.midnightcontrols.client.util;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class ToggleSneakSprintUtil {
    public static boolean toggleSneak(ButtonBinding button) {
        if (client.player == null) return false;
        boolean isFlying = client.player.getAbilities().flying;
        var option = client.options.getSneakToggled();

        button.asKeyBinding().ifPresent(binding -> {
            boolean sneakToggled = option.getValue();
            if (isFlying && sneakToggled)
                option.setValue(false);
            else if (MidnightControlsConfig.controllerToggleSneak != sneakToggled)
                option.setValue(!sneakToggled);
            binding.setPressed(button.isPressed());
            if (isFlying && sneakToggled)
                option.setValue(true);
            else if (MidnightControlsConfig.controllerToggleSneak != sneakToggled)
                option.setValue(sneakToggled);
        });
        return true;
    }
    public static boolean toggleSprint(ButtonBinding button) {
        if (client.player == null) return false;
        boolean isFlying = client.player.getAbilities().flying;
        var option = client.options.getSprintToggled();

        button.asKeyBinding().ifPresent(binding -> {
            boolean sprintToggled = option.getValue();
            if (isFlying && sprintToggled)
                option.setValue(false);
            else if (MidnightControlsConfig.controllerToggleSprint != sprintToggled)
                option.setValue(!sprintToggled);
            binding.setPressed(button.isPressed());
            if (client.player.getAbilities().flying && sprintToggled)
                option.setValue(true);
            else if (MidnightControlsConfig.controllerToggleSprint != sprintToggled)
                option.setValue(sprintToggled);
        });
        return true;
    }
}
