package eu.midnightdust.midnightcontrols.client.touch.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemUseAnimation;

public class ItemUseButtonWidget extends SpruceButtonWidget {

    public ItemUseButtonWidget(Position position, int width, int height, Component message, PressAction action) {
        super(position, width, height, message, action);
    }
    @Override
    protected void onRelease(double mouseX, double mouseY) {
        assert client.player != null;
        assert client.gameMode != null;
        ItemUseAnimation action = client.player.getMainHandItem().getUseAnimation();
        if (action == ItemUseAnimation.SPYGLASS || action == ItemUseAnimation.TOOT_HORN) client.gameMode.releaseUsingItem(client.player);
        super.onRelease(mouseX, mouseY);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && client.player != null && client.player.getMainHandItem() != null) {
            ItemUseAnimation action = client.player.getMainHandItem().getUseAnimation();
            if (action == ItemUseAnimation.EAT) {
                this.setMessage(Component.translatable(MidnightControlsConstants.NAMESPACE+".action.eat"));
            } else if (action == ItemUseAnimation.DRINK) {
                this.setMessage(Component.translatable(MidnightControlsConstants.NAMESPACE+".action.drink"));
            } else if (client.player.getMainHandItem().getComponents().has(DataComponents.EQUIPPABLE)) {
                this.setMessage(Component.translatable(MidnightControlsConstants.NAMESPACE+".action.equip"));
            } else if (!action.equals(ItemUseAnimation.NONE)) {
                this.setMessage(Component.translatable(MidnightControlsConstants.NAMESPACE+".action.use"));
            }
        }
        this.setAlpha(MidnightControlsConfig.touchTransparency / 100f);
        super.setVisible(visible);
    }
}
