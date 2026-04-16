package io.drahlek.cinderstride.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Stops ability to combine boots with FrostWalker enchant book in an anvil
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {
    @Accessor("inputSlots")
    Container cinderstride$getInputSlots();

    @Accessor("resultSlots")
    ResultContainer cinderstride$getResultSlots();
}
