package eu.midnightdust.midnightcontrols.client.gui.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceTexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SilentTexturedButtonWidget extends SpruceTexturedButtonWidget {
    public SilentTexturedButtonWidget(Position position, int width, int height, Text message, PressAction action, int u, int v, int hoveredVOffset, Identifier texture) {
        super(position, width, height, message, action, u, v, hoveredVOffset, texture);
    }

    public SilentTexturedButtonWidget(Position position, int width, int height, Text message, boolean showMessage, PressAction action, int u, int v, int hoveredVOffset, Identifier texture) {
        super(position, width, height, message, showMessage, action, u, v, hoveredVOffset, texture);
    }

    public SilentTexturedButtonWidget(Position position, int width, int height, Text message, PressAction action, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight) {
        super(position, width, height, message, action, u, v, hoveredVOffset, texture, textureWidth, textureHeight);
    }

    public SilentTexturedButtonWidget(Position position, int width, int height, Text message, boolean showMessage, PressAction action, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight) {
        super(position, width, height, message, showMessage, action, u, v, hoveredVOffset, texture, textureWidth, textureHeight);
    }
    @Override
    public void playDownSound() {}
}
