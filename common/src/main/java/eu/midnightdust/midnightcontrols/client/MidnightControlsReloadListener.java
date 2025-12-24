package eu.midnightdust.midnightcontrols.client;

import eu.midnightdust.midnightcontrols.client.virtualkeyboard.KeyboardLayoutManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class MidnightControlsReloadListener implements ResourceManagerReloadListener {
    public static final MidnightControlsReloadListener INSTANCE = new MidnightControlsReloadListener();

    private MidnightControlsReloadListener() {}

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        manager.listResources("keyboard_layouts", path -> path.toString().startsWith("midnightcontrols") && path.toString().endsWith(".json")).forEach(KeyboardLayoutManager::loadLayout);
    }
}