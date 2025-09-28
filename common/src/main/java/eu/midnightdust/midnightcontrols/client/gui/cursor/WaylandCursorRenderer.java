package eu.midnightdust.midnightcontrols.client.gui.cursor;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.enums.VirtualMouseSkin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;

public class WaylandCursorRenderer extends CursorRenderer {
    private static final WaylandCursorRenderer INSTANCE = new WaylandCursorRenderer();

    public static final Identifier WAYLAND_CURSOR_ARROW_LIGHT = id("cursor/light/mouse_arrow");
    public static final Identifier WAYLAND_CURSOR_ARROW_DARK = id("cursor/dark/mouse_arrow");
    public static final Identifier WAYLAND_CURSOR_POINTING_LIGHT = id("cursor/light/mouse_pointing_hand");
    public static final Identifier WAYLAND_CURSOR_POINTING_DARK = id("cursor/dark/mouse_pointing_hand");
    public static final Identifier WAYLAND_CURSOR_IBEAM_LIGHT = id("cursor/light/mouse_ibeam");
    public static final Identifier WAYLAND_CURSOR_IBEAM_DARK = id("cursor/dark/mouse_ibeam");

    public static WaylandCursorRenderer getInstance() {
        return INSTANCE;
    }

    public void renderCursor(@NotNull DrawContext context, @NotNull MinecraftClient client) {
        if (MidnightControlsConfig.virtualMouse || client.currentScreen == null || MidnightControlsConfig.controlsMode != ControlsMode.CONTROLLER) return;

        float mouseX = (float) client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
        float mouseY = (float) client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();

        try {
            Sprite sprite = getSprite(client);
            drawUnalignedTexturedQuad(RenderPipelines.GUI_TEXTURED, sprite.getAtlasId(), context, mouseX - 2, mouseX + 6, mouseY - 2, mouseY + 6, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
        } catch (IllegalStateException ignored) {}
    }

    private static Sprite getSprite(@NotNull MinecraftClient client) {
        boolean isDark = MidnightControlsConfig.virtualMouseSkin == VirtualMouseSkin.DEFAULT_DARK || MidnightControlsConfig.virtualMouseSkin == VirtualMouseSkin.SECOND_DARK;
        Identifier spritePath;
        if (CursorRenderer.currentCursorStyle == StandardCursors.POINTING_HAND) spritePath = isDark ? WAYLAND_CURSOR_POINTING_DARK : WAYLAND_CURSOR_POINTING_LIGHT;
        else if (CursorRenderer.currentCursorStyle == StandardCursors.IBEAM) spritePath = isDark ? WAYLAND_CURSOR_IBEAM_DARK : WAYLAND_CURSOR_IBEAM_LIGHT;
        else spritePath = isDark ? WAYLAND_CURSOR_ARROW_DARK : WAYLAND_CURSOR_ARROW_LIGHT;

        return client.getAtlasManager().getAtlasTexture(Atlases.GUI).getSprite(spritePath);
    }
}
