package eu.midnightdust.midnightcontrols.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface KeyBindingIDAccessor {
    @Accessor @Final
    static Map<String, KeyMapping> getALL() {return null;};
}
