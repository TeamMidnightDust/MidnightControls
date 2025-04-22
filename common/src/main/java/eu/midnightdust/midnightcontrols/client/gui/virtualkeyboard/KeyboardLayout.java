package eu.midnightdust.midnightcontrols.client.gui.virtualkeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardLayout {

    public static KeyboardLayout QWERTY = new KeyboardLayout(createQwertyLetterLayout(), createSymbolLayout());

    private final List<List<KeyInfo>> letters;
    private final List<List<KeyInfo>> symbols;

    private KeyboardLayout(List<List<KeyInfo>> letters, List<List<KeyInfo>> symbols) {
        this.letters = letters;
        this.symbols = symbols;
    }

    public List<List<KeyInfo>> getLetters() {
        return letters;
    }

    public List<List<KeyInfo>> getSymbols() {
        return symbols;
    }

    private static List<List<KeyInfo>> createQwertyLetterLayout() {
        List<List<KeyInfo>> letters = new ArrayList<>();
        letters.add(Arrays.asList(
                new KeyInfo("q"), new KeyInfo("w"), new KeyInfo("e"), new KeyInfo("r"), new KeyInfo("t"),
                new KeyInfo("y"), new KeyInfo("u"), new KeyInfo("i"), new KeyInfo("o"), new KeyInfo("p")
        ));
        letters.add(Arrays.asList(
                new KeyInfo("a"), new KeyInfo("s"), new KeyInfo("d"), new KeyInfo("f"), new KeyInfo("g"),
                new KeyInfo("h"), new KeyInfo("j"), new KeyInfo("k"), new KeyInfo("l")
        ));
        letters.add(Arrays.asList(
                new KeyInfo("z"), new KeyInfo("x"), new KeyInfo("c"), new KeyInfo("v"),
                new KeyInfo("b"), new KeyInfo("n"), new KeyInfo("m")
        ));
        return letters;
    }

    private static List<List<KeyInfo>> createSymbolLayout() {
        List<List<KeyInfo>> symbols = new ArrayList<>();
        symbols.add(Arrays.asList(
                new KeyInfo("1"), new KeyInfo("2"), new KeyInfo("3"), new KeyInfo("4"), new KeyInfo("5"),
                new KeyInfo("6"), new KeyInfo("7"), new KeyInfo("8"), new KeyInfo("9"), new KeyInfo("0")
        ));
        symbols.add(Arrays.asList(
                new KeyInfo("@"), new KeyInfo("#"), new KeyInfo("$"), new KeyInfo("%"), new KeyInfo("&"),
                new KeyInfo("*"), new KeyInfo("-"), new KeyInfo("+"), new KeyInfo("("), new KeyInfo(")")
        ));
        symbols.add(Arrays.asList(
                new KeyInfo("!"), new KeyInfo("\""), new KeyInfo("'"), new KeyInfo(":"), new KeyInfo(";"),
                new KeyInfo(","), new KeyInfo("."), new KeyInfo("?"), new KeyInfo("/")
        ));
        return symbols;
    }
}