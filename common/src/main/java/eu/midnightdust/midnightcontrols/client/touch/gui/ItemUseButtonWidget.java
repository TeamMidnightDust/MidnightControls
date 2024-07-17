package eu.midnightdust.midnightcontrols.client.touch.gui;

import org.thinkingstudio.obsidianui.Position;
import org.thinkingstudio.obsidianui.widget.SpruceButtonWidget;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.item.ArmorItem;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;

public class ItemUseButtonWidget extends SpruceButtonWidget {

    public ItemUseButtonWidget(Position position, int width, int height, Text message, PressAction action) {
        super(position, width, height, message, action);
    }
    @Override
    protected void onRelease(double mouseX, double mouseY) {
        assert client.player != null;
        assert client.interactionManager != null;
        UseAction action = client.player.getMainHandStack().getUseAction();
        if (action == UseAction.SPYGLASS || action == UseAction.TOOT_HORN) client.interactionManager.stopUsingItem(client.player);
        super.onRelease(mouseX, mouseY);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && client.player != null && client.player.getMainHandStack() != null) {
            UseAction action = client.player.getMainHandStack().getUseAction();
            if (action == UseAction.EAT) {
                this.setMessage(Text.translatable(MidnightControlsConstants.NAMESPACE+".action.eat"));
            } else if (action == UseAction.DRINK) {
                this.setMessage(Text.translatable(MidnightControlsConstants.NAMESPACE+".action.drink"));
            } else if (client.player.getMainHandStack().getItem() instanceof ArmorItem) {
                this.setMessage(Text.translatable(MidnightControlsConstants.NAMESPACE+".action.equip"));
            } else if (!action.equals(UseAction.NONE)) {
                this.setMessage(Text.translatable(MidnightControlsConstants.NAMESPACE+".action.use"));
            }
        }
        this.setAlpha(MidnightControlsConfig.touchTransparency / 100f);
        super.setVisible(visible);
    }
}
