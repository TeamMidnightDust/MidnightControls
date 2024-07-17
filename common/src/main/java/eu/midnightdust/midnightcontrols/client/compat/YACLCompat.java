package eu.midnightdust.midnightcontrols.client.compat;

import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.OptionListWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import dev.isxander.yacl.gui.controllers.slider.SliderControllerElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class YACLCompat implements CompatHandler {
    public static boolean handleAButton(Screen screen, Element element) {
        if (element instanceof AbstractWidget abstractWidget) {
            // imitate enter key press
            return abstractWidget.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0);
        }
        return false;
    }

    public static boolean handleLeftRight(Screen screen, boolean direction) {
        if (screen instanceof YACLScreen yaclScreen) {
            SliderControllerElement focusedSlider = yaclScreen.optionList.children().stream()
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

    @Override
    public boolean handleTabs(Screen screen, boolean direction) {
        if (screen instanceof YACLScreen yaclScreen) {
            int categoryIdx = yaclScreen.getCurrentCategoryIdx();
            if (direction) categoryIdx++; else categoryIdx--;
            if (categoryIdx < 0) categoryIdx = yaclScreen.config.categories().size() - 1;
            if (categoryIdx >= yaclScreen.config.categories().size()) categoryIdx = 0;

            yaclScreen.changeCategory(categoryIdx);
            return true;
        }
        return false;
    }
}
