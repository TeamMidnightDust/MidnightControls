/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.gui.TouchscreenOverlay;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Final
    @Shadow
    private Window window;

    @Shadow
    public boolean skipGameRender;

    @Shadow
    public Screen currentScreen;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Final
    public GameRenderer gameRenderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void lambdacontrols_on_init(CallbackInfo ci)
    {
        LambdaControlsClient.get().on_mc_init((MinecraftClient) (Object) this);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void lambdacontrols_on_render(boolean full_render, CallbackInfo ci)
    {
        LambdaControlsClient.get().on_render((MinecraftClient) (Object) (this));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void lambdacontrols_on_handle_input_events(CallbackInfo ci)
    {
        LambdaControlsClient.get().on_tick((MinecraftClient) (Object) this);
    }

    @Inject(method = "openScreen", at = @At("RETURN"))
    private void lambdacontrols_on_open_screen(@Nullable Screen screen, CallbackInfo ci)
    {
        LambdaControlsClient mod = LambdaControlsClient.get();
        if (screen == null && mod.config.get_controls_mode() == ControlsMode.TOUCHSCREEN) {
            screen = new TouchscreenOverlay(mod);
            screen.init(((MinecraftClient) (Object) this), this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
            this.currentScreen = screen;
        } else if (screen != null) {
            mod.input.on_screen_open(((MinecraftClient) (Object) this), this.window.getWidth(), this.window.getHeight());
        }
    }

    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void lambdacontrols_on_item_use(CallbackInfo ci, Hand[] hands, int hand_count, int hand_index, Hand hand, ItemStack stack_in_hand)
    {
        LambdaControlsClient mod = LambdaControlsClient.get();
        if (!stack_in_hand.isEmpty() && this.player.pitch > 35.0F && mod.config.has_front_block_placing()) {
            if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.MISS && this.player.onGround) {
                if (!stack_in_hand.isEmpty() && stack_in_hand.getItem() instanceof BlockItem) {
                    BlockPos player_pos = this.player.getBlockPos().down();
                    BlockPos target_pos = new BlockPos(this.crosshairTarget.getPos()).subtract(player_pos);
                    BlockPos vector = new BlockPos(MathHelper.clamp(target_pos.getX(), -1, 1), 0, MathHelper.clamp(target_pos.getZ(), -1, 1));
                    BlockPos block_pos = player_pos.add(vector);

                    Direction direction = player.getHorizontalFacing();

                    BlockState adjacent_block_state = this.world.getBlockState(block_pos.offset(direction.getOpposite()));
                    if (adjacent_block_state.isAir() || adjacent_block_state.getBlock() instanceof FluidBlock || (vector.getX() == 0 && vector.getZ() == 0)) {
                        return;
                    }

                    BlockHitResult hit_result = new BlockHitResult(this.crosshairTarget.getPos(), direction.getOpposite(), block_pos, false);

                    int previous_stack_count = stack_in_hand.getCount();
                    ActionResult result = this.interactionManager.interactBlock(this.player, this.world, hand, hit_result);
                    if (result.isAccepted()) {
                        if (result.shouldSwingHand()) {
                            this.player.swingHand(hand);
                            if (!stack_in_hand.isEmpty() && (stack_in_hand.getCount() != previous_stack_count || this.interactionManager.hasCreativeInventory())) {
                                this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                            }
                        }

                        ci.cancel();
                    }

                    if (result == ActionResult.FAIL) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
