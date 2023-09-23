/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import eu.midnightdust.midnightcontrols.client.HudSide;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.gui.widget.SilentTexturedButtonWidget;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;

/**
 * Represents the touchscreen overlay
 */
public class TouchscreenOverlay extends Screen {
    public static final Identifier WIDGETS_LOCATION = new Identifier("midnightcontrols", "textures/gui/widgets.png");
    private MidnightControlsClient mod;
    private SilentTexturedButtonWidget jumpButton;
    private SilentTexturedButtonWidget flyButton;
    private SilentTexturedButtonWidget flyUpButton;
    private SilentTexturedButtonWidget flyDownButton;
    private int flyButtonEnableTicks = 0;
    private int forwardButtonTick = 0;
    private SilentTexturedButtonWidget forwardLeftButton;
    private SilentTexturedButtonWidget forwardRightButton;
    private SilentTexturedButtonWidget startSneakButton;
    private SilentTexturedButtonWidget endSneakButton;

    public TouchscreenOverlay(@NotNull MidnightControlsClient mod) {
        super(Text.literal("Touchscreen overlay"));
        this.mod = mod;
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    public void renderInGameBackground(DrawContext context) {}

    private void pauseGame(boolean bl) {
        if (this.client == null)
            return;
        boolean bl2 = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
        if (bl2) {
            this.client.setScreen(new GameMenuScreen(!bl));
            this.client.getSoundManager().pauseAll();
        } else {
            this.client.setScreen(new GameMenuScreen(true));
        }
    }

    /**
     * Updates the forward button ticks cooldown.
     *
     * @param state The button state.
     *
     */
    private void updateForwardButtonsState(boolean state) {
        if (state)
            this.forwardButtonTick = -1;
        else
            this.forwardButtonTick = 20;
    }

    /**
     * Updates the jump buttons.
     */
    private void updateJumpButtons() {
        if (this.client == null)
            return;
        if (!this.client.interactionManager.isFlyingLocked()) {
            boolean oldStateFly = this.flyButton.isVisible();
            this.jumpButton.setVisible(false);
            this.flyButton.setVisible(true);
            this.flyUpButton.setVisible(true);
            this.flyDownButton.setVisible(true);
            if (oldStateFly != this.flyButton.isVisible()) {
                this.flyButtonEnableTicks = 5;
                this.setJump(false);
            } else if (this.flyButtonEnableTicks > 0)
                this.flyButtonEnableTicks--;
        } else {
            this.jumpButton.setVisible(true);
            this.flyButton.setVisible(false);
            this.flyUpButton.setVisible(false);
            this.flyDownButton.setVisible(false);
        }
    }

    /**
     * Handles the jump button.
     *
     * @param btn   The pressed button.
     */
    private void handleJump(SpruceButtonWidget btn) {
        ((KeyBindingAccessor) this.client.options.jumpKey).midnightcontrols$handlePressState(btn.isActive());
    }
    /**
     * Handles the jump button.
     *
     * @param state   The state.
     */
    private void setJump(boolean state) {
        ((KeyBindingAccessor) this.client.options.jumpKey).midnightcontrols$handlePressState(state);
    }

    @Override
    protected void init() {
        super.init();
        int scaledWidth = this.client.getWindow().getScaledWidth();
        int scaledHeight = this.client.getWindow().getScaledHeight();
        TextIconButtonWidget chatButton = TextIconButtonWidget.builder(Text.empty(), btn -> this.client.setScreen(new ChatScreen("")), true).width(20).texture(new Identifier("icon/language"), 15, 15).build();
        chatButton.setPosition(scaledWidth / 2 - 20, 0);
        this.addDrawableChild(chatButton);
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(scaledWidth / 2, 0), 20, 20, Text.empty(), btn -> this.pauseGame(false), 0, 0, 20, WIDGETS_LOCATION, 256, 256));
        // Inventory buttons.
        int inventoryButtonX = scaledWidth / 2;
        int inventoryButtonY = scaledHeight - 16 - 5;
        if (this.client.options.getMainArm().getValue() == Arm.LEFT) {
            inventoryButtonX = inventoryButtonX - 91 - 24;
        } else {
            inventoryButtonX = inventoryButtonX + 91 + 4;
        }
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(inventoryButtonX, inventoryButtonY), 20, 20, Text.empty(), btn -> {
            if (this.client.interactionManager.hasRidingInventory()) {
                this.client.player.openRidingInventory();
            } else {
                this.client.getTutorialManager().onInventoryOpened();
                this.client.setScreen(new InventoryScreen(this.client.player));
            }
        }, 20, 0, 20, WIDGETS_LOCATION, 256, 256));
                ;
        int jumpButtonX, swapHandsX, sneakButtonX;
        int sneakButtonY = scaledHeight - 10 - 40 - 5;
        if (MidnightControlsConfig.hudSide == HudSide.LEFT) {
            jumpButtonX = scaledWidth - 20 - 20;
            swapHandsX = jumpButtonX - 5 - 40;
            sneakButtonX = 10 + 20 + 5;
        } else {
            jumpButtonX = 20;
            swapHandsX = jumpButtonX + 5 + 40;
            sneakButtonX = scaledWidth - 10 - 40 - 5;
        }
        // Swap items hand.
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY), 20, 20, Text.empty(),
                button -> {
                    if (button.isActive()) {
                        if (!this.client.player.isSpectator()) {
                            this.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                        }
                    }
                },0, 160, 20, WIDGETS_LOCATION));
        // Drop
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY + 5 + 20), 20, 20, Text.empty(), btn ->
                client.player.getInventory().dropSelectedItem(false), 20, 160, 20, WIDGETS_LOCATION));
        // Jump keys
        this.addDrawableChild(this.jumpButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY), 20, 20, Text.empty(), this::handleJump, 0, 40, 20, WIDGETS_LOCATION));
        this.addDrawableChild(this.flyButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY), 20, 20, Text.empty(),btn -> {
                    if (this.flyButtonEnableTicks == 0) this.client.player.getAbilities().flying = false;
                }, 20, 40, 20, WIDGETS_LOCATION)
        );
        this.addDrawableChild(this.flyUpButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY - 5 - 20), 20, 20,Text.empty(),
                this::handleJump, 40, 40, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.flyDownButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY + 20 + 5), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.sneakKey).midnightcontrols$handlePressState(btn.isActive()), 60, 40, 20, WIDGETS_LOCATION
        ));
        this.updateJumpButtons();
        // Movements keys
        this.addDrawableChild((this.startSneakButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY), 20, 20, Text.empty(), btn -> {
                    if (btn.isActive()) {
                        ((KeyBindingAccessor) this.client.options.sneakKey).midnightcontrols$handlePressState(true);
                        this.startSneakButton.setVisible(false);
                        this.endSneakButton.setVisible(true);
                    }
                }, 0, 120, 20, WIDGETS_LOCATION))
        );
        this.addDrawableChild((this.endSneakButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY), 20, 20, Text.empty(), btn -> {
            if (btn.isActive()) {
                ((KeyBindingAccessor) this.client.options.sneakKey).midnightcontrols$handlePressState(false);
                this.endSneakButton.setVisible(false);
                this.startSneakButton.setVisible(true);
            }
        }, 20, 120, 20, WIDGETS_LOCATION)));
        this.endSneakButton.setVisible(false);
        this.addDrawableChild(this.forwardLeftButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY - 5 - 20), 20, 20, Text.empty(), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.client.options.leftKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 80, 80, 20, WIDGETS_LOCATION
        ));
        this.forwardLeftButton.setVisible(false);
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY - 5 - 20), 20, 20, Text.empty(), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
            this.forwardLeftButton.setVisible(true);
            this.forwardRightButton.setVisible(true);
        }, 0, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.forwardRightButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY - 5 - 20), 20, 20, Text.empty(), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.client.options.rightKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 100, 80, 20, WIDGETS_LOCATION
        ));
        this.forwardRightButton.setVisible(false);
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.rightKey).midnightcontrols$handlePressState(btn.isActive()), 20, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY + 20 + 5), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.backKey).midnightcontrols$handlePressState(btn.isActive()), 40, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.leftKey).midnightcontrols$handlePressState(btn.isActive()), 60, 80, 20, WIDGETS_LOCATION
        ));
    }

    @Override
    public void tick() {
        if (this.forwardButtonTick > 0) {
            this.forwardButtonTick--;
        } else if (this.forwardButtonTick == 0) {
            if (this.forwardLeftButton.isVisible())
                this.forwardLeftButton.setVisible(false);
            if (this.forwardRightButton.isVisible())
                this.forwardRightButton.setVisible(false);
        }
        this.updateJumpButtons();
        double scaleFactor = client.getWindow().getScaleFactor();
        if (clickStartTime > 0 && System.currentTimeMillis() - clickStartTime >= 100) mouseHeldDown(client.mouse.getX() / scaleFactor, client.mouse.getY() / scaleFactor);
        else client.interactionManager.cancelBlockBreaking();
    }

    private long clickStartTime;
    private double[] firstPosition = new double[2];
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        if (mouseY >= (double) (this.height - 22) && this.client != null && this.client.player != null && mouseX >= (double) (centerX - 90) && mouseX <= (double) (centerX + 90)) {
            for (int slot = 0; slot < 9; ++slot) {
                int slotX = centerX - 90 + slot * 20 + 2;
                if (mouseX >= (double) slotX && mouseX <= (double) (slotX + 20)) {
                    this.client.player.getInventory().selectedSlot = slot;
                    return true;
                }
            }
        } else {
            clickStartTime = System.currentTimeMillis();
            firstPosition[0] = mouseX;
            firstPosition[1] = mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!super.mouseReleased(mouseX, mouseY, button) && System.currentTimeMillis() - clickStartTime < 200) {
            clickStartTime = -1;
            HitResult result = TouchUtils.getTargettedObject(mouseX, mouseY);
            if (result == null) return false;

            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) result;
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
            if (result.getType() == HitResult.Type.ENTITY) {
                client.interactionManager.attackEntity(client.player, ((EntityHitResult)result).getEntity());
            }
        }
        firstPosition = new double[2];
        clickStartTime = -1;
        return false;
    }
    public boolean mouseHeldDown(double mouseX, double mouseY) {
        System.out.println(mouseX + " " + firstPosition[0]);
        if (!isDragging() && Math.abs(mouseX-firstPosition[0]) < 1 && Math.abs(mouseY-firstPosition[1]) < 1) {
            HitResult result = TouchUtils.getTargettedObject(mouseX, mouseY);
            if (result == null) return false;

            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) result;
                System.out.println(blockHit.getBlockPos().toString());
                return client.interactionManager.updateBlockBreakingProgress(blockHit.getBlockPos(), blockHit.getSide());
            }
            if (result.getType() == HitResult.Type.ENTITY) {
                client.interactionManager.interactEntity(client.player, ((EntityHitResult)result).getEntity(), client.player.getActiveHand());
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && this.client != null) {
            if (deltaY > 0.01)
                this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs((deltaY / 3.0)*MidnightControlsConfig.touchSpeed/100), 2);
            else this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs((deltaY / 3.0)*MidnightControlsConfig.touchSpeed/100), 1);

            if (deltaX > 0.01)
                this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs((deltaX / 3.0)*MidnightControlsConfig.touchSpeed/100), 2);
            else this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs((deltaX / 3.0)*MidnightControlsConfig.touchSpeed/100), 1);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        KeyBinding.onKeyPressed(InputUtil.fromKeyCode(keyCode, scanCode));
        //return false;
        super.keyPressed(keyCode,scanCode,modifiers);
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.fill(mouseX-10, mouseY-10, mouseX+10, mouseY+10, 0xFFFFFF);
    }
}
