package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBehaviour.class)
public interface AbstractBlockAccessor {
    @Invoker("canSurvive")
    boolean midnightcontrols$canPlaceAt(BlockState state, LevelReader world, BlockPos pos);
}
