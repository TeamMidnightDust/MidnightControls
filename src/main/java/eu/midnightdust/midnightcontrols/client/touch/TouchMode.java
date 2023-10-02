package eu.midnightdust.midnightcontrols.client.touch;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum TouchMode {
    CROSSHAIR, FINGER_POS;
    public Text getTranslatedText() {
        return Text.translatable("midnightcontrols.midnightconfig.enum."+this.getClass().getSimpleName()+"."+this.name());
    }
    public @NotNull TouchMode next() {
        var v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }
}
