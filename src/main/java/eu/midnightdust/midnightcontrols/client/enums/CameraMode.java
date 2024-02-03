package eu.midnightdust.midnightcontrols.client.enums;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum CameraMode {
    FLAT, ADAPTIVE;
    public Text getTranslatedText() {
        return Text.translatable("midnightcontrols.midnightconfig.enum."+this.getClass().getSimpleName()+"."+this.name());
    }
    public @NotNull CameraMode next() {
        var v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }
}
