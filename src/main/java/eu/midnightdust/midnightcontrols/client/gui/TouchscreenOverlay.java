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
import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.ButtonState;
import eu.midnightdust.midnightcontrols.client.HudSide;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.EmotecraftCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.gui.widget.SilentTexturedButtonWidget;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import io.github.kosmx.emotes.arch.gui.EmoteMenuImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;

/**
 * Represents the touchscreen overlay
 */
public class TouchscreenOverlay extends Screen {
    public static final Identifier WIDGETS_LOCATION = new Identifier("midnightcontrols", "textures/gui/widgets.png");
    private final MidnightControlsClient mod;
    private SilentTexturedButtonWidget inventoryButton;
    private SilentTexturedButtonWidget swapHandsButton;
    private SilentTexturedButtonWidget dropButton;
    private SilentTexturedButtonWidget jumpButton;
    private SilentTexturedButtonWidget flyButton;
    private SilentTexturedButtonWidget flyUpButton;
    private SilentTexturedButtonWidget flyDownButton;
    private SilentTexturedButtonWidget forwardButton;
    private SilentTexturedButtonWidget forwardLeftButton;
    private SilentTexturedButtonWidget forwardRightButton;
    private SilentTexturedButtonWidget leftButton;
    private SilentTexturedButtonWidget rightButton;
    private SilentTexturedButtonWidget backButton;
    private SilentTexturedButtonWidget startSneakButton;
    private SilentTexturedButtonWidget endSneakButton;
    private int flyButtonEnableTicks = 0;
    private int forwardButtonTick = 0;
    public long clickStartTime;
    public HitResult firstHitResult = null;
    public static TouchscreenOverlay instance;

    public TouchscreenOverlay(@NotNull MidnightControlsClient mod) {
        super(Text.literal("Touchscreen overlay"));
        this.mod = mod;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderInGameBackground(DrawContext context) {}

    private void pauseGame() {
        assert this.client != null;
        this.client.setScreen(new GameMenuScreen(true));
        if (this.client.isIntegratedServerRunning() && !Objects.requireNonNull(this.client.getServer()).isRemote()) {
            this.client.getSoundManager().pauseAll();
        }
    }

    /**
     * Updates the forward button ticks cooldown.
     *
     * @param state The button state.
     *
     */
    private void updateForwardButtonsState(boolean state) {
        this.forwardButtonTick = state ? -1 : 20;
    }

    /**
     * Updates the jump buttons.
     */
    private void updateJumpButtons() {
        assert this.client != null;
        assert this.client.player != null;

        if (this.client.player.getAbilities().flying) {
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
        assert this.client != null;
        ((KeyBindingAccessor) this.client.options.jumpKey).midnightcontrols$handlePressState(btn.isActive());
    }
    /**
     * Handles the jump button.
     *
     * @param state   The state.
     */
    private void setJump(boolean state) {
        assert this.client != null;
        ((KeyBindingAccessor) this.client.options.jumpKey).midnightcontrols$handlePressState(state);
    }

    @Override
    protected void init() {
        super.init();
        assert this.client != null;
        assert this.client.player != null;
        assert this.client.interactionManager != null;
        int scaledWidth = this.client.getWindow().getScaledWidth();
        int scaledHeight = this.client.getWindow().getScaledHeight();
        int emoteOffset = 0;
        if (PlatformFunctions.isModLoaded("emotecraft")) {
            emoteOffset = 10;
            TextIconButtonWidget emoteButton = TextIconButtonWidget.builder(Text.empty(), btn -> EmotecraftCompat.openEmotecraftScreen(this), true).width(20).texture(new Identifier(MidnightControlsConstants.NAMESPACE, "touch/emote"), 20, 20).build();
            emoteButton.setPosition(scaledWidth / 2 - 30, 0);
            this.addDrawableChild(emoteButton);
        }

        TextIconButtonWidget chatButton = TextIconButtonWidget.builder(Text.empty(), btn -> this.client.setScreen(new ChatScreen("")), true).width(20).texture(new Identifier(MidnightControlsConstants.NAMESPACE, "touch/chat"), 20, 20).build();
        chatButton.setPosition(scaledWidth / 2 - 20 + emoteOffset, 0);
        this.addDrawableChild(chatButton);
        TextIconButtonWidget pauseButton = TextIconButtonWidget.builder(Text.empty(), btn -> this.pauseGame(), true).width(20).texture(new Identifier(MidnightControlsConstants.NAMESPACE, "touch/pause"), 20, 20).build();
        pauseButton.setPosition(scaledWidth / 2 + emoteOffset, 0);
        this.addDrawableChild(pauseButton);
        // Inventory buttons.
        int inventoryButtonX = scaledWidth / 2;
        int inventoryButtonY = scaledHeight - 16 - 5;
        if (this.client.options.getMainArm().getValue() == Arm.LEFT) {
            inventoryButtonX = inventoryButtonX - 91 - 24;
        } else {
            inventoryButtonX = inventoryButtonX + 91 + 4;
        }
        this.addDrawableChild(this.inventoryButton = new SilentTexturedButtonWidget(Position.of(inventoryButtonX, inventoryButtonY), 20, 20, Text.empty(), btn -> {
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
        this.addDrawableChild(this.swapHandsButton = new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY), 20, 20, Text.empty(),
                button -> {
                    if (button.isActive()) {
                        if (!this.client.player.isSpectator()) {
                            Objects.requireNonNull(this.client.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                        }
                    }
                },0, 160, 20, WIDGETS_LOCATION));
        // Drop
        this.addDrawableChild(this.dropButton = new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY + 5 + 20), 20, 20, Text.empty(), btn ->
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
        this.addDrawableChild(this.forwardLeftButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY - 5 - 20), 20, 20, Text.empty(), btn -> {
            ((KeyBindingAccessor) this.client.options.forwardKey).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.client.options.leftKey).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 80, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.forwardButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY - 5 - 20), 20, 20, Text.empty(), btn -> {
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

        this.addDrawableChild(this.rightButton =new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.rightKey).midnightcontrols$handlePressState(btn.isActive()), 20, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.backButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY + 20 + 5), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.backKey).midnightcontrols$handlePressState(btn.isActive()), 40, 80, 20, WIDGETS_LOCATION
        ));
        this.addDrawableChild(this.leftButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY), 20, 20, Text.empty(),
                btn -> ((KeyBindingAccessor) this.client.options.leftKey).midnightcontrols$handlePressState(btn.isActive()), 60, 80, 20, WIDGETS_LOCATION
        ));
        Identifier emptySprite = new Identifier(MidnightControlsConstants.NAMESPACE, "touch/empty");
        for (int i = 0; i < MidnightControlsConfig.leftTouchBinds.size(); i++) {
            String bindName = MidnightControlsConfig.leftTouchBinds.get(i);
            ButtonBinding binding = InputManager.getBinding(bindName);
            if (binding == null) continue;
            boolean hasTexture = client.getTextureManager().getOrDefault(new Identifier(MidnightControlsConstants.NAMESPACE, "textures/gui/sprites/icon/"+bindName+".png"), null) != null;
            var button = TextIconButtonWidget.builder(Text.translatable(binding.getTranslationKey()), b -> binding.handle(client, 1, ButtonState.PRESS), false)
                    .texture(hasTexture ? new Identifier(MidnightControlsConstants.NAMESPACE, "icon/"+bindName) : emptySprite, 20, 20).dimension(20, 20).build();
            button.setPosition(i > 1 ? 3 : 3+(i*23), 3);
            button.setAlpha(MidnightControlsConfig.touchTransparency / 100f);
            this.addDrawableChild(button);
        }

        this.setButtonProperties(MidnightControlsConfig.touchTransparency / 100f);
        TouchscreenOverlay.instance = this;
    }
    private void setButtonProperties(float transparency) {
        this.inventoryButton.setAlpha(transparency);
        this.dropButton.setAlpha(transparency);
        this.swapHandsButton.setAlpha(transparency);
        this.jumpButton.setAlpha(transparency);
        this.flyButton.setAlpha(transparency);
        this.flyUpButton.setAlpha(transparency);
        this.flyDownButton.setAlpha(transparency);
        this.startSneakButton.setAlpha(transparency);
        this.endSneakButton.setAlpha(transparency);
        this.forwardButton.setAlpha(transparency);
        this.forwardLeftButton.setAlpha(Math.max(0.05f, transparency-0.1f));
        this.forwardRightButton.setAlpha(Math.max(0.05f, transparency-0.1f));
        this.leftButton.setAlpha(transparency);
        this.rightButton.setAlpha(transparency);
        this.backButton.setAlpha(transparency);
        this.endSneakButton.setVisible(false);
        this.forwardLeftButton.setVisible(false);
        this.forwardRightButton.setVisible(false);
    }

    @Override
    public void tick() {
        assert this.client != null;
        assert this.client.interactionManager != null;

        if (this.forwardButtonTick > 0) {
            --this.forwardButtonTick;
        } else {
            this.forwardLeftButton.setVisible(false);
            this.forwardRightButton.setVisible(false);
        }
        this.updateJumpButtons();

        double scaleFactor = client.getWindow().getScaleFactor();
        if (clickStartTime > 0 && System.currentTimeMillis() - clickStartTime >= 100) this.mouseHeldDown(client.mouse.getX() / scaleFactor, client.mouse.getY() / scaleFactor);
        else client.interactionManager.cancelBlockBreaking();
    }

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
            boolean bl = super.mouseClicked(mouseX, mouseY, button);
            if (!bl) firstHitResult = TouchUtils.getTargettedObject(mouseX, mouseY);
            return bl;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        firstHitResult = null;
        if (!super.mouseReleased(mouseX, mouseY, button) && System.currentTimeMillis() - clickStartTime < 200) {
            assert client != null;
            assert client.player != null;
            assert client.world != null;
            assert client.interactionManager != null;
            clickStartTime = -1;
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
            }
        }
        clickStartTime = -1;
        return false;
    }
    public void mouseHeldDown(double mouseX, double mouseY) {
        assert client != null;
        assert client.player != null;
        assert client.interactionManager != null;
        if (!isDragging()) {
            HitResult result = TouchUtils.getTargettedObject(mouseX, mouseY);
            if (result == null || firstHitResult == null) return;
            if (result instanceof BlockHitResult blockHit && firstHitResult instanceof BlockHitResult firstBlock && blockHit.getBlockPos().equals(firstBlock.getBlockPos())) {
                if (MidnightControlsConfig.debug) System.out.println(blockHit.getBlockPos().toString());
                client.interactionManager.updateBlockBreakingProgress(blockHit.getBlockPos(), blockHit.getSide());
                firstHitResult = TouchUtils.getTargettedObject(mouseX, mouseY);
            }
            else if (result instanceof EntityHitResult entityHit && firstHitResult instanceof EntityHitResult firstEntity && entityHit.getEntity().getUuid().compareTo(firstEntity.getEntity().getUuid()) == 0) {
                client.interactionManager.interactEntity(client.player, entityHit.getEntity(), client.player.getActiveHand());
                firstHitResult = TouchUtils.getTargettedObject(mouseX, mouseY);
            }
        }
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

            HitResult result = TouchUtils.getTargettedObject(mouseX, mouseY);
            if (result != null && firstHitResult != null) {
                if (result instanceof BlockHitResult blockHit && firstHitResult instanceof BlockHitResult firstBlock && !blockHit.getBlockPos().equals(firstBlock.getBlockPos())) {
                    firstHitResult = null;
                } else if (result instanceof EntityHitResult entityHit && firstHitResult instanceof EntityHitResult firstEntity && entityHit.getEntity().getUuid().compareTo(firstEntity.getEntity().getUuid()) != 0) {
                    firstHitResult = null;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //client.currentScreen = null;
        KeyBinding.onKeyPressed(InputUtil.fromKeyCode(keyCode, scanCode));

        super.keyPressed(keyCode,scanCode,modifiers);
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.fill(mouseX-10, mouseY-10, mouseX+10, mouseY+10, 0xFFFFFF);
    }
}
