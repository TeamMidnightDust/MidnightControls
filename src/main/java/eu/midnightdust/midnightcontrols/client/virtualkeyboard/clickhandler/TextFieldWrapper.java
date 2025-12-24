package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;

public record TextFieldWrapper(Object textField) {

    public TextFieldWrapper {
        if (!isValidTextField(textField)) {
            throw new IllegalArgumentException("Type " + textField.getClass() + " is not marked as a valid text field");
        }
    }

    GuiEventListener asElement() {
        return (GuiEventListener) textField;
    }

    String getText() {
        switch (textField) {
            case SpruceTextFieldWidget spruceTextField -> {
                return spruceTextField.getText();
            }
            case EditBox vanillaTextField -> {
                return vanillaTextField.getValue();
            }
            default -> {
                return null;
            }
        }
    }

    void setText(String text) {
        switch (textField) {
            case SpruceTextFieldWidget spruceTextField -> {
                spruceTextField.setText(text);
            }
            case EditBox vanillaTextField -> {
                vanillaTextField.setValue(text);
            }
            default -> {
            }
        }
    }

    boolean isMouseOver(double mouseX, double mouseY) {
        switch (textField) {
            case SpruceTextFieldWidget spruceTextField -> {
                return spruceTextField.isMouseOver(mouseX, mouseY);
            }
            case EditBox vanillaTextField -> {
                return vanillaTextField.isMouseOver(mouseX, mouseY);
            }
            default -> {
                return false;
            }
        }
    }

    boolean isFocused() {
        switch (textField) {
            case SpruceTextFieldWidget spruceTextField -> {
                return spruceTextField.isFocused();
            }
            case EditBox vanillaTextField -> {
                return vanillaTextField.isFocused();
            }
            default -> {
                return false;
            }
        }
    }

    static boolean isValidTextField(Object textField) {
        return textField instanceof EditBox || textField instanceof SpruceTextFieldWidget;
    }
}
