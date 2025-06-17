package eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.KeyboardLayout;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.KeyboardLayoutManager;
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
        super(Text.translatable("midnightcontrols.virtual_keyboard.screen"));
        this.buffer = new StringBuilder(initialText);
        this.closeCallback = closeCallback;
        this.layout = KeyboardLayoutManager.getById(MidnightControlsConfig.keyboardLayout);
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

        var layoutKeys = getActiveKeyLayout();
        var keyboardContainer = createKeyboardContainer(layoutKeys);

        addLayoutRows(keyboardContainer, layoutKeys);
        addFunctionKeys(keyboardContainer);
        addBottomRow(keyboardContainer);

        this.keyboardContainer = keyboardContainer;
        this.addDrawableChild(this.keyboardContainer);
    }

    private SpruceContainerWidget createKeyboardContainer(List<List<String>> layoutKeys) {
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
        int lineCount = this.newLineSupport ? 4 : 1;
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

    private int calculateKeyboardHeight(List<List<String>> keyRows) {
        return keyRows.size() * (KEY_HEIGHT + VERTICAL_SPACING) +
                (KEY_HEIGHT + VERTICAL_SPACING) + // space for bottom row
                CONTAINER_PADDING * 2; // top and bottom padding
    }

    private void addLayoutRows(SpruceContainerWidget container, List<List<String>> keyLayoutRows) {
        int currentY = CONTAINER_PADDING;

        for (List<String> row : keyLayoutRows) {
            int rowWidth = calculateRowWidth(row);
            // center row
            int currentX = (container.getWidth() - rowWidth) / 2;

            for (String key : row) {
                String displayText = (this.capsMode && !this.symbolMode) ? key.toUpperCase() : key;
                container.addChild(
                        new SpruceButtonWidget(
                                Position.of(currentX, currentY),
                                STANDARD_KEY_WIDTH,
                                KEY_HEIGHT,
                                Text.literal(displayText),
                                btn -> handleKeyPress(displayText)
                        )
                );

                currentX += STANDARD_KEY_WIDTH + HORIZONTAL_SPACING;
            }

            currentY += KEY_HEIGHT + VERTICAL_SPACING;
        }
    }

    private int calculateRowWidth(List<String> row) {
        int rowWidth = 0;
        for (int i = 0; i < row.size(); i++) {
            rowWidth += STANDARD_KEY_WIDTH;
            // padding
            if (i < row.size() - 1) {
                rowWidth += HORIZONTAL_SPACING;
            }
        }
        return rowWidth;
    }

    private void addFunctionKeys(SpruceContainerWidget container) {
        List<String> firstRow = getActiveKeyLayout().getFirst();
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
            List<String> secondRow = getActiveKeyLayout().get(1);
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
                        Text.translatable("midnightcontrols.virtual_keyboard.keyboard.space"),
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

    private List<List<String>> getActiveKeyLayout() {
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