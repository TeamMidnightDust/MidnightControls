package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            case CreativeInventoryScreen creativeInventoryScreen -> {
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


    private TextFieldWrapper findClickedTextField(List<? extends Element> elements, double mouseX, double mouseY) {
        for (Element element : elements) {
            if (TextFieldWrapper.isValidTextField(element)) {
                TextFieldWrapper textField = new TextFieldWrapper(element);
                if (textField.isMouseOver(mouseX, mouseY) && textField.isFocused()) {
                    return textField;
                }
            }

            if (element instanceof ParentElement parentElement) {
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
    protected List<Integer> calculatePathToElement(Element parent, Element target) {
        if (!(parent instanceof ParentElement parentElement)) {
            return null;
        }

        List<? extends Element> children = parentElement.children();

        for (int i = 0; i < children.size(); i++) {
            Element child = children.get(i);

            if (child == target) {
                return Collections.singletonList(i);
            }

            if (child instanceof ParentElement) {
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

    protected TextFieldWrapper findTextFieldByPath(Element parent, List<Integer> path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        if (!(parent instanceof ParentElement parentElement)) {
            return null;
        }

        List<? extends Element> children = parentElement.children();
        int index = path.get(0);

        if (index < 0 || index >= children.size()) {
            return null;
        }

        Element child = children.get(index);

        if (path.size() == 1) {
            return TextFieldWrapper.isValidTextField(child) ? new TextFieldWrapper(child) : null;
        }

        if (child instanceof ParentElement) {
            return findTextFieldByPath(child, path.subList(1, path.size()));
        }

        return null;
    }
}
