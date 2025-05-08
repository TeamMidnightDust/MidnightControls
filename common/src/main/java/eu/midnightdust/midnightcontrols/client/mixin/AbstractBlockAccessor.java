package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractBlock.class)
public interface AbstractBlockAccessor {
    @Invoker("canPlaceAt")
    boolean midnightcontrols$canPlaceAt(BlockState state, WorldView world, BlockPos pos);
}
