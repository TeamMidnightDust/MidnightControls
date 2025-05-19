package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.util.AbstractSignEditScreenAccessor;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin implements AbstractSignEditScreenAccessor {
    @Shadow @Final private String[] messages;
    @Shadow private SignText text;
    @Shadow @Final protected SignBlockEntity blockEntity;
    @Shadow @Final private boolean front;

    @Override
    public String[] midnightcontrols$getMessages() {
        return messages;
    }

    @Override
    public void midnightcontrols$setMessage(int line, String text) {
        this.messages[line] = text;
        this.text = this.text.withMessage(line, Text.literal(text));
    }

    @Override
    public void midnightcontrols$writeToBlockEntity() {
        this.blockEntity.setText(this.text, this.front);
    }
}
