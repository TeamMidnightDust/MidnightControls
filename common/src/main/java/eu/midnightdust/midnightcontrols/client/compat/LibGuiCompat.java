package eu.midnightdust.midnightcontrols.client.compat;

import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class LibGuiCompat {
    public static boolean handlePress(@NotNull Screen screen) {
        if (screen instanceof CottonScreenImpl cottonScreen) {
            if (cottonScreen.getDescription() != null && cottonScreen.getDescription().getFocus() != null) {
                if (cottonScreen.getDescription().getFocus() instanceof WButton button && button.getOnClick() != null) {
                    button.getOnClick().run();
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        return false;
    }
}
