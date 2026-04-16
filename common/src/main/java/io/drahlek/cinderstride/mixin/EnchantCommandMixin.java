package io.drahlek.cinderstride.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.drahlek.cinderstride.enchantments.CinderStrideEnchantmentRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

//Stops /enchant from granting FrostWalker to boots
@Mixin(EnchantCommand.class)
public class EnchantCommandMixin {
    private static final SimpleCommandExceptionType ERROR_FROST_WALKER_NOT_ALLOWED =
            new SimpleCommandExceptionType(Component.literal(CinderStrideEnchantmentRules.FROST_WALKER_NOT_ALLOWED_MESSAGE));

    @Inject(method = "enchant", at = @At("HEAD"))
    private static void cinderstride$blockFrostWalkerOnCinderStrideBoots(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            Holder<Enchantment> enchantment,
            int level,
            CallbackInfoReturnable<Integer> cir
    ) throws CommandSyntaxException {
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity
                    && CinderStrideEnchantmentRules.blocks(enchantment, livingEntity.getMainHandItem())) {
                throw ERROR_FROST_WALKER_NOT_ALLOWED.create();
            }
        }
    }
}
