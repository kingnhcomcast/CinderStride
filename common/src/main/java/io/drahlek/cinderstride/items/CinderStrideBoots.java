package io.drahlek.cinderstride.items;

import io.drahlek.cinderstride.Constants;
import io.drahlek.dirigo.annotation.Item;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@Item(id = "cinder_stride_boots"/*, creativeTab = "minecraft:combat"*/)
public class CinderStrideBoots extends net.minecraft.world.item.Item  {
    static public final String NAME = "cinder_stride_boots";
    //static public final ResourceKey<CreativeModeTab> CREATIVE_TAB = CreativeModeTabs.COMBAT;

    public static Properties PROPERTIES = new Properties().humanoidArmor( new ArmorMaterial(
                    ArmorMaterials.NETHERITE.durability(),
                    ArmorMaterials.NETHERITE.defense(),
                    ArmorMaterials.NETHERITE.enchantmentValue(),
                    ArmorMaterials.NETHERITE.equipSound(),
                    ArmorMaterials.NETHERITE.toughness(),
                    ArmorMaterials.NETHERITE.knockbackResistance(),
                    ArmorMaterials.NETHERITE.repairIngredient(),
                    ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Constants.MOD_ID, NAME))
            ), ArmorType.BOOTS)
            .stacksTo(1)
            .fireResistant()
            .durability(ArmorType.BOOTS.getDurability(ArmorMaterials.NETHERITE.durability()));

    public CinderStrideBoots(Properties properties) {
        super(properties);
    }

    //TODO see https://wiki.fabricmc.net/tutorial:tooltip
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.cinderstride." + NAME).withStyle(ChatFormatting.RED));
    }
}
