package eu.midnightdust.midnightcontrols.client;

import eu.midnightdust.midnightcontrols.client.virtualkeyboard.KeyboardLayoutManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

public class MidnightControlsReloadListener implements SynchronousResourceReloader {
    public static final MidnightControlsReloadListener INSTANCE = new MidnightControlsReloadListener();

    private MidnightControlsReloadListener() {}

    @Override
    public void reload(ResourceManager manager) {
        manager.findResources("keyboard_layouts", path -> path.toString().startsWith("midnightcontrols") && path.toString().endsWith(".json")).forEach(KeyboardLayoutManager::loadLayout);
    }
}