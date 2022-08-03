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
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
        super(new LiteralText("Touchscreen overlay"));
        this.mod = mod;
        this.passEvents = true;
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode,scanCode,modifiers);
        //return false;
        return true;
    }

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
    }

    @Override
    protected void init() {
        super.init();
        int scaledWidth = this.client.getWindow().getScaledWidth();
        int scaledHeight = this.client.getWindow().getScaledHeight();
        this.addDrawableChild(new TexturedButtonWidget(scaledWidth / 2 - 20, 0, 20, 20, 0, 106, 20, new Identifier("textures/gui/widgets.png"), 256, 256,
                btn -> this.client.setScreen(new ChatScreen("")), Text.of("")));
        this.addDrawableChild(new TexturedButtonWidget(scaledWidth / 2, 0, 20, 20, 0, 0, 20, WIDGETS_LOCATION, 256, 256,
                btn -> this.pauseGame(false)));
        // Inventory buttons.
        int inventoryButtonX = scaledWidth / 2;
        int inventoryButtonY = scaledHeight - 16 - 5;
        if (this.client.options.mainArm == Arm.LEFT) {
            inventoryButtonX = inventoryButtonX - 91 - 24;
        } else {
            inventoryButtonX = inventoryButtonX + 91 + 4;
        }
        this.addDrawableChild(new TexturedButtonWidget(inventoryButtonX, inventoryButtonY, 20, 20, 20, 0, 20, WIDGETS_LOCATION, 256, 256,
                btn -> {
                    if (this.client.interactionManager.hasRidingInventory()) {
                        this.client.player.openRidingInventory();
                    } else {
                        this.client.getTutorialManager().onInventoryOpened();
                        this.client.setScreen(new InventoryScreen(this.client.player));
                    }
                }));
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
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY), 20, 20, Text.of(""),
                button -> {
                    if (button.isActive()) {
                        if (!this.client.player.isSpectator()) {
                            this.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                        }
                    }
                },0, 160, 20, WIDGETS_LOCATION));
        // Drop
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY + 5 + 20), 20, 20, Text.of(""), btn ->
                ((KeyBindingAccessor) this.client.options.dropKey).midnightcontrols$handlePressState(btn.isActive()), 0, 160, 20, WIDGETS_LOCATION));
        // Jump keys
        this.addDrawableChild(this.jumpButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY), 20, 20, Text.of(""), this::handleJump, 0, 40, 20, WIDGETS_LOCATION));
        this.addDrawableChild(this.flyButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY), 20, 20, Text.of(""),btn -> {
                    if (this.flyButtonEnableTicks == 0) this.client.player.getAbilities().flying = false;
                }, 20, 40, 20, WIDGETS_LOCATION)
        );
        this.addDrawableChild(this.flyUpButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY - 5 - 20), 20, 20,Text.of(""),
                this::handleJump, 40, 40, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.flyDownButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY + 20 + 5), 20, 20, Text.of(""),
                btn -> ((KeyBindingAccessor) this.client.options.sneakKey).midnightcontrols$handlePressState(btn.isActive()), 60, 40, 20, WIDGETS_LOCATION
        ));
        this.updateJumpButtons();
        // Movements keys
        this.addDrawableChild((this.startSneakButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY), 20, 20, Text.of(""), btn -> {
                    if (btn.isActive()) {
                        ((KeyBindingAccessor) this.client.options.sneakKey).midnightcontrols$handlePressState(true);
                        this.startSneakButton.setVisible(false);
                        this.endSneakButton.setVisible(true);
                    }
                }, 0, 120, 20, WIDGETS_LOCATION))
        );
        this.addDrawableChild((this.endSneakButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY), 20, 20, Text.of(""), btn -> {
            if (btn.isActive()) {
                ((KeyBindingAccessor) this.client.options.sneakKey).midnightcontrols$handlePressState(false);
                this.endSneakButton.setVisible(false);
                this.startSneakButton.setVisible(true);
            }
        }, 20, 120, 20, WIDGETS_LOCATION)));
        this.endSneakButton.setVisible(false);
        this.addDrawableChild(this.forwardLeftButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY - 5 - 20), 20, 20, Text.of(""), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.client.options.leftKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 80, 80, 20, WIDGETS_LOCATION
        ));
        this.forwardLeftButton.setVisible(false);
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY - 5 - 20), 20, 20, Text.of(""), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
            this.forwardLeftButton.setVisible(true);
            this.forwardRightButton.setVisible(true);
        }, 0, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.forwardRightButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY - 5 - 20), 20, 20, Text.of(""), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.client.options.rightKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 100, 80, 20, WIDGETS_LOCATION
        ));
        this.forwardRightButton.setVisible(true);
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY), 20, 20, Text.of(""),
                btn -> ((KeyBindingAccessor) this.client.options.rightKey).midnightcontrols$handlePressState(btn.isActive()), 20, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY + 20 + 5), 20, 20, Text.of(""),
                btn -> ((KeyBindingAccessor) this.client.options.backKey).midnightcontrols$handlePressState(btn.isActive()), 40, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY), 20, 20, Text.of(""),
                btn -> ((KeyBindingAccessor) this.client.options.leftKey).midnightcontrols$handlePressState(btn.isActive()), 60, 80, 20, WIDGETS_LOCATION
        ));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY >= (double) (this.height - 22) && this.client != null && this.client.player != null) {
            int centerX = this.width / 2;
            if (mouseX >= (double) (centerX - 90) && mouseX <= (double) (centerX + 90)) {
                for (int slot = 0; slot < 9; ++slot) {
                    int slotX = centerX - 90 + slot * 20 + 2;
                    if (mouseX >= (double) slotX && mouseX <= (double) (slotX + 20)) {
                        this.client.player.getInventory().selectedSlot = slot;
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && this.client != null) {
            if (deltaY > 0.01)
                this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs(deltaY / 5.0), 2);
            else if (deltaY < 0.01)
                this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs(deltaY / 5.0), 1);

            if (deltaX > 0.01)
                this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs(deltaX / 5.0), 2);
            else if (deltaX < 0.01)
                this.mod.input.handleLook(this.client, GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs(deltaX / 5.0), 1);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
