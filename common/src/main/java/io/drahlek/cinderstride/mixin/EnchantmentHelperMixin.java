package io.drahlek.cinderstride.mixin;

import io.drahlek.cinderstride.enchantments.CinderStrideEnchantmentRules;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;


@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void cinderstride$removeFrostWalkerFromAvailableEnchantments(
            int enchantingPower,
            ItemStack stack,
            Stream<Holder<Enchantment>> enchantments,
            CallbackInfoReturnable<List<EnchantmentInstance>> cir
    ) {
        if (CinderStrideEnchantmentRules.isCinderStrideBoots(stack)) {
            cir.getReturnValue().removeIf(instance -> CinderStrideEnchantmentRules.isFrostWalker(instance.enchantment));
        }
    }

    @Inject(method = "updateEnchantments", at = @At("RETURN"), cancellable = true)
    private static void cinderstride$removeFrostWalkerAfterUpdatingEnchantments(
            ItemStack stack,
            java.util.function.Consumer<ItemEnchantments.Mutable> updater,
            CallbackInfoReturnable<ItemEnchantments> cir
    ) {
        removeFrostWalker(stack, cir.getReturnValue(), cir);
    }

    @Inject(method = "setEnchantments", at = @At("HEAD"), cancellable = true)
    private static void cinderstride$removeFrostWalkerWhenSettingEnchantments(
            ItemStack stack,
            ItemEnchantments enchantments,
            CallbackInfo ci
    ) {
        if (!CinderStrideEnchantmentRules.isCinderStrideBoots(stack)
                || !CinderStrideEnchantmentRules.hasFrostWalker(enchantments)) {
            return;
        }

        stack.set(DataComponents.ENCHANTMENTS, CinderStrideEnchantmentRules.withoutFrostWalker(enchantments));
        ci.cancel();
    }

    private static void removeFrostWalker(
            ItemStack stack,
            ItemEnchantments enchantments,
            CallbackInfoReturnable<ItemEnchantments> cir
    ) {
        if (!CinderStrideEnchantmentRules.isCinderStrideBoots(stack)
                || !CinderStrideEnchantmentRules.hasFrostWalker(enchantments)) {
            return;
        }

        ItemEnchantments cleaned = CinderStrideEnchantmentRules.withoutFrostWalker(enchantments);
        stack.set(DataComponents.ENCHANTMENTS, cleaned);
        cir.setReturnValue(cleaned);
    }
}
