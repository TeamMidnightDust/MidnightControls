package eu.midnightdust.midnightcontrols.client.compat;

import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.controllers.slider.SliderControllerElement;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

public class YACLCompat implements CompatHandler {
    public static boolean handleAButton(Screen screen, GuiEventListener element) {
        if (element instanceof AbstractWidget abstractWidget) {
            // imitate enter key press
            return abstractWidget.keyPressed(MidnightInput.ENTER_KEY_INPUT);
        }
        return false;
    }

    public static boolean handleLeftRight(Screen screen, boolean direction) {
        if (screen instanceof YACLScreen yaclScreen) {
            SliderControllerElement focusedSlider = yaclScreen.children().stream()
                    .filter(OptionListWidget.OptionEntry.class::isInstance)
                    .map(entry -> ((OptionListWidget.OptionEntry) entry).widget)
                    .filter(ControllerWidget.class::isInstance)
                    .map(ControllerWidget.class::cast)
                    .filter(SliderControllerElement.class::isInstance)
                    .map(SliderControllerElement.class::cast)
                    .filter(ControllerWidget::isHovered)
                    .findFirst()
                    .orElse(null);

            if (focusedSlider == null)
                return false;

            focusedSlider.incrementValue(direction ? 1 : -1);
            return true;
        }

        return false;
    }

//    @Override
//    public boolean handleTabs(Screen screen, boolean direction) {
//        if (screen instanceof YACLScreen yaclScreen) {
//            int categoryIdx = yaclScreen.tabNavigationBar.getTabs().indexOf(yaclScreen.tabManager.getCurrentTab());
//            if (direction) categoryIdx++; else categoryIdx--;
//            if (categoryIdx < 0) categoryIdx = yaclScreen.config.categories().size() - 1;
//            if (categoryIdx >= yaclScreen.config.categories().size()) categoryIdx = 0;
//
//            yaclScreen.tabNavigationBar.selectTab(categoryIdx, true);
//            return true;
//        }
//        return false;
//    }
}
