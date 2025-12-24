package eu.midnightdust.midnightcontrols.client.gui.cursor;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.enums.VirtualMouseSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

public class WaylandCursorRenderer extends CursorRenderer {
    private static final WaylandCursorRenderer INSTANCE = new WaylandCursorRenderer();

    public static final Identifier WAYLAND_CURSOR_ARROW_LIGHT = id("cursor/light/mouse_arrow");
    public static final Identifier WAYLAND_CURSOR_ARROW_DARK = id("cursor/dark/mouse_arrow");
    public static final Identifier WAYLAND_CURSOR_POINTING_LIGHT = id("cursor/light/mouse_pointing_hand");
    public static final Identifier WAYLAND_CURSOR_POINTING_DARK = id("cursor/dark/mouse_pointing_hand");
    public static final Identifier WAYLAND_CURSOR_IBEAM_LIGHT = id("cursor/light/mouse_ibeam");
    public static final Identifier WAYLAND_CURSOR_IBEAM_DARK = id("cursor/dark/mouse_ibeam");
    public static final Identifier WAYLAND_CURSOR_RESIZE_VERTICAL_LIGHT = id("cursor/light/mouse_resize_vertical");
    public static final Identifier WAYLAND_CURSOR_REZIZE_VERTICAL_DARK = id("cursor/dark/mouse_resize_vertical");
    public static final Identifier WAYLAND_CURSOR_RESIZE_HORIZONTAL_LIGHT = id("cursor/light/mouse_resize_horizontal");
    public static final Identifier WAYLAND_CURSOR_REZIZE_HORIZONTAL_DARK = id("cursor/dark/mouse_resize_horizontal");
    public static final Identifier WAYLAND_CURSOR_NOT_ALLOWED_LIGHT = id("cursor/light/mouse_not_allowed");
    public static final Identifier WAYLAND_CURSOR_NOT_ALLOWED_DARK = id("cursor/dark/mouse_not_allowed");

    public static WaylandCursorRenderer getInstance() {
        return INSTANCE;
    }

    public void renderCursor(@NotNull GuiGraphics context, @NotNull Minecraft client) {
        if (MidnightControlsConfig.virtualMouse || client.screen == null || MidnightControlsConfig.controlsMode != ControlsMode.CONTROLLER) return;

        float mouseX = (float) client.mouseHandler.xpos() * client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth();
        float mouseY = (float) client.mouseHandler.ypos() * client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight();

        try {
            TextureAtlasSprite sprite = getSprite(client);
            drawUnalignedTexturedQuad(RenderPipelines.GUI_TEXTURED, sprite.atlasLocation(), context, mouseX - 2, mouseX + 6, mouseY - 2, mouseY + 6, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
        } catch (IllegalStateException ignored) {}
    }

    private static TextureAtlasSprite getSprite(@NotNull Minecraft client) {
        boolean isDark = MidnightControlsConfig.virtualMouseSkin == VirtualMouseSkin.DEFAULT_DARK || MidnightControlsConfig.virtualMouseSkin == VirtualMouseSkin.SECOND_DARK;
        Identifier spritePath;
        if (CursorRenderer.currentCursorStyle == CursorTypes.POINTING_HAND) spritePath = isDark ? WAYLAND_CURSOR_POINTING_DARK : WAYLAND_CURSOR_POINTING_LIGHT;
        else if (CursorRenderer.currentCursorStyle == CursorTypes.IBEAM) spritePath = isDark ? WAYLAND_CURSOR_IBEAM_DARK : WAYLAND_CURSOR_IBEAM_LIGHT;
        else if (CursorRenderer.currentCursorStyle == CursorTypes.RESIZE_NS) spritePath = isDark ? WAYLAND_CURSOR_REZIZE_VERTICAL_DARK : WAYLAND_CURSOR_RESIZE_VERTICAL_LIGHT;
        else if (CursorRenderer.currentCursorStyle == CursorTypes.RESIZE_EW) spritePath = isDark ? WAYLAND_CURSOR_REZIZE_HORIZONTAL_DARK : WAYLAND_CURSOR_RESIZE_HORIZONTAL_LIGHT;
        else if (CursorRenderer.currentCursorStyle == CursorTypes.NOT_ALLOWED) spritePath = isDark ? WAYLAND_CURSOR_NOT_ALLOWED_DARK : WAYLAND_CURSOR_NOT_ALLOWED_LIGHT;
        else spritePath = isDark ? WAYLAND_CURSOR_ARROW_DARK : WAYLAND_CURSOR_ARROW_LIGHT;

        return client.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spritePath);
    }
}
