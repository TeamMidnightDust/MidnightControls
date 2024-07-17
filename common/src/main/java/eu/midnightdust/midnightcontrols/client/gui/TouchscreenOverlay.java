/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import eu.midnightdust.midnightcontrols.client.util.storage.AxisStorage;
import org.thinkingstudio.obsidianui.Position;
import org.thinkingstudio.obsidianui.widget.SpruceButtonWidget;
import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.enums.HudSide;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.EmotecraftCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.touch.gui.ItemUseButtonWidget;
import eu.midnightdust.midnightcontrols.client.touch.gui.SilentTexturedButtonWidget;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.input;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;

/**
 * Represents the touchscreen overlay
 */
public class TouchscreenOverlay extends Screen {
    public static final Identifier WIDGETS_LOCATION = id("textures/gui/widgets.png");
    private SilentTexturedButtonWidget inventoryButton;
    private SilentTexturedButtonWidget swapHandsButton;
    private SilentTexturedButtonWidget dropButton;
    private ItemUseButtonWidget useButton;
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

    public TouchscreenOverlay() {
        super(Text.literal("Touchscreen Overlay"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderInGameBackground(DrawContext context) {}

    @Override
    protected void applyBlur(float delta) {}

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
        float transparency = MidnightControlsConfig.touchTransparency / 100f;

        if (this.client.player.getAbilities().flying) {
            boolean oldStateFly = this.flyButton.isVisible();
            this.jumpButton.setVisible(false);
            this.flyButton.setVisible(true);
            this.flyUpButton.setVisible(true);
            this.flyDownButton.setVisible(true);
            this.flyButton.setAlpha(transparency);
            this.flyUpButton.setAlpha(transparency);
            this.flyDownButton.setAlpha(transparency);
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
            this.jumpButton.setAlpha(transparency);
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
            TextIconButtonWidget emoteButton = TextIconButtonWidget.builder(Text.empty(), btn -> EmotecraftCompat.openEmotecraftScreen(this), true).width(20).texture(id("touch/emote"), 20, 20).build();
            emoteButton.setPosition(scaledWidth / 2 - 30, 0);
            this.addDrawableChild(emoteButton);
        }

        TextIconButtonWidget chatButton = TextIconButtonWidget.builder(Text.empty(), btn -> this.client.setScreen(new ChatScreen("")), true).width(20).texture(id("touch/chat"), 20, 20).build();
        chatButton.setPosition(scaledWidth / 2 - 20 + emoteOffset, 0);
        this.addDrawableChild(chatButton);
        TextIconButtonWidget pauseButton = TextIconButtonWidget.builder(Text.empty(), btn -> this.pauseGame(), true).width(20).texture(id("touch/pause"), 20, 20).build();
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
        this.addDrawableChild(this.dropButton = new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY + 5 + 20), 20, 20, Text.empty(), btn -> {
            if (btn.isActive() && !client.player.isSpectator() && client.player.dropSelectedItem(false)) {
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }, 20, 160, 20, WIDGETS_LOCATION));
        // Use
        this.addDrawableChild(this.useButton = new ItemUseButtonWidget(Position.of(width/2-25, height - 70), 50, 17, Text.translatable(MidnightControlsConstants.NAMESPACE+".action.eat"), btn ->
                client.interactionManager.interactItem(client.player, client.player.getActiveHand())));
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
        initCustomButtons(true);
        initCustomButtons(false);

        this.setButtonProperties(MidnightControlsConfig.touchTransparency / 100f);
    }
    private void initCustomButtons(boolean left) {
        assert client != null;
        Identifier emptySprite = id("touch/empty");
        List<String> list = left ? MidnightControlsConfig.leftTouchBinds : MidnightControlsConfig.rightTouchBinds;
        Sprite missingSprite = client.getGuiAtlasManager().getSprite(MissingSprite.getMissingSpriteId());
        for (int i = 0; i < list.size(); i++) {
            String bindName = list.get(i);
            ButtonBinding binding = InputManager.getBinding(bindName);
            if (binding == null) continue;
            boolean hasTexture = client.getGuiAtlasManager().getSprite(id("binding/"+bindName)) != missingSprite;
            if (MidnightControlsConfig.debug) System.out.println(left +" "+id("binding/"+bindName)+" "+ hasTexture);
            var button = TextIconButtonWidget.builder(Text.translatable(binding.getTranslationKey()), b -> binding.handle(client, 1, ButtonState.PRESS), hasTexture)
                    .texture(hasTexture ? id("binding/"+bindName) : emptySprite, 20, 20).dimension(20, 20).build();
            button.setPosition(left ? (3+(i*23)) : this.width-(23+(i*23)), 3);
            button.setAlpha(MidnightControlsConfig.touchTransparency / 100f);
            this.addDrawableChild(button);
        }
    }
    private void setButtonProperties(float transparency) {
        this.inventoryButton.setAlpha(transparency);
        this.dropButton.setAlpha(transparency);
        this.swapHandsButton.setAlpha(transparency);
        this.jumpButton.setAlpha(transparency);
        this.flyButton.setAlpha(transparency);
        this.flyUpButton.setAlpha(transparency);
        this.useButton.setAlpha(Math.min(transparency+0.1f, 1.0f));
        this.flyDownButton.setAlpha(transparency);
        this.startSneakButton.setAlpha(transparency);
        this.endSneakButton.setAlpha(transparency);
        this.forwardButton.setAlpha(transparency);
        this.forwardLeftButton.setAlpha(Math.max(0.05f, transparency-0.1f));
        this.forwardRightButton.setAlpha(Math.max(0.05f, transparency-0.1f));
        this.leftButton.setAlpha(transparency);
        this.rightButton.setAlpha(transparency);
        this.backButton.setAlpha(transparency);
        this.useButton.setAlpha(Math.min(transparency+0.1f, 1.0f));
        this.endSneakButton.setVisible(false);
        this.forwardLeftButton.setVisible(false);
        this.forwardRightButton.setVisible(false);
    }

    @Override
    public void tick() {
        assert this.client != null;
        assert this.client.interactionManager != null;
        assert this.client.player != null;

        if (this.forwardButtonTick > 0) {
            --this.forwardButtonTick;
        } else {
            this.forwardLeftButton.setVisible(false);
            this.forwardRightButton.setVisible(false);
        }
        this.useButton.setVisible(client.player.getMainHandStack() != null && (client.player.getMainHandStack().getUseAction() != UseAction.NONE || client.player.getMainHandStack().getItem() instanceof ArmorItem) && !TouchUtils.hasInWorldUseAction(client.player.getMainHandStack()));
        this.updateJumpButtons();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && this.client != null) {
            if (!MidnightControlsConfig.invertTouch) {
                deltaX = -deltaX;
                deltaY = -deltaY;
            }
            input.handleLook(this.client, new AxisStorage(GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) Math.abs((deltaY / 3.0)*MidnightControlsConfig.touchSpeed/100), deltaX > 0.01 ? 2 : 1));
            input.handleLook(this.client, new AxisStorage(GLFW_GAMEPAD_AXIS_RIGHT_X, (float) Math.abs((deltaX / 3.0)*MidnightControlsConfig.touchSpeed/100), deltaX > 0.01 ? 2 : 1));
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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
