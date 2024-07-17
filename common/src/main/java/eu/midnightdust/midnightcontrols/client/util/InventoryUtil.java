package eu.midnightdust.midnightcontrols.client.util;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.aperlambda.lambdacommon.utils.Pair;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class InventoryUtil {
    // Finds the closest slot in the GUI within 14 pixels.
    public static Optional<Slot> findClosestSlot(HandledScreen<?> inventory, int direction) {
        var accessor = (HandledScreenAccessor) inventory;
        int guiLeft = accessor.getX();
        int guiTop = accessor.getY();
        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        // Finds the hovered slot.
        var mouseSlot = accessor.midnightcontrols$getSlotAt(mouseX, mouseY);
        return inventory.getScreenHandler().slots.parallelStream()
                .filter(Predicate.isEqual(mouseSlot).negate())
                .map(slot -> {
                    int posX = guiLeft + slot.x + 8;
                    int posY = guiTop + slot.y + 8;

                    int otherPosX = (int) mouseX;
                    int otherPosY = (int) mouseY;
                    if (mouseSlot != null) {
                        otherPosX = guiLeft + mouseSlot.x + 8;
                        otherPosY = guiTop + mouseSlot.y + 8;
                    }

                    // Distance between the slot and the cursor.
                    double distance = Math.sqrt(Math.pow(posX - otherPosX, 2) + Math.pow(posY - otherPosY, 2));
                    return Pair.of(slot, distance);
                }).filter(entry -> {
                    var slot = entry.key;
                    int posX = guiLeft + slot.x + 8;
                    int posY = guiTop + slot.y + 8;
                    int otherPosX = (int) mouseX;
                    int otherPosY = (int) mouseY;
                    if (mouseSlot != null) {
                        otherPosX = guiLeft + mouseSlot.x + 8;
                        otherPosY = guiTop + mouseSlot.y + 8;
                    }
                    if (direction == 0)
                        return posY < otherPosY;
                    else if (direction == 1)
                        return posY > otherPosY;
                    else if (direction == 2)
                        return posX > otherPosX;
                    else if (direction == 3)
                        return posX < otherPosX;
                    else
                        return false;
                })
                .min(Comparator.comparingDouble(p -> p.value))
                .map(p -> p.key);
    }
}
