package eu.midnightdust.midnightcontrols.fabric.event;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.clickInterceptor;

public class MouseClickListener implements ScreenMouseEvents.AllowMouseClick {
    private final Screen screen;

    public MouseClickListener(Screen screen) {
        this.screen = screen;
    }

    @Override
    public boolean allowMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        clickInterceptor.intercept(screen, mouseX, mouseY);
        return true;
    }

    // Add equals and hashCode to prevent duplicate registrations
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MouseClickListener) {
            return ((MouseClickListener) obj).screen == this.screen;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return screen.hashCode();
    }




}
