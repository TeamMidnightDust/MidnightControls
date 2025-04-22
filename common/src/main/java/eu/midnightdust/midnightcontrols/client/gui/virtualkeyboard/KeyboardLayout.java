package eu.midnightdust.midnightcontrols.client.gui.virtualkeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardLayout {

    public static KeyboardLayout QWERTY = new KeyboardLayout(createQwertyLetterLayout(), createSymbolLayout());

    private final List<List<String>> letters;
    private final List<List<String>> symbols;

    private KeyboardLayout(List<List<String>> letters, List<List<String>> symbols) {
        this.letters = letters;
        this.symbols = symbols;
    }

    public List<List<String>> getLetters() {
        return letters;
    }

    public List<List<String>> getSymbols() {
        return symbols;
    }

    private static List<List<String>> createQwertyLetterLayout() {
        List<List<String>> letters = new ArrayList<>();
        letters.add(Arrays.asList(
                "q", "w", "e", "r", "t",
                "y", "u", "i", "o", "p"
        ));
        letters.add(Arrays.asList(
                "a", "s", "d", "f", "g",
                "h", "j", "k", "l"
        ));
        letters.add(Arrays.asList(
                "z", "x", "c", "v",
                "b", "n", "m"
        ));
        return letters;
    }

    private static List<List<String>> createSymbolLayout() {
        List<List<String>> symbols = new ArrayList<>();
        symbols.add(Arrays.asList(
                "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "0"
        ));
        symbols.add(Arrays.asList(
                "@", "#", "$", "%", "&",
                "*", "-", "+", "(", ")"
        ));
        symbols.add(Arrays.asList(
                "!", "\"", "'", ":", ";",
                ",", ".", "?", "/"
        ));
        return symbols;
    }
}