package eu.midnightdust.midnightcontrols.client.gui.config;

import dev.lambdaurora.spruceui.SpruceTexts;
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static eu.midnightdust.midnightcontrols.client.gui.MidnightControlsSettingsScreen.searchNextAvailableController;

public class ControllerSelectionButton {
    public static void add(MidnightConfigListWidget list, MidnightConfigScreen screen, boolean second) {
        SpriteIconButton resetButton = SpriteIconButton.builder(Component.translatable("controls.reset"), (button -> {
            if (second) MidnightControlsConfig.secondControllerID = -1;
            else MidnightControlsConfig.controllerID = 0;
            screen.updateList();
        }), true).sprite(Identifier.fromNamespaceAndPath("midnightlib","icon/reset"), 12, 12).size(20, 20).build();
        resetButton.setPosition(screen.width - 205 + 150 + 25, 0);
        Button editButton = Button.builder(getControllerName(second),
                button -> {
                    int id = second ? MidnightControlsConfig.getSecondController().map(Controller::id).orElse(-1) : MidnightControlsConfig.getController().id();
                    id += 1;
                    if (id > GLFW.GLFW_JOYSTICK_LAST)
                        id = GLFW.GLFW_JOYSTICK_1;
                    id = searchNextAvailableController(id, second);
                    if (second) {
                        MidnightControlsConfig.setSecondController(Controller.byId(id));
                    } else {
                        MidnightControlsConfig.setController(Controller.byId(id));
                    }
                    if (MidnightControlsConfig.debug && id != -1) System.out.println(Controller.byId(id).getName() + "'s Controller GUID: " + Controller.byId(id).getGuid());

                    resetButton.active = second ? MidnightControlsConfig.getSecondController().isPresent() : false;
                    button.setMessage(getControllerName(second));
                }).bounds(screen.width - 185, 0, 150, 20).build();
        resetButton.active = second ? MidnightControlsConfig.getSecondController().isPresent() : false;
        if (second) editButton.setTooltip(Tooltip.create(Component.translatable("midnightcontrols.menu.controller2.tooltip")));

        list.addButton(List.of(editButton, resetButton), Component.translatable(second ? "midnightcontrols.menu.controller2" : "midnightcontrols.menu.controller"), new EntryInfo(null, screen.modid));
    }

    private static Component getControllerName(boolean second) {
        if (second && MidnightControlsConfig.getSecondController().isEmpty()) return SpruceTexts.OPTIONS_OFF.plainCopy().withStyle(ChatFormatting.RED);

        var controller = second ? MidnightControlsConfig.getSecondController().get() : MidnightControlsConfig.getController();
        var controllerName = controller.getName();
        if (!controller.isConnected())
            return Component.literal(controllerName).withStyle(ChatFormatting.RED);
        else if (!controller.isGamepad())
            return Component.literal(controllerName).withStyle(ChatFormatting.GOLD);
        else
            return Component.literal(controllerName);
    }
}
