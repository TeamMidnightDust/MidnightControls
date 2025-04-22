package eu.midnightdust.midnightcontrols.client.gui.virtualkeyboard;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.thinkingstudio.obsidianui.Position;
import org.thinkingstudio.obsidianui.SpruceTexts;
import org.thinkingstudio.obsidianui.screen.SpruceScreen;
import org.thinkingstudio.obsidianui.widget.SpruceButtonWidget;
import org.thinkingstudio.obsidianui.widget.container.SpruceContainerWidget;
import org.thinkingstudio.obsidianui.widget.text.SpruceTextAreaWidget;

import java.util.List;

public class VirtualKeyboardScreen extends SpruceScreen {

    @FunctionalInterface
    public interface CloseCallback {
        void onClose(String text);
    }

    private static final int STANDARD_KEY_WIDTH = 20;
    private static final int SPECIAL_KEY_WIDTH = (int) (STANDARD_KEY_WIDTH * 1.5);
    private static final int KEY_HEIGHT = 20;
    private static final int HORIZONTAL_SPACING = 2;
    private static final int VERTICAL_SPACING = 4;
    private static final int CONTAINER_PADDING = 10;

    // Key symbols
    private static final String BACKSPACE_SYMBOL = "\b";
    private static final String NEWLINE_SYMBOL = "\n";
    private static final String SPACE_SYMBOL = " ";

    private final StringBuilder buffer;
    private final CloseCallback closeCallback;
    private final KeyboardLayout layout;
    private final boolean newLineSupport;

    private boolean capsMode;
    private boolean symbolMode;
    private SpruceTextAreaWidget bufferDisplayArea;
    private SpruceContainerWidget keyboardContainer;

    public VirtualKeyboardScreen(String initialText, CloseCallback closeCallback, boolean newLineSupport) {
        super(Text.literal("Virtual Keyboard"));
        this.buffer = new StringBuilder(initialText);
        this.closeCallback = closeCallback;
        this.layout = KeyboardLayout.QWERTY;
        this.capsMode = false;
        this.symbolMode = false;
        this.newLineSupport = newLineSupport;
    }

    @Override
    protected void init() {
        super.init();

        this.bufferDisplayArea = createBufferDisplayArea();
        this.addDrawableChild(this.bufferDisplayArea);

        rebuildKeyboard();

        int doneButtonY = this.keyboardContainer.getY() + this.keyboardContainer.getHeight() + VERTICAL_SPACING * 2;
        this.addDrawableChild(
                new SpruceButtonWidget(
                        Position.of(this, this.width / 2 - 50, doneButtonY),
                        100,
                        20,
                        SpruceTexts.GUI_DONE,
                        btn -> this.close()
                )
        );
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
        if (this.closeCallback != null) {
            this.closeCallback.onClose(this.buffer.toString());
        }
    }

    private void rebuildKeyboard() {
        if (this.keyboardContainer != null) {
            this.remove(this.keyboardContainer);
        }

        var keys = getActiveKeyLayout();
        var keyboardContainer = createKeyboardContainer(keys);

        addLetterRows(keyboardContainer, keys);
        addFunctionKeys(keyboardContainer);
        addBottomRow(keyboardContainer);

        this.keyboardContainer = keyboardContainer;
        this.addDrawableChild(this.keyboardContainer);
    }

    private SpruceContainerWidget createKeyboardContainer(List<List<KeyInfo>> layoutKeys) {
        int containerWidth = this.width;
        int totalKeyboardHeight = calculateKeyboardHeight(layoutKeys);
        int keyboardY = this.bufferDisplayArea.getY() + this.bufferDisplayArea.getHeight() + VERTICAL_SPACING * 2;

        return new SpruceContainerWidget(
                Position.of(0, keyboardY),
                containerWidth,
                totalKeyboardHeight
        );
    }

    private SpruceTextAreaWidget createBufferDisplayArea() {
        int lineCount = this.newLineSupport ? 3 : 1;
        int bufferX = this.width / 2 - 100;
        int bufferY = this.height / 4 - VERTICAL_SPACING * 5 - 5;
        int bufferWidth = 200;
        int desiredHeight = (this.textRenderer.fontHeight + 2) * lineCount + 6;

        var bufferDisplay = new SpruceTextAreaWidget(
                Position.of(bufferX, bufferY),
                bufferWidth,
                desiredHeight,
                Text.literal("Buffer Display")
        );
        bufferDisplay.setText(this.buffer.toString());
        bufferDisplay.setEditable(false);
        bufferDisplay.setUneditableColor(0xFFFFFFFF);
        bufferDisplay.setDisplayedLines(lineCount);
        bufferDisplay.setCursorToEnd();

        return bufferDisplay;
    }

    private int calculateKeyboardHeight(List<List<KeyInfo>> keyRows) {
        return keyRows.size() * (KEY_HEIGHT + VERTICAL_SPACING) +
                (KEY_HEIGHT + VERTICAL_SPACING) + // space for bottom row
                CONTAINER_PADDING * 2; // top and bottom padding
    }

    private void addLetterRows(SpruceContainerWidget container, List<List<KeyInfo>> keyRows) {
        int currentY = CONTAINER_PADDING;

        for (List<KeyInfo> row : keyRows) {
            int rowWidth = calculateRowWidth(row);
            // center row
            int currentX = (container.getWidth() - rowWidth) / 2;

            for (KeyInfo keyInfo : row) {
                int keyWidth = (int) (STANDARD_KEY_WIDTH * keyInfo.widthFactor());
                String displayText = getKeyDisplayText(keyInfo);
                container.addChild(
                        new SpruceButtonWidget(
                                Position.of(currentX, currentY),
                                keyWidth,
                                KEY_HEIGHT,
                                Text.literal(displayText),
                                btn -> handleKeyPress(displayText)
                        )
                );

                currentX += keyWidth + HORIZONTAL_SPACING;
            }

            currentY += KEY_HEIGHT + VERTICAL_SPACING;
        }
    }

    private int calculateRowWidth(List<KeyInfo> row) {
        int rowWidth = 0;
        for (int i = 0; i < row.size(); i++) {
            rowWidth += (int) (STANDARD_KEY_WIDTH * row.get(i).widthFactor());
            if (i < row.size() - 1) {
                rowWidth += HORIZONTAL_SPACING;
            }
        }
        return rowWidth;
    }

    private void addFunctionKeys(SpruceContainerWidget container) {
        List<KeyInfo> firstRow = getActiveKeyLayout().get(0);
        int firstRowWidth = calculateRowWidth(firstRow);

        // position backspace at the right of the first row
        int backspaceWidth = (int) (STANDARD_KEY_WIDTH * 1.5);
        int backspaceX = (container.getWidth() + firstRowWidth) / 2 + HORIZONTAL_SPACING;

        container.addChild(
                new SpruceButtonWidget(
                        Position.of(backspaceX, CONTAINER_PADDING),
                        backspaceWidth,
                        KEY_HEIGHT,
                        Text.literal("←"),
                        btn -> handleKeyPress(BACKSPACE_SYMBOL)
                )
        );


        if (this.newLineSupport) {
            // position newline at the right of the second row
            List<KeyInfo> secondRow = getActiveKeyLayout().get(1);
            int newlineWidth = (int) (STANDARD_KEY_WIDTH * 1.5);
            int secondRowWidth = calculateRowWidth(secondRow);
            int newlineX = (container.getWidth() + secondRowWidth) / 2 + HORIZONTAL_SPACING;
            int newlineY = CONTAINER_PADDING + (KEY_HEIGHT + VERTICAL_SPACING);

            container.addChild(
                    new SpruceButtonWidget(
                            Position.of(newlineX, newlineY),
                            newlineWidth,
                            KEY_HEIGHT,
                            Text.literal("⏎"),
                            btn -> handleKeyPress(NEWLINE_SYMBOL)
                    )
            );
        }
    }

    private void addBottomRow(SpruceContainerWidget container) {
        // calculate positions for bottom row
        int rowY = CONTAINER_PADDING + getActiveKeyLayout().size() * (KEY_HEIGHT + VERTICAL_SPACING);

        // space bar - wide key in the middle
        double spaceWidthFactor = 5.0;
        int spaceKeyWidth = (int) (STANDARD_KEY_WIDTH * spaceWidthFactor);
        int spaceX = (container.getWidth() - spaceKeyWidth) / 2;

        container.addChild(
                new SpruceButtonWidget(
                        Position.of(spaceX, rowY),
                        spaceKeyWidth,
                        KEY_HEIGHT,
                        Text.literal("Space"),
                        btn -> handleKeyPress(SPACE_SYMBOL)
                )
        );

        // caps key - left of space
        if (!this.symbolMode) {
            int capsX = spaceX - SPECIAL_KEY_WIDTH - HORIZONTAL_SPACING * 2;
            var capsModeButton = new SpruceButtonWidget(
                    Position.of(capsX, rowY),
                    SPECIAL_KEY_WIDTH,
                    KEY_HEIGHT,
                    Text.literal(this.capsMode ? "caps" : "CAPS"),
                    btn -> toggleCapsMode());

            container.addChild(capsModeButton);
        }

        // symbols key - right of space
        int symbolsX = spaceX + spaceKeyWidth + HORIZONTAL_SPACING * 2;
        var symbolModeButton = new SpruceButtonWidget(
                Position.of(symbolsX, rowY),
                SPECIAL_KEY_WIDTH,
                KEY_HEIGHT,
                Text.literal(this.symbolMode ? "ABC" : "123?!"),
                btn -> toggleSymbolMode()
        );
        container.addChild(symbolModeButton);
    }

    private void handleKeyPress(String key) {
        if (key.equals(BACKSPACE_SYMBOL)) {
            if (!this.buffer.isEmpty()) {
                this.buffer.deleteCharAt(buffer.length() - 1);
            }
        } else {
            this.buffer.append(key);
        }

        if (this.bufferDisplayArea != null) {
            this.bufferDisplayArea.setText(this.buffer.toString());
            this.bufferDisplayArea.setCursorToEnd();
        }
    }

    private String getKeyDisplayText(KeyInfo keyInfo) {
        if(this.capsMode && !this.symbolMode) {
            return keyInfo.displayText().toUpperCase();
        }

        return keyInfo.displayText();
    }

    private List<List<KeyInfo>> getActiveKeyLayout() {
        return this.symbolMode ? this.layout.getSymbols() : this.layout.getLetters();
    }

    private void toggleCapsMode() {
        this.capsMode = !this.capsMode;
        rebuildKeyboard();
    }

    private void toggleSymbolMode() {
        this.symbolMode = !this.symbolMode;
        rebuildKeyboard();
    }
}