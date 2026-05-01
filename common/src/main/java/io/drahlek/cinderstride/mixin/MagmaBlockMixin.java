package io.drahlek.cinderstride.mixin;

import io.drahlek.cinderstride.items.CinderStrideBoots;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.MagmaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {

    @Redirect(
            method = "stepOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean cinderstride$skipMagmaDamage(Entity entity, DamageSource source, float amount) {
        if (entity instanceof LivingEntity living) {
            ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);

            if (boots.getItem() instanceof CinderStrideBoots) {
                return false; // prevent magma damage
            }
        }

        return entity.hurt(source, amount); // vanilla behavior
    }
}
