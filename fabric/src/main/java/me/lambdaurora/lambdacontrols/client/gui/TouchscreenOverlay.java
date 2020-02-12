/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.client.HudSide;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import me.lambdaurora.spruceui.SpruceTexturedButtonWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;

/**
 * Represents the touchscreen overlay
 */
public class TouchscreenOverlay extends Screen
{
    public static final Identifier                 WIDGETS_LOCATION     = new Identifier("lambdacontrols", "textures/gui/widgets.png");
    private             LambdaControlsClient       mod;
    private             SpruceTexturedButtonWidget jumpButton;
    private             SpruceTexturedButtonWidget flyButton;
    private             SpruceTexturedButtonWidget flyUpButton;
    private             SpruceTexturedButtonWidget flyDownButton;
    private             int                        flyButtonEnableTicks = 0;
    private             int                        forwardButtonTick    = 0;
    private             SpruceTexturedButtonWidget forwardLeftButton;
    private             SpruceTexturedButtonWidget forwardRightButton;
    private             SpruceTexturedButtonWidget startSneakButton;
    private             SpruceTexturedButtonWidget endSneakButton;

    public TouchscreenOverlay(@NotNull LambdaControlsClient mod)
    {
        super(new LiteralText("Touchscreen overlay"));
        this.mod = mod;
        this.passEvents = true;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    private void pauseGame(boolean bl)
    {
        if (this.minecraft == null)
            return;
        boolean bl2 = this.minecraft.isIntegratedServerRunning() && !this.minecraft.getServer().isRemote();
        if (bl2) {
            this.minecraft.openScreen(new GameMenuScreen(!bl));
            this.minecraft.getSoundManager().pauseAll();
        } else {
            this.minecraft.openScreen(new GameMenuScreen(true));
        }
    }

    /**
     * Updates the forward button ticks cooldown.
     *
     * @param state The button state.
     */
    private void updateForwardButtonsState(boolean state)
    {
        if (state)
            this.forwardButtonTick = -1;
        else
            this.forwardButtonTick = 20;
    }

    /**
     * Updates the jump buttons.
     */
    private void updateJumpButtons()
    {
        if (this.minecraft == null)
            return;
        if (this.minecraft.player.abilities.allowFlying && this.minecraft.player.abilities.flying) {
            boolean oldStateFly = this.flyButton.visible;
            this.jumpButton.visible = false;
            this.flyButton.visible = true;
            this.flyUpButton.visible = true;
            this.flyDownButton.visible = true;
            if (oldStateFly != this.flyButton.visible) {
                this.flyButtonEnableTicks = 5;
                this.handleJump(null, false);
            } else if (this.flyButtonEnableTicks > 0)
                this.flyButtonEnableTicks--;
        } else {
            this.jumpButton.visible = true;
            this.flyButton.visible = false;
            this.flyUpButton.visible = false;
            this.flyDownButton.visible = false;
        }
    }

    /**
     * Handles the jump button.
     *
     * @param btn   The pressed button.
     * @param state The state of the jump button.
     */
    private void handleJump(ButtonWidget btn, boolean state)
    {
        ((KeyBindingAccessor) this.minecraft.options.keyJump).lambdacontrols_handlePressState(state);
    }

    @Override
    public void tick()
    {
        if (this.forwardButtonTick > 0) {
            this.forwardButtonTick--;
        } else if (this.forwardButtonTick == 0) {
            if (this.forwardLeftButton.visible)
                this.forwardLeftButton.visible = false;
            if (this.forwardRightButton.visible)
                this.forwardRightButton.visible = false;
        }
        this.updateJumpButtons();
    }

    @Override
    protected void init()
    {
        super.init();
        int scaledWidth = this.minecraft.getWindow().getScaledWidth();
        int scaledHeight = this.minecraft.getWindow().getScaledHeight();
        this.addButton(new TexturedButtonWidget(scaledWidth / 2 - 20, 0, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_LOCATION, 256, 256,
                btn -> this.minecraft.openScreen(new ChatScreen("")), ""));
        this.addButton(new TexturedButtonWidget(scaledWidth / 2, 0, 20, 20, 0, 0, 20, WIDGETS_LOCATION, 256, 256,
                btn -> this.pauseGame(false)));
        // Inventory buttons.
        int inventoryButtonX = scaledWidth / 2;
        int inventoryButtonY = scaledHeight - 16 - 5;
        if (this.minecraft.options.mainArm == Arm.LEFT) {
            inventoryButtonX = inventoryButtonX - 91 - 24;
        } else {
            inventoryButtonX = inventoryButtonX + 91 + 4;
        }
        this.addButton(new TexturedButtonWidget(inventoryButtonX, inventoryButtonY, 20, 20, 20, 0, 20, WIDGETS_LOCATION, 256, 256,
                btn -> {
                    if (this.minecraft.interactionManager.hasRidingInventory()) {
                        this.minecraft.player.openRidingInventory();
                    } else {
                        this.minecraft.getTutorialManager().onInventoryOpened();
                        this.minecraft.openScreen(new InventoryScreen(this.minecraft.player));
                    }
                }));
        int jumpButtonX, swapHandsX, sneakButtonX;
        int sneakButtonY = scaledHeight - 10 - 40 - 5;
        if (this.mod.config.getHudSide() == HudSide.LEFT) {
            jumpButtonX = scaledWidth - 20 - 20;
            swapHandsX = jumpButtonX - 5 - 40;
            sneakButtonX = 10 + 20 + 5;
        } else {
            jumpButtonX = 20;
            swapHandsX = jumpButtonX + 5 + 40;
            sneakButtonX = scaledWidth - 10 - 40 - 5;
        }
        // Swap items hand.
        this.addButton(new SpruceTexturedButtonWidget(swapHandsX, sneakButtonY, 20, 20, 0, 160, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    if (state) {
                        if (!this.minecraft.player.isSpectator()) {
                            this.minecraft.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
                        }
                    }
                }));
        // Drop
        this.addButton(new SpruceTexturedButtonWidget(swapHandsX, sneakButtonY + 5 + 20, 20, 20, 20, 160, 20, WIDGETS_LOCATION,
                (btn, state) -> ((KeyBindingAccessor) this.minecraft.options.keyDrop).lambdacontrols_handlePressState(state)));
        // Jump keys
        this.addButton(this.jumpButton = new SpruceTexturedButtonWidget(jumpButtonX, sneakButtonY, 20, 20, 0, 40, 20, WIDGETS_LOCATION,
                this::handleJump));
        this.addButton(this.flyButton = new SpruceTexturedButtonWidget(jumpButtonX, sneakButtonY, 20, 20, 20, 40, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    if (this.flyButtonEnableTicks == 0) this.minecraft.player.abilities.flying = false;
                }));
        this.addButton(this.flyUpButton = new SpruceTexturedButtonWidget(jumpButtonX, sneakButtonY - 5 - 20, 20, 20, 40, 40, 20, WIDGETS_LOCATION,
                this::handleJump));
        this.addButton(this.flyDownButton = new SpruceTexturedButtonWidget(jumpButtonX, sneakButtonY + 20 + 5, 20, 20, 60, 40, 20, WIDGETS_LOCATION,
                (btn, state) -> ((KeyBindingAccessor) this.minecraft.options.keySneak).lambdacontrols_handlePressState(state)));
        this.updateJumpButtons();
        // Movements keys
        this.addButton((this.startSneakButton = new SpruceTexturedButtonWidget(sneakButtonX, sneakButtonY, 20, 20, 0, 120, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    if (state) {
                        ((KeyBindingAccessor) this.minecraft.options.keySneak).lambdacontrols_handlePressState(true);
                        this.startSneakButton.visible = false;
                        this.endSneakButton.visible = true;
                    }
                })));
        this.addButton((this.endSneakButton = new SpruceTexturedButtonWidget(sneakButtonX, sneakButtonY, 20, 20, 20, 120, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    if (state) {
                        ((KeyBindingAccessor) this.minecraft.options.keySneak).lambdacontrols_handlePressState(false);
                        this.endSneakButton.visible = false;
                        this.startSneakButton.visible = true;
                    }
                })));
        this.endSneakButton.visible = false;
        this.addButton(this.forwardLeftButton = new SpruceTexturedButtonWidget(sneakButtonX - 20 - 5, sneakButtonY - 5 - 20, 20, 20, 80, 80, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    ((KeyBindingAccessor) this.minecraft.options.keyForward).lambdacontrols_handlePressState(state);
                    ((KeyBindingAccessor) this.minecraft.options.keyLeft).lambdacontrols_handlePressState(state);
                    this.updateForwardButtonsState(state);
                }));
        this.forwardLeftButton.visible = false;
        this.addButton(new SpruceTexturedButtonWidget(sneakButtonX, sneakButtonY - 5 - 20, 20, 20, 0, 80, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    ((KeyBindingAccessor) this.minecraft.options.keyForward).lambdacontrols_handlePressState(state);
                    this.updateForwardButtonsState(state);
                    this.forwardLeftButton.visible = true;
                    this.forwardRightButton.visible = true;
                }));
        this.addButton(this.forwardRightButton = new SpruceTexturedButtonWidget(sneakButtonX + 20 + 5, sneakButtonY - 5 - 20, 20, 20, 100, 80, 20, WIDGETS_LOCATION,
                (btn, state) -> {
                    ((KeyBindingAccessor) this.minecraft.options.keyForward).lambdacontrols_handlePressState(state);
                    ((KeyBindingAccessor) this.minecraft.options.keyRight).lambdacontrols_handlePressState(state);
                    this.updateForwardButtonsState(state);
                }));
        this.forwardRightButton.visible = true;
        this.addButton(new SpruceTexturedButtonWidget(sneakButtonX + 20 + 5, sneakButtonY, 20, 20, 20, 80, 20, WIDGETS_LOCATION,
                (btn, state) -> ((KeyBindingAccessor) this.minecraft.options.keyRight).lambdacontrols_handlePressState(state)));
        this.addButton(new SpruceTexturedButtonWidget(sneakButtonX, sneakButtonY + 20 + 5, 20, 20, 40, 80, 20, WIDGETS_LOCATION,
                (btn, state) -> ((KeyBindingAccessor) this.minecraft.options.keyBack).lambdacontrols_handlePressState(state)));
        this.addButton(new SpruceTexturedButtonWidget(sneakButtonX - 20 - 5, sneakButtonY, 20, 20, 60, 80, 20, WIDGETS_LOCATION,
                (btn, state) -> ((KeyBindingAccessor) this.minecraft.options.keyLeft).lambdacontrols_handlePressState(state)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (mouseY >= (double) (this.height - 22) && this.minecraft != null && this.minecraft.player != null) {
            int centerX = this.width / 2;
            if (mouseX >= (double) (centerX - 90) && mouseX <= (double) (centerX + 90)) {
                for (int slot = 0; slot < 9; ++slot) {
                    int slotX = centerX - 90 + slot * 20 + 2;
                    if (mouseX >= (double) slotX && mouseX <= (double) (slotX + 20)) {
                        this.minecraft.player.inventory.selectedSlot = slot;
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && this.minecraft != null) {
            if (deltaY > 0.01)
                this.mod.input.handleLook(this.minecraft, GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs(deltaY / 5.0), 2);
            else if (deltaY < 0.01)
                this.mod.input.handleLook(this.minecraft, GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs(deltaY / 5.0), 1);

            if (deltaX > 0.01)
                this.mod.input.handleLook(this.minecraft, GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs(deltaX / 5.0), 2);
            else if (deltaX < 0.01)
                this.mod.input.handleLook(this.minecraft, GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs(deltaX / 5.0), 1);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
