package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.thinkingstudio.obsidianui.widget.text.SpruceTextFieldWidget;

public record TextFieldWrapper(Object textField) {

    public TextFieldWrapper {
        if (!isValidTextField(textField)) {
            throw new IllegalArgumentException("Type " + textField.getClass() + " is not marked as a valid text field");
        }
    }

    Element asElement() {
        return (Element) textField;
    }

    String getText() {
        switch (textField) {
            case SpruceTextFieldWidget spruceTextField -> {
                return spruceTextField.getText();
            }
            case TextFieldWidget vanillaTextField -> {
                return vanillaTextField.getText();
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
            case TextFieldWidget vanillaTextField -> {
                vanillaTextField.setText(text);
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
            case TextFieldWidget vanillaTextField -> {
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
            case TextFieldWidget vanillaTextField -> {
                return vanillaTextField.isFocused();
            }
            default -> {
                return false;
            }
        }
    }

    static boolean isValidTextField(Object textField) {
        return textField instanceof TextFieldWidget || textField instanceof SpruceTextFieldWidget;
    }
}
