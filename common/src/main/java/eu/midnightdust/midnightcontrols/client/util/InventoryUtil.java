package eu.midnightdust.midnightcontrols.client.util;

import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.input;

public class InventoryUtil {
    // Finds the closest slot in the GUI within 14 pixels.
    public static Optional<Slot> findClosestSlot(AbstractContainerScreen<?> inventory, int direction) {
        var accessor = (HandledScreenAccessor) inventory;
        int guiLeft = accessor.getX();
        int guiTop = accessor.getY();
        double mouseX = client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / (double) client.getWindow().getScreenWidth();
        double mouseY = client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / (double) client.getWindow().getScreenHeight();
        // Finds the hovered slot.
        var mouseSlot = accessor.midnightcontrols$getSlotAt(mouseX, mouseY);
        return inventory.getMenu().slots.parallelStream()
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

    private static int targetMouseX = 0;
    private static int targetMouseY = 0;

    // Inspired from https://github.com/MrCrayfish/Controllable/blob/1.14.X/src/main/java/com/mrcrayfish/controllable/client/ControllerInput.java#L686.
    public static void moveMouseToClosestSlot(@Nullable Screen screen) {
        // Makes the mouse attracted to slots. This helps with selecting items when using a controller.
        if (screen instanceof AbstractContainerScreen<?> inventoryScreen) {
            var accessor = (HandledScreenAccessor) inventoryScreen;
            int guiLeft = accessor.getX();
            int guiTop = accessor.getY();
            int mouseX = (int) (targetMouseX * (double) client.getWindow().getGuiScaledWidth() / (double) client.getWindow().getScreenWidth());
            int mouseY = (int) (targetMouseY * (double) client.getWindow().getGuiScaledHeight() / (double) client.getWindow().getScreenHeight());

            // Finds the closest slot in the GUI within 14 pixels.
            Optional<net.minecraft.util.Tuple<Slot, Double>> closestSlot = inventoryScreen.getMenu().slots.parallelStream()
                    .map(slot -> {
                        int x = guiLeft + slot.x + 8;
                        int y = guiTop + slot.y + 8;

                        // Distance between the slot and the cursor.
                        double distance = Math.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2));
                        return new net.minecraft.util.Tuple<>(slot, distance);
                    }).filter(entry -> entry.getB() <= 14.0)
                    .min(Comparator.comparingDouble(net.minecraft.util.Tuple::getB));

            if (closestSlot.isPresent() && client.player != null) {
                var slot = closestSlot.get().getA();
                if (slot.hasItem() || !client.player.getInventory().getSelectedItem().isEmpty()) {
                    int slotCenterXScaled = guiLeft + slot.x + 8;
                    int slotCenterYScaled = guiTop + slot.y + 8;
                    int slotCenterX = (int) (slotCenterXScaled / ((double) client.getWindow().getGuiScaledWidth() / (double) client.getWindow().getScreenWidth()));
                    int slotCenterY = (int) (slotCenterYScaled / ((double) client.getWindow().getGuiScaledHeight() / (double) client.getWindow().getScreenHeight()));
                    double deltaX = slotCenterX - targetMouseX;
                    double deltaY = slotCenterY - targetMouseY;

                    if (mouseX != slotCenterXScaled || mouseY != slotCenterYScaled) {
                        targetMouseX += (int) (deltaX * 0.75);
                        targetMouseY += (int) (deltaY * 0.75);
                    } else {
                        input.mouseSpeedX *= 0.3F;
                        input.mouseSpeedY *= 0.3F;
                    }
                    input.mouseSpeedX *= .75F;
                    input.mouseSpeedY *= .75F;
                } else {
                    input.mouseSpeedX *= .1F;
                    input.mouseSpeedY *= .1F;
                }
            } else {
                input.mouseSpeedX *= .3F;
                input.mouseSpeedY *= .3F;
            }
        } else {
            input.mouseSpeedX = 0.F;
            input.mouseSpeedY = 0.F;
        }
    }
}
