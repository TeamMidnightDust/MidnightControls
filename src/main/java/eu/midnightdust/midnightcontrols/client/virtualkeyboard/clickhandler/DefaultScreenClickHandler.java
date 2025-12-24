package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static eu.midnightdust.midnightcontrols.client.MidnightInput.ENTER_KEY_INPUT;

public class DefaultScreenClickHandler extends AbstractScreenClickHandler<Screen> {

    private Screen parentScreen;
    private List<Integer> textFieldElementPath;

    @Override
    public void handle(Screen screen, double mouseX, double mouseY) {
        var textField = findClickedTextField(screen.children(), mouseX, mouseY);
        if (textField == null) {
            return;
        }

        this.parentScreen = screen;
        this.textFieldElementPath = calculatePathToElement(screen, textField.asElement());

        var virtualKeyboardScreen = new VirtualKeyboardScreen(textField.getText(), this::handleKeyboardClose, false);
        client.setScreen(virtualKeyboardScreen);
    }

    private void handleKeyboardClose(String newText) {
        if (this.parentScreen == null || this.textFieldElementPath == null) {
            return;
        }

        client.setScreen(this.parentScreen);
        TextFieldWrapper textField = findTextFieldByPath(this.parentScreen, this.textFieldElementPath);
        if (textField == null) {
            return;
        }

        textField.setText(newText);

        switch (this.parentScreen) {
            case CreativeModeInventoryScreen creativeInventoryScreen -> {
                var accessor = (CreativeInventoryScreenAccessor) creativeInventoryScreen;
                accessor.midnightcontrols$search();
            }
            case ChatScreen chatScreen -> {
                // send the chat message
                chatScreen.keyPressed(ENTER_KEY_INPUT);
            }
            default -> {
            }
        }
    }


    private TextFieldWrapper findClickedTextField(List<? extends GuiEventListener> elements, double mouseX, double mouseY) {
        for (GuiEventListener element : elements) {
            if (TextFieldWrapper.isValidTextField(element)) {
                TextFieldWrapper textField = new TextFieldWrapper(element);
                if (textField.isMouseOver(mouseX, mouseY) && textField.isFocused()) {
                    return textField;
                }
            }

            if (element instanceof ContainerEventHandler parentElement) {
                TextFieldWrapper found = findClickedTextField(parentElement.children(), mouseX, mouseY);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * Calculates the path between a parent and a target in the UI hierarchy
     */
    protected List<Integer> calculatePathToElement(GuiEventListener parent, GuiEventListener target) {
        if (!(parent instanceof ContainerEventHandler parentElement)) {
            return null;
        }

        List<? extends GuiEventListener> children = parentElement.children();

        for (int i = 0; i < children.size(); i++) {
            GuiEventListener child = children.get(i);

            if (child == target) {
                return Collections.singletonList(i);
            }

            if (child instanceof ContainerEventHandler) {
                List<Integer> subPath = calculatePathToElement(child, target);
                if (subPath != null) {
                    List<Integer> fullPath = new ArrayList<>(subPath.size() + 1);
                    fullPath.add(i);
                    fullPath.addAll(subPath);
                    return fullPath;
                }
            }
        }

        return null;
    }

    protected TextFieldWrapper findTextFieldByPath(GuiEventListener parent, List<Integer> path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        if (!(parent instanceof ContainerEventHandler parentElement)) {
            return null;
        }

        List<? extends GuiEventListener> children = parentElement.children();
        int index = path.get(0);

        if (index < 0 || index >= children.size()) {
            return null;
        }

        GuiEventListener child = children.get(index);

        if (path.size() == 1) {
            return TextFieldWrapper.isValidTextField(child) ? new TextFieldWrapper(child) : null;
        }

        if (child instanceof ContainerEventHandler) {
            return findTextFieldByPath(child, path.subList(1, path.size()));
        }

        return null;
    }
}
