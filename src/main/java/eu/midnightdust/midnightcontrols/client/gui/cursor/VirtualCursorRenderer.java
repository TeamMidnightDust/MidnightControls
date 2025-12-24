package eu.midnightdust.midnightcontrols.client.gui.cursor;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;

public class VirtualCursorRenderer extends CursorRenderer {
    private static final VirtualCursorRenderer INSTANCE = new VirtualCursorRenderer();

    public static VirtualCursorRenderer getInstance() {
        return INSTANCE;
    }

    public void renderCursor(@NotNull GuiGraphics context, @NotNull Minecraft client) {
        if (!MidnightControlsConfig.virtualMouse || (client.screen == null
                || MidnightInput.isScreenInteractive(client.screen)))
            return;

        float mouseX = (float) client.mouseHandler.xpos() * client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth();
        float mouseY = (float) client.mouseHandler.ypos() * client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight();

        boolean hoverSlot = false;

        if (client.screen instanceof HandledScreenAccessor inventoryScreen) {
            int guiLeft = inventoryScreen.getX();
            int guiTop = inventoryScreen.getY();

            Slot slot = inventoryScreen.midnightcontrols$getSlotAt(mouseX, mouseY);

            if (slot != null) {
                mouseX = guiLeft + slot.x;
                mouseY = guiTop + slot.y;
                hoverSlot = true;
            }
        }

        if (!hoverSlot && client.screen != null) {
            var slot = MidnightControlsCompat.getSlotAt(client.screen, (int) mouseX, (int) mouseY);

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
            TextureAtlasSprite sprite = client.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(id(MidnightControlsConfig.virtualMouseSkin.getSpritePath() + (hoverSlot ? "_slot" : "")));
            drawUnalignedTexturedQuad(RenderPipelines.GUI_TEXTURED, sprite.atlasLocation(), context, mouseX, mouseX + 16, mouseY, mouseY + 16, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
        } catch (IllegalStateException ignored) {}
    }
}
