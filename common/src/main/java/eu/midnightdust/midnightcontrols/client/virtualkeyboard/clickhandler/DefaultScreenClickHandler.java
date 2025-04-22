package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class DefaultScreenClickHandler extends AbstractScreenClickHandler<Screen> {

    private Screen parentScreen;
    private List<Integer> textFieldElementPath;

    @Override
    public void handle(Screen screen, double mouseX, double mouseY) {
        var textField = findClickedTextField(screen, mouseX, mouseY);
        if (textField == null) {
            return;
        }

        this.parentScreen = screen;
        this.textFieldElementPath = calculatePathToElement(screen, textField);

        var virtualKeyboardScreen = new VirtualKeyboardScreen(textField.getText(), this::handleKeyboardClose, false);
        client.setScreen(virtualKeyboardScreen);
    }

    private void handleKeyboardClose(String newText) {
        if(this.parentScreen == null || this.textFieldElementPath == null) {
            return;
        }

        client.setScreen(this.parentScreen);
        TextFieldWidget textField = findTextFieldByPath(this.parentScreen, this.textFieldElementPath);
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
                chatScreen.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0);
            }
            default -> {}
        }
    }

    private TextFieldWidget findClickedTextField(Screen screen, double mouseX, double mouseY) {
        for (Element element : screen.children()) {
            if (element instanceof TextFieldWidget textField) {
                if (textField.isMouseOver(mouseX, mouseY) && textField.isFocused()) {
                    return textField;
                }
            }
        }

        // not hovering over a text field
        return null;
    }

    /**
     * Calculates the path between a parent and a target in the UI hierarchy
     */
    protected List<Integer> calculatePathToElement(Element parent, Element target) {
        if (parent instanceof ParentElement parentElement) {
            List<? extends Element> children = parentElement.children();

            // check direct children first
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == target) {
                    // found it, return the path to this element
                    return Collections.singletonList(i);
                }
            }

            // check each child's children
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) instanceof ParentElement childParent) {
                    List<Integer> subPath = calculatePathToElement(childParent, target);
                    if (subPath != null) {
                        // found in this subtree, prepend current index
                        List<Integer> fullPath = new ArrayList<>();
                        fullPath.add(i);
                        fullPath.addAll(subPath);
                        return fullPath;
                    }
                }
            }
        }

        // Not found
        return null;
    }

    protected TextFieldWidget findTextFieldByPath(Element parent, List<Integer> path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        if (parent instanceof ParentElement parentElement) {
            List<? extends Element> children = parentElement.children();
            int index = path.getFirst();

            if (index >= 0 && index < children.size()) {
                Element child = children.get(index);

                if (path.size() == 1) {
                    // This should be our target
                    return (child instanceof TextFieldWidget) ? (TextFieldWidget) child : null;
                } else {
                    // Continue traversing
                    if (child instanceof ParentElement) {
                        return findTextFieldByPath(child, path.subList(1, path.size()));
                    }
                }
            }
        }

        return null;
    }
}
