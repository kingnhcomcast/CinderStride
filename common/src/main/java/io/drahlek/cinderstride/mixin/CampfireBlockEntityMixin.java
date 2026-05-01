package io.drahlek.cinderstride.mixin;

import io.drahlek.cinderstride.blocks.HearthFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin {
    @Inject(method = "cookTick", at = @At("HEAD"), cancellable = true)
    private static void cinderstride$accelerateHearthfireCooking(
            Level level,
            BlockPos pos,
            BlockState state,
            CampfireBlockEntity blockEntity,
            CallbackInfo ci
    ) {
        if (!HearthFireBlock.isHearthfire(state)) {
            return;
        }

        HearthFireBlock.cookTick(level, pos, state, blockEntity);
        ci.cancel();
    }

    @Inject(method = "particleTick", at = @At("HEAD"), cancellable = true)
    private static void cinderstride$replaceHearthfireParticles(
            Level level,
            BlockPos pos,
            BlockState state,
            CampfireBlockEntity blockEntity,
            CallbackInfo ci
    ) {
        if (!HearthFireBlock.isHearthfire(state)) {
            return;
        }

        HearthFireBlock.particleTick(level, pos, state, blockEntity);
        ci.cancel();
    }
}
