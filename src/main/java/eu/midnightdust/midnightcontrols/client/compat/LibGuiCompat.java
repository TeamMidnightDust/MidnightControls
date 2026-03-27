package eu.midnightdust.midnightcontrols.client.compat;

//import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
//import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class LibGuiCompat {
    public static boolean handlePress(@NotNull Screen screen) {
//        if (screen instanceof CottonScreenImpl cottonScreen) {
//            if (cottonScreen.getDescription() != null && cottonScreen.getDescription().getFocus() != null) {
//                if (cottonScreen.getDescription().getFocus() instanceof WButton button && button.getOnClick() != null) {
//                    button.getOnClick().run();
//                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//                    return true;
//                }
//            }
//        }
        return false;
    }
}
