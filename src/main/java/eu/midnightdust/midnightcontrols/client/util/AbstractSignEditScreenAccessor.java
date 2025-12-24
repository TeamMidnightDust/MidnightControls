package eu.midnightdust.midnightcontrols.client.util;

import org.spongepowered.asm.mixin.Unique;

public interface AbstractSignEditScreenAccessor {
    @Unique
    String[] midnightcontrols$getMessages();

    @Unique
    void midnightcontrols$setMessage(int line, String text);

    @Unique
    void midnightcontrols$writeToBlockEntity();
}
