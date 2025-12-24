package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.util.AbstractSignEditScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin implements AbstractSignEditScreenAccessor {
    @Shadow @Final private String[] messages;
    @Shadow private SignText text;
    @Shadow @Final protected SignBlockEntity sign;
    @Shadow @Final private boolean isFrontText;

    @Override
    public String[] midnightcontrols$getMessages() {
        return messages;
    }

    @Override
    public void midnightcontrols$setMessage(int line, String text) {
        this.messages[line] = text;
        this.text = this.text.setMessage(line, Component.literal(text));
    }

    @Override
    public void midnightcontrols$writeToBlockEntity() {
        this.sign.setText(this.text, this.isFrontText);
    }
}
