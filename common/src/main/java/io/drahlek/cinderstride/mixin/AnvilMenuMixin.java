package io.drahlek.cinderstride.mixin;

import io.drahlek.cinderstride.enchantments.CinderStrideEnchantmentRules;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Stops ability to combine boots with FrostWalker enchant book in an anvil
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Shadow
    @Final
    private DataSlot cost;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void cinderstride$blockFrostWalkerBookOnCinderStrideBoots(CallbackInfo ci) {
        ItemCombinerMenuAccessor combiner = (ItemCombinerMenuAccessor) this;
        Container inputSlots = combiner.cinderstride$getInputSlots();
        ResultContainer resultSlots = combiner.cinderstride$getResultSlots();
        ItemStack target = inputSlots.getItem(AnvilMenu.INPUT_SLOT);
        ItemStack source = inputSlots.getItem(AnvilMenu.ADDITIONAL_SLOT);
        if (CinderStrideEnchantmentRules.hasBlockedEnchantmentsFor(target, source)) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            cost.set(0);
            ((AbstractContainerMenu) (Object) this).broadcastChanges();
            ci.cancel();
        }
    }
}
