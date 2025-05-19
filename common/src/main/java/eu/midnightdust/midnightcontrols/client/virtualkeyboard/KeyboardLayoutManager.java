package eu.midnightdust.midnightcontrols.client.virtualkeyboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KeyboardLayoutManager {
    private static final Map<String, KeyboardLayout> KEYBOARD_LAYOUTS = new HashMap<>();

    public static void loadLayout(Identifier id, Resource resource) {
        try {
            JsonObject json = JsonParser.parseReader(resource.getReader()).getAsJsonObject();
            KeyboardLayout layout = KeyboardLayout.fromJson(json);
            KEYBOARD_LAYOUTS.put(layout.getId(), layout);
            if (MidnightControlsConfig.debug) System.out.printf("Loaded keyboard layout: %s\n", layout.getId());
        } catch (IOException e) { throw new RuntimeException(e); }
    }
    public static KeyboardLayout getById(String id) {
        return KEYBOARD_LAYOUTS.get(id) == null ? KeyboardLayout.QWERTY : KEYBOARD_LAYOUTS.get(id);
    }
}
