package eu.midnightdust.midnightcontrols.client.touch;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsConfig.doMixedInput;

public class TouchInput {
    private static final Minecraft client = Minecraft.getInstance();
    public static long clickStartTime;
    public static HitResult firstHitResult = null;
    public static boolean isDragging = false;

    public static void tick() {
        //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
        if ((client.gui.screen() == null && doMixedInput()) || client.gui.screen() instanceof TouchscreenOverlay) {
            double scaleFactor = client.getWindow().getGuiScale();
            if (clickStartTime > 0 && System.currentTimeMillis() - clickStartTime >= MidnightControlsConfig.touchBreakDelay) {
                mouseHeldDown(client.mouseHandler.xpos() / scaleFactor, client.mouseHandler.ypos() / scaleFactor);
            }
        }
    }
    public static void mouseHeldDown(double mouseX, double mouseY) {
        assert client != null;
        assert client.player != null;
        assert client.gameMode != null;

        if (client.player.getMainHandItem() != null && TouchUtils.hasInWorldUseAction(client.player.getMainHandItem())) {
            client.gameMode.useItem(client.player, client.player.getUsedItemHand());
            return;
        }
        HitResult result = TouchUtils.getTargetedObject(mouseX, mouseY);
        if (result == null || firstHitResult == null) {
            client.gameMode.stopDestroyBlock();
            return;
        }

        if (result instanceof BlockHitResult blockHit && firstHitResult instanceof BlockHitResult firstBlock && blockHit.getBlockPos().equals(firstBlock.getBlockPos())) {
            if (MidnightControlsConfig.debug) System.out.println(blockHit.getBlockPos().toString());
            if (client.gameMode.continueDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection())) {
                //client.particleManager.addBlockBreakingParticles(blockHit.getBlockPos(), blockHit.getSide()); // TODO Re-implement block breaking particles!!!
                client.player.swing(InteractionHand.MAIN_HAND);
            } else client.gameMode.stopDestroyBlock();
            firstHitResult = TouchUtils.getTargetedObject(mouseX, mouseY);
        }
        else if (result instanceof EntityHitResult entityHit && firstHitResult instanceof EntityHitResult firstEntity && entityHit.getEntity().getUUID().compareTo(firstEntity.getEntity().getUUID()) == 0) {
            if (client.gameMode.interact(client.player, entityHit.getEntity(), /*? if >= 26.1 {*/ entityHit,/*?}*/ client.player.getUsedItemHand()) == InteractionResult.SUCCESS) {
                client.player.swing(InteractionHand.MAIN_HAND);
            }
            firstHitResult = TouchUtils.getTargetedObject(mouseX, mouseY);
        }
    }
    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        firstHitResult = null;
        if (client.gameMode != null) client.gameMode.stopDestroyBlock();
        //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
        if ((client.gui.screen() == null || !client.gui.screen().mouseReleased(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)))) && System.currentTimeMillis() - clickStartTime < MidnightControlsConfig.touchBreakDelay) {
            assert client.player != null;
            assert client.level != null;
            assert client.gameMode != null;
            clickStartTime = -1;

            if (client.player.getMainHandItem() != null && TouchUtils.hasInWorldUseAction(client.player.getMainHandItem())) {
                client.gameMode.releaseUsingItem(client.player);
                return true;
            }
            HitResult result = TouchUtils.getTargetedObject(mouseX, mouseY);
            if (result == null) return false;


            if (result instanceof BlockHitResult blockHit) {
                BlockPos blockPos = blockHit.getBlockPos().relative(blockHit.getDirection());
                BlockState state = client.level.getBlockState(blockPos);

                if (client.level.isEmptyBlock(blockPos) || state.canBeReplaced()) {
                    ItemStack stackInHand = client.player.getMainHandItem();
                    int previousStackCount = stackInHand.getCount();
                    var interaction = client.gameMode.useItemOn(client.player, client.player.getUsedItemHand(), blockHit);
                    if (interaction.consumesAction()) {
                        //if (interaction.shouldSwingHand()) {
                            client.player.swing(client.player.swingingArm);
                            if (!stackInHand.isEmpty() && (stackInHand.getCount() != previousStackCount || client.player.hasInfiniteMaterials())) {
                                client.gameRenderer.itemInHandRenderer.itemUsed(client.player.swingingArm);
                            }
                        //}
                        return true;
                    }
                }
            }
            if (result instanceof EntityHitResult entityHit) {
                client.gameMode.attack(client.player, entityHit.getEntity());
                client.player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
        }
        clickStartTime = -1;
        return false;
    }
}
