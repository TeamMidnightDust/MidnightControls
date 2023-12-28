package eu.midnightdust.midnightcontrols.client.touch;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.gui.TouchscreenOverlay;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class TouchInput {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static long clickStartTime;
    public static HitResult firstHitResult = null;
    public static void tick() {
        if (client.currentScreen != null && !(client.currentScreen instanceof TouchscreenOverlay)) return;
        double scaleFactor = client.getWindow().getScaleFactor();
        if (clickStartTime > 0 && System.currentTimeMillis() - clickStartTime >= MidnightControlsConfig.touchBreakDelay) {
            mouseHeldDown(client.mouse.getX() / scaleFactor, client.mouse.getY() / scaleFactor);
        }
        else {
            if (client.interactionManager != null) client.interactionManager.cancelBlockBreaking();
        }
    }
    public static void mouseHeldDown(double mouseX, double mouseY) {
        assert client != null;
        assert client.player != null;
        assert client.interactionManager != null;

        if (client.player.getMainHandStack() != null && TouchUtils.hasInWorldUseAction(client.player.getMainHandStack())) {
            client.interactionManager.interactItem(client.player, client.player.getActiveHand());
            return;
        }
        HitResult result = TouchUtils.getTargettedObject(mouseX, mouseY);
        if (result == null || firstHitResult == null) {
            client.interactionManager.cancelBlockBreaking();
            return;
        }

        if (result instanceof BlockHitResult blockHit && firstHitResult instanceof BlockHitResult firstBlock && blockHit.getBlockPos().equals(firstBlock.getBlockPos())) {
            if (MidnightControlsConfig.debug) System.out.println(blockHit.getBlockPos().toString());
            if (client.interactionManager.updateBlockBreakingProgress(blockHit.getBlockPos(), blockHit.getSide())) {
                client.particleManager.addBlockBreakingParticles(blockHit.getBlockPos(), blockHit.getSide());
                client.player.swingHand(Hand.MAIN_HAND);
            } else client.interactionManager.cancelBlockBreaking();
            firstHitResult = TouchUtils.getTargettedObject(mouseX, mouseY);
        }
        else if (result instanceof EntityHitResult entityHit && firstHitResult instanceof EntityHitResult firstEntity && entityHit.getEntity().getUuid().compareTo(firstEntity.getEntity().getUuid()) == 0) {
            if (client.interactionManager.interactEntity(client.player, entityHit.getEntity(), client.player.getActiveHand()) == ActionResult.SUCCESS) {
                client.player.swingHand(Hand.MAIN_HAND);
            }
            firstHitResult = TouchUtils.getTargettedObject(mouseX, mouseY);
        }
    }
    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        firstHitResult = null;
        if (client.interactionManager != null) client.interactionManager.cancelBlockBreaking();
        if ((client.currentScreen == null || !client.currentScreen.mouseReleased(mouseX, mouseY, button)) && System.currentTimeMillis() - clickStartTime < MidnightControlsConfig.touchBreakDelay) {
            assert client.player != null;
            assert client.world != null;
            assert client.interactionManager != null;
            clickStartTime = -1;

            if (client.player.getMainHandStack() != null && TouchUtils.hasInWorldUseAction(client.player.getMainHandStack())) {
                client.interactionManager.stopUsingItem(client.player);
                return true;
            }
            HitResult result = TouchUtils.getTargettedObject(mouseX, mouseY);
            if (result == null) return false;


            if (result instanceof BlockHitResult blockHit) {
                BlockPos blockPos = blockHit.getBlockPos().offset(blockHit.getSide());
                BlockState state = client.world.getBlockState(blockPos);

                if (client.world.isAir(blockPos) || state.isReplaceable()) {
                    ItemStack stackInHand = client.player.getMainHandStack();
                    int previousStackCount = stackInHand.getCount();
                    var interaction = client.interactionManager.interactBlock(client.player, client.player.getActiveHand(), blockHit);
                    if (interaction.isAccepted()) {
                        if (interaction.shouldSwingHand()) {
                            client.player.swingHand(client.player.preferredHand);
                            if (!stackInHand.isEmpty() && (stackInHand.getCount() != previousStackCount || client.interactionManager.hasCreativeInventory())) {
                                client.gameRenderer.firstPersonRenderer.resetEquipProgress(client.player.preferredHand);
                            }
                        }
                        return true;
                    }
                }
            }
            if (result instanceof EntityHitResult entityHit) {
                client.interactionManager.attackEntity(client.player, entityHit.getEntity());
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
        clickStartTime = -1;
        return false;
    }
}
