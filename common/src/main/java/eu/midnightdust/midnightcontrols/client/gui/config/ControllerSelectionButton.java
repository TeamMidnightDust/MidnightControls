package eu.midnightdust.midnightcontrols.client.gui.config;

import dev.lambdaurora.spruceui.SpruceTexts;
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static eu.midnightdust.midnightcontrols.client.gui.MidnightControlsSettingsScreen.searchNextAvailableController;

public class ControllerSelectionButton {
    public static void add(MidnightConfigListWidget list, MidnightConfigScreen screen, boolean second) {
        TextIconButtonWidget resetButton = TextIconButtonWidget.builder(Text.translatable("controls.reset"), (button -> {
            if (second) MidnightControlsConfig.secondControllerID = -1;
            else MidnightControlsConfig.controllerID = 0;
            screen.updateList();
        }), true).texture(Identifier.of("midnightlib","icon/reset"), 12, 12).dimension(20, 20).build();
        resetButton.setPosition(screen.width - 205 + 150 + 25, 0);
        ButtonWidget editButton = ButtonWidget.builder(getControllerName(second),
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
                }).dimensions(screen.width - 185, 0, 150, 20).build();
        resetButton.active = second ? MidnightControlsConfig.getSecondController().isPresent() : false;
        if (second) editButton.setTooltip(Tooltip.of(Text.translatable("midnightcontrols.menu.controller2.tooltip")));

        list.addButton(List.of(editButton, resetButton), Text.translatable(second ? "midnightcontrols.menu.controller2" : "midnightcontrols.menu.controller"), new EntryInfo(null, screen.modid));
    }

    private static Text getControllerName(boolean second) {
        if (second && MidnightControlsConfig.getSecondController().isEmpty()) return SpruceTexts.OPTIONS_OFF.copyContentOnly().formatted(Formatting.RED);

        var controller = second ? MidnightControlsConfig.getSecondController().get() : MidnightControlsConfig.getController();
        var controllerName = controller.getName();
        if (!controller.isConnected())
            return Text.literal(controllerName).formatted(Formatting.RED);
        else if (!controller.isGamepad())
            return Text.literal(controllerName).formatted(Formatting.GOLD);
        else
            return Text.literal(controllerName);
    }
}
