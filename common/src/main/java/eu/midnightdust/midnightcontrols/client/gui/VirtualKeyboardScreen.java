package eu.midnightdust.midnightcontrols.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.thinkingstudio.obsidianui.Position;
import org.thinkingstudio.obsidianui.SpruceTexts;
import org.thinkingstudio.obsidianui.screen.SpruceScreen;
import org.thinkingstudio.obsidianui.widget.SpruceButtonWidget;
import org.thinkingstudio.obsidianui.widget.container.SpruceContainerWidget;

public class VirtualKeyboardScreen extends SpruceScreen {
    private SpruceContainerWidget container;
    private TextFieldWidget bufferDisplay;
    private final StringBuilder buffer;
    private final CloseCallback closeCallback;

    @FunctionalInterface
    public interface CloseCallback {
        void onClose(String text);
    }

    public VirtualKeyboardScreen(String initialText, CloseCallback closeCallback) {
        super(Text.literal("Virtual Keyboard"));

        this.buffer = new StringBuilder(initialText);
        this.closeCallback = closeCallback;
    }

    @Override
    protected void init() {
        super.init();

        this.bufferDisplay = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 4 - 40, 200, 20, Text.literal(""));
        this.bufferDisplay.setEditable(false);
        this.bufferDisplay.setMaxLength(1024);
        this.bufferDisplay.setText(buffer.toString());
        this.addDrawableChild(this.bufferDisplay);

        rebuildKeyboard();

        this.addDrawableChild(container);
        this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 50, this.height - 30), 100, 20, SpruceTexts.GUI_DONE, btn -> this.close()));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
    }


    private void rebuildKeyboard() {
        this.container = new SpruceContainerWidget(Position.of(0, this.height / 4 - 10), this.width, this.height / 2);


        var row1 = new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
        var row2 = new String[]{"a", "s", "d", "f", "g", "h", "j", "k", "l"};
        var row3 = new String[]{"z", "x", "c", "v", "b", "n", "m"};

        addKeyRow(0, row1);
        addKeyRow(1, row2);
        addKeyRow(2, row3);
    }

    private void addKeyRow(int rowOffset, String... keys) {
        int keyWidth = 20;
        int spacing = 2;
        int totalWidth = (keyWidth + spacing) * keys.length - spacing;
        int startX = (this.width - totalWidth) / 2;
        int y = this.height / 4 + rowOffset * 24;

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            this.container.addChild(new SpruceButtonWidget(Position.of(startX + i * (keyWidth + spacing), y), keyWidth, 20, Text.literal(key), btn -> handleKeyPress(key)));
        }
    }

    private void handleKeyPress(String key) {
        if (this.client == null) return;

        // TODO
        if (key.equals("\b")) {
            if (!buffer.isEmpty()) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
        } else {
            buffer.append(key);
        }

        if (this.bufferDisplay != null) {
            this.bufferDisplay.setText(buffer.toString());
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
        if (closeCallback != null) {
            closeCallback.onClose(buffer.toString());
        }
    }
}