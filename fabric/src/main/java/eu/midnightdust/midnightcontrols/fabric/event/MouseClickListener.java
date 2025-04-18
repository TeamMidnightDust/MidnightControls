package eu.midnightdust.midnightcontrols.fabric.event;
import eu.midnightdust.midnightcontrols.client.gui.VirtualKeyboardScreen;
import eu.midnightdust.midnightcontrols.client.mixin.AbstractSignEditScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.BookEditScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eu.midnightdust.midnightcontrols.MidnightControls.logger;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

record ScreenLink(Screen screen, List<Integer> elementPath) {}

public class MouseClickListener implements ScreenMouseEvents.AllowMouseClick {
    private final Screen screen;

    private ScreenLink link;

    public MouseClickListener(Screen screen) {
        this.screen = screen;
    }

    @Override
    public boolean allowMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        interceptMouseClick(screen, mouseX, mouseY);
        return true;
    }

    private void interceptMouseClick(Screen screen, double mouseX, double mouseY) {
        logger.info("In scr: {}", screen.getClass());
        switch(screen) {
            case BookEditScreen bookEditScreen -> {
                if(screen.hoveredElement(mouseX, mouseY).isPresent()) {
                    return;
                }
                handleBookEditScreenClick(bookEditScreen);
            }
            case SignEditScreen signEditScreen -> {
                if(screen.hoveredElement(mouseX, mouseY).isPresent()) {
                    return;
                }
                handleSignEditScreenClick(signEditScreen);
            }
            default -> {
                var textField = findClickedTextField(screen, mouseX, mouseY);
                if (textField == null) {
                    return;
                }
                handleTextFieldClick(textField);
            }
        }
    }

    // Add equals and hashCode to prevent duplicate registrations
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MouseClickListener) {
            return ((MouseClickListener) obj).screen == this.screen;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return screen.hashCode();
    }

    // handlers

    private void handleBookEditScreenClick(BookEditScreen bookEditScreen) {
        var accessor = (BookEditScreenAccessor) screen;

        VirtualKeyboardScreen virtualKeyboardScreen;
        if(accessor.midnightcontrols$isSigning()) {
            virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getTitle(), (text) -> {
                client.setScreen(bookEditScreen);
                accessor.midnightcontrols$setTitle(text);
            });
        }
        else {
            virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getCurrentPageContent(), (text) -> {
                client.setScreen(bookEditScreen);
                accessor.midnightcontrols$setPageContent(text);
            });
        }

        client.setScreen(virtualKeyboardScreen);
    }

    private void handleSignEditScreenClick(SignEditScreen signEditScreen) {
        var accessor = (AbstractSignEditScreenAccessor) signEditScreen;
        // TODO
    }

    private void handleTextFieldClick(TextFieldWidget textField) {
        this.link = new ScreenLink(screen, calculatePathToElement(screen, textField));
        var virtualKeyboardScreen = new VirtualKeyboardScreen(textField.getText(), this::handleKeyboardClose);
        client.setScreen(virtualKeyboardScreen);
    }

    private void handleKeyboardClose(String newText) {
        if(this.link == null) {
            return;
        }

        client.setScreen(this.link.screen());
        var txtField = findTextFieldByPath(screen, this.link.elementPath());
        if (txtField == null) {
            return;
        }

        txtField.setText(newText);


        switch (this.link.screen()) {
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

    // utility

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
    private List<Integer> calculatePathToElement(Element parent, Element target) {
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

    private TextFieldWidget findTextFieldByPath(Element parent, List<Integer> path) {
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
