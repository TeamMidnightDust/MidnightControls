/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.touch.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import eu.midnightdust.midnightcontrols.client.touch.TouchInput;
import eu.midnightdust.midnightcontrols.client.util.storage.AxisStorage;
import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.enums.HudSide;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.EmotecraftCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemUseAnimation;

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
        super(Component.literal("Touchscreen Overlay"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {}

    private void pauseGame() {
        assert this.minecraft != null;
        this.minecraft.setScreen(new PauseScreen(true));
        if (this.minecraft.hasSingleplayerServer() && !Objects.requireNonNull(this.minecraft.getSingleplayerServer()).isPublished()) {
            this.minecraft.getSoundManager().pauseAllExcept();
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
        assert this.minecraft != null;
        assert this.minecraft.player != null;
        float transparency = MidnightControlsConfig.touchTransparency / 100f;

        if (this.minecraft.player.getAbilities().flying) {
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
        assert this.minecraft != null;
        ((KeyBindingAccessor) this.minecraft.options.keyJump).midnightcontrols$handlePressState(btn.isActive());
    }
    /**
     * Handles the jump button.
     *
     * @param state   The state.
     */
    private void setJump(boolean state) {
        assert this.minecraft != null;
        ((KeyBindingAccessor) this.minecraft.options.keyJump).midnightcontrols$handlePressState(state);
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        assert this.minecraft.player != null;
        assert this.minecraft.gameMode != null;
        int scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = this.minecraft.getWindow().getGuiScaledHeight();
        int emoteOffset = 0;
        if (PlatformFunctions.isModLoaded("emotecraft")) {
            emoteOffset = 10;
            SpriteIconButton emoteButton = SpriteIconButton.builder(Component.empty(), btn -> EmotecraftCompat.openEmotecraftScreen(this), true).width(20).sprite(id("touch/emote"), 20, 20).build();
            emoteButton.setPosition(scaledWidth / 2 - 30, 0);
            this.addRenderableWidget(emoteButton);
        }

        SpriteIconButton chatButton = SpriteIconButton.builder(Component.empty(), btn -> this.minecraft.setScreen(new ChatScreen("", true)), true).width(20).sprite(id("touch/chat"), 20, 20).build();
        chatButton.setPosition(scaledWidth / 2 - 20 + emoteOffset, 0);
        this.addRenderableWidget(chatButton);
        SpriteIconButton pauseButton = SpriteIconButton.builder(Component.empty(), btn -> this.pauseGame(), true).width(20).sprite(id("touch/pause"), 20, 20).build();
        pauseButton.setPosition(scaledWidth / 2 + emoteOffset, 0);
        this.addRenderableWidget(pauseButton);
        // Inventory buttons.
        int inventoryButtonX = scaledWidth / 2;
        int inventoryButtonY = scaledHeight - 16 - 5;
        if (this.minecraft.options.mainHand().get() == HumanoidArm.LEFT) {
            inventoryButtonX = inventoryButtonX - 91 - 24;
        } else {
            inventoryButtonX = inventoryButtonX + 91 + 4;
        }
        this.addRenderableWidget(this.inventoryButton = new SilentTexturedButtonWidget(Position.of(inventoryButtonX, inventoryButtonY), 20, 20, Component.empty(), btn -> {
            if (this.minecraft.gameMode.isServerControlledInventory()) {
                this.minecraft.player.sendOpenInventory();
            } else {
                this.minecraft.getTutorial().onOpenInventory();
                this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
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
        this.addRenderableWidget(this.swapHandsButton = new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY), 20, 20, Component.empty(),
                button -> {
                    if (button.isActive()) {
                        if (!this.minecraft.player.isSpectator()) {
                            Objects.requireNonNull(this.minecraft.getConnection()).send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                        }
                    }
                },0, 160, 20, WIDGETS_LOCATION));
        // Drop
        this.addRenderableWidget(this.dropButton = new SilentTexturedButtonWidget(Position.of(swapHandsX, sneakButtonY + 5 + 20), 20, 20, Component.empty(), btn -> {
            if (btn.isActive() && !minecraft.player.isSpectator() && minecraft.player.drop(false)) {
                minecraft.player.swing(InteractionHand.MAIN_HAND);
            }
        }, 20, 160, 20, WIDGETS_LOCATION));
        // Use
        this.addRenderableWidget(this.useButton = new ItemUseButtonWidget(Position.of(width/2-25, height - 70), 50, 17, Component.translatable(MidnightControlsConstants.NAMESPACE+".action.eat"), btn ->
                minecraft.gameMode.useItem(minecraft.player, minecraft.player.getUsedItemHand())));
        // Jump keys
        this.addRenderableWidget(this.jumpButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY), 20, 20, Component.empty(), this::handleJump, 0, 40, 20, WIDGETS_LOCATION));
        this.addRenderableWidget(this.flyButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY), 20, 20, Component.empty(),btn -> {
                    if (this.flyButtonEnableTicks == 0) this.minecraft.player.getAbilities().flying = false;
                }, 20, 40, 20, WIDGETS_LOCATION)
        );
        this.addRenderableWidget(this.flyUpButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY - 5 - 20), 20, 20,Component.empty(),
                this::handleJump, 40, 40, 20, WIDGETS_LOCATION
        ));
        this.addRenderableWidget(this.flyDownButton = new SilentTexturedButtonWidget(Position.of(jumpButtonX, sneakButtonY + 20 + 5), 20, 20, Component.empty(),
                btn -> ((KeyBindingAccessor) this.minecraft.options.keyShift).midnightcontrols$handlePressState(btn.isActive()), 60, 40, 20, WIDGETS_LOCATION
        ));
        this.updateJumpButtons();
        // Movements keys
        this.addRenderableWidget((this.startSneakButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY), 20, 20, Component.empty(), btn -> {
                    if (btn.isActive()) {
                        ((KeyBindingAccessor) this.minecraft.options.keyShift).midnightcontrols$handlePressState(true);
                        this.startSneakButton.setVisible(false);
                        this.endSneakButton.setVisible(true);
                    }
                }, 0, 120, 20, WIDGETS_LOCATION))
        );
        this.addRenderableWidget((this.endSneakButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY), 20, 20, Component.empty(), btn -> {
            if (btn.isActive()) {
                ((KeyBindingAccessor) this.minecraft.options.keyShift).midnightcontrols$handlePressState(false);
                this.endSneakButton.setVisible(false);
                this.startSneakButton.setVisible(true);
            }
        }, 20, 120, 20, WIDGETS_LOCATION)));
        this.addRenderableWidget(this.forwardLeftButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY - 5 - 20), 20, 20, Component.empty(), btn -> {
            ((KeyBindingAccessor) this.minecraft.options.keyUp).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.minecraft.options.keyLeft).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 80, 80, 20, WIDGETS_LOCATION
        ));
        this.addRenderableWidget(this.forwardButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY - 5 - 20), 20, 20, Component.empty(), btn -> {
            ((KeyBindingAccessor) this.minecraft.options.keyUp).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
            this.forwardLeftButton.setVisible(true);
            this.forwardRightButton.setVisible(true);
        }, 0, 80, 20, WIDGETS_LOCATION
        ));
        this.addRenderableWidget(this.forwardRightButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY - 5 - 20), 20, 20, Component.empty(), btn -> {
            ((KeyBindingAccessor) this.minecraft.options.keyUp).midnightcontrols$handlePressState(btn.isActive());
            ((KeyBindingAccessor) this.minecraft.options.keyRight).midnightcontrols$handlePressState(btn.isActive());
            this.updateForwardButtonsState(btn.isActive());
        }, 100, 80, 20, WIDGETS_LOCATION
        ));

        this.addRenderableWidget(this.rightButton =new SilentTexturedButtonWidget(Position.of(sneakButtonX + 20 + 5, sneakButtonY), 20, 20, Component.empty(),
                btn -> ((KeyBindingAccessor) this.minecraft.options.keyRight).midnightcontrols$handlePressState(btn.isActive()), 20, 80, 20, WIDGETS_LOCATION
        ));
        this.addRenderableWidget(this.backButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX, sneakButtonY + 20 + 5), 20, 20, Component.empty(),
                btn -> ((KeyBindingAccessor) this.minecraft.options.keyDown).midnightcontrols$handlePressState(btn.isActive()), 40, 80, 20, WIDGETS_LOCATION
        ));
        this.addRenderableWidget(this.leftButton = new SilentTexturedButtonWidget(Position.of(sneakButtonX - 20 - 5, sneakButtonY), 20, 20, Component.empty(),
                btn -> ((KeyBindingAccessor) this.minecraft.options.keyLeft).midnightcontrols$handlePressState(btn.isActive()), 60, 80, 20, WIDGETS_LOCATION
        ));
        initCustomButtons(true);
        initCustomButtons(false);

        this.setButtonProperties(MidnightControlsConfig.touchTransparency / 100f);
    }
    private void initCustomButtons(boolean left) {
        assert minecraft != null;
        Identifier emptySprite = id("touch/empty");
        List<String> list = left ? MidnightControlsConfig.leftTouchBinds : MidnightControlsConfig.rightTouchBinds;
        TextureAtlasSprite missingSprite = minecraft.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).missingSprite();
        for (int i = 0; i < list.size(); i++) {
            String bindName = list.get(i);
            ButtonBinding binding = InputManager.getBinding(bindName);
            if (binding == null) continue;
            boolean hasTexture = minecraft.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(id("binding/"+bindName)) != missingSprite;
            if (MidnightControlsConfig.debug) System.out.println(left +" "+id("binding/"+bindName)+" "+ hasTexture);
            var button = SpriteIconButton.builder(Component.translatable(binding.getTranslationKey()), b -> {
                    binding.handle(minecraft, 1.0f, ButtonState.PRESS);
                    if (binding.asKeyBinding().isPresent()) {
                        binding.asKeyBinding().get().setDown(true);
                        ((KeyBindingAccessor)binding.asKeyBinding().get()).midnightcontrols$press();
                    }
                }, hasTexture)
                    .sprite(hasTexture ? id("binding/"+bindName) : emptySprite, 20, 20).size(20, 20).build();
            button.setPosition(left ? (3+(i*23)) : this.width-(23+(i*23)), 3);
            button.setAlpha(MidnightControlsConfig.touchTransparency / 100f);
            this.addRenderableWidget(button);
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
        assert this.minecraft != null;
        assert this.minecraft.gameMode != null;
        assert this.minecraft.player != null;

        if (this.forwardButtonTick > 0) {
            --this.forwardButtonTick;
        } else {
            this.forwardLeftButton.setVisible(false);
            this.forwardRightButton.setVisible(false);
        }
        this.useButton.setVisible(minecraft.player.getMainHandItem() != null && (minecraft.player.getMainHandItem().getUseAnimation() != ItemUseAnimation.NONE || minecraft.player.getMainHandItem().getComponents().has(DataComponents.EQUIPPABLE)) && !TouchUtils.hasInWorldUseAction(minecraft.player.getMainHandItem()));
        this.updateJumpButtons();
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1 && this.minecraft != null) {
            if (TouchInput.isDragging) {
                if (!MidnightControlsConfig.invertTouch) {
                    deltaX = -deltaX;
                    deltaY = -deltaY;
                }
                input.handleTouchscreenLook(AxisStorage.of(GLFW_GAMEPAD_AXIS_RIGHT_Y, (float) deltaY, 0.25d));
                input.handleTouchscreenLook(AxisStorage.of(GLFW_GAMEPAD_AXIS_RIGHT_X, (float) deltaX, 0.25d));
            }
            else TouchInput.isDragging = true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }
}
