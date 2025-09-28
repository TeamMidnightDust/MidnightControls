package eu.midnightdust.midnightcontrols.client.gui.cursor;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Atlases;
import org.jetbrains.annotations.NotNull;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;

public class VirtualCursorRenderer extends CursorRenderer {
    private static final VirtualCursorRenderer INSTANCE = new VirtualCursorRenderer();

    public static VirtualCursorRenderer getInstance() {
        return INSTANCE;
    }

    public void renderCursor(@NotNull DrawContext context, @NotNull MinecraftClient client) {
        if (!MidnightControlsConfig.virtualMouse || (client.currentScreen == null
                || MidnightInput.isScreenInteractive(client.currentScreen)))
            return;

        float mouseX = (float) client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
        float mouseY = (float) client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();

        boolean hoverSlot = false;

        if (client.currentScreen instanceof HandledScreenAccessor inventoryScreen) {
            int guiLeft = inventoryScreen.getX();
            int guiTop = inventoryScreen.getY();

            Slot slot = inventoryScreen.midnightcontrols$getSlotAt(mouseX, mouseY);

            if (slot != null) {
                mouseX = guiLeft + slot.x;
                mouseY = guiTop + slot.y;
                hoverSlot = true;
            }
        }

        if (!hoverSlot && client.currentScreen != null) {
            var slot = MidnightControlsCompat.getSlotAt(client.currentScreen, (int) mouseX, (int) mouseY);

            if (slot != null) {
                mouseX = slot.x();
                mouseY = slot.y();
                hoverSlot = true;
            }
        }

        if (!hoverSlot) {
            mouseX -= 8;
            mouseY -= 8;
        }

        try {
            Sprite sprite = client.getAtlasManager().getAtlasTexture(Atlases.GUI).getSprite(id(MidnightControlsConfig.virtualMouseSkin.getSpritePath() + (hoverSlot ? "_slot" : "")));
            drawUnalignedTexturedQuad(RenderPipelines.GUI_TEXTURED, sprite.getAtlasId(), context, mouseX, mouseX + 16, mouseY, mouseY + 16, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
        } catch (IllegalStateException ignored) {}
    }
}
