package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface KeyBindingIDAccessor {
    @Accessor @Final
    static Map<String, KeyBinding> getKEYS_BY_ID() {return null;};
}
