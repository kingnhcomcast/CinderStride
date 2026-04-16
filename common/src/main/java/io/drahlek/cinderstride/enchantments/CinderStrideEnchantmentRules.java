package io.drahlek.cinderstride.enchantments;

import io.drahlek.cinderstride.items.CinderStrideBoots;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class CinderStrideEnchantmentRules {
    public static final String FROST_WALKER_NOT_ALLOWED_MESSAGE = "Frost Walker is not allowed on CinderStride Boots";

    private CinderStrideEnchantmentRules() {
    }

    public static boolean blocks(Holder<Enchantment> enchantment, ItemStack stack) {
        return isCinderStrideBoots(stack) && isFrostWalker(enchantment);
    }

    public static boolean hasBlockedEnchantmentsFor(ItemStack target, ItemStack source) {
        return isCinderStrideBoots(target)
                && hasFrostWalker(EnchantmentHelper.getEnchantmentsForCrafting(source));
    }

    public static boolean isCinderStrideBoots(ItemStack stack) {
        return stack.getItem() instanceof CinderStrideBoots;
    }

    public static boolean isFrostWalker(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.FROST_WALKER);
    }

    public static boolean hasFrostWalker(ItemEnchantments enchantments) {
        return enchantments.keySet().stream().anyMatch(CinderStrideEnchantmentRules::isFrostWalker);
    }

    public static ItemEnchantments withoutFrostWalker(ItemEnchantments enchantments) {
        if (!hasFrostWalker(enchantments)) {
            return enchantments;
        }

        var mutable = new ItemEnchantments.Mutable(enchantments);
        mutable.removeIf(CinderStrideEnchantmentRules::isFrostWalker);
        return mutable.toImmutable();
    }
}
