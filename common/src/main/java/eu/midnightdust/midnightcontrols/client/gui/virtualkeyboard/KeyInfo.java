package eu.midnightdust.midnightcontrols.client.gui.virtualkeyboard;

public record KeyInfo(String keySymbol, String displayText, double widthFactor) {
    // Convenience constructor for standard width keys
    KeyInfo(String keySymbol, double widthFactor) {
        this(keySymbol, keySymbol, widthFactor);
    }

    KeyInfo(String keySymbol) {
        this(keySymbol, 1.0);
    }

    KeyInfo(String keySymbol, String displayText) {
        this(keySymbol, displayText, 1.0);
    }
}
