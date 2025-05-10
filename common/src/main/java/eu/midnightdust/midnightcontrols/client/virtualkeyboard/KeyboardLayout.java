package eu.midnightdust.midnightcontrols.client.virtualkeyboard;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardLayout {

    public static KeyboardLayout QWERTY = new KeyboardLayout("US (Qwerty)", "en-US", createQwertyLetterLayout(), createSymbolLayout());
    public static final List<KeyboardLayout> KEYBOARD_LAYOUTS = new ArrayList<>();

    private final String name;
    private final String locale;
    private final List<List<String>> letters;
    private final List<List<String>> symbols;

    private KeyboardLayout(String name, String locale, List<List<String>> letters, List<List<String>> symbols) {
        this.name = name;
        this.locale = locale;
        this.letters = letters;
        this.symbols = symbols;
    }

    public KeyboardLayout fromJson(JsonObject json) {
        try {
            return new KeyboardLayout(json.get("metadata").getAsJsonObject().get("name").getAsString(), json.get("metadata").getAsJsonObject().get("locale").getAsString(), getFromJson(json, true), getFromJson(json, false));
        } catch (Exception e) {
            throw new RuntimeException("Error loading keyboard definition: %s".formatted(e));
        }
    }
    public List<List<String>> getFromJson(JsonObject json, boolean letters) {
        String type = letters ? "letters" : "symbols";
        List<List<String>> arr = new ArrayList<>();
        if (json.has(type)) {
            JsonObject lettersJson = json.get(type).getAsJsonObject();
            for (int i = 0; ; i++) {
                if (!lettersJson.has("row%s".formatted(i))) break;
                var rowJson = lettersJson.get("row%s".formatted(i)).getAsJsonArray();
                List<String> row = new ArrayList<>();
                for (int j = 0; j < rowJson.size(); j++) {
                    row.add(rowJson.get(j).getAsString());
                }
                arr.add(row);
            }
            return arr;
        }
        else {
            return letters ? createQwertyLetterLayout() : createSymbolLayout();
        }
    }

    public String getName() {
        return name;
    }

    public String getLocale() {
        return locale;
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