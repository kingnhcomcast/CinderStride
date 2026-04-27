package io.drahlek.cinderstride.items;

import io.drahlek.cinderstride.Constants;
import io.drahlek.cinderstride.blocks.CooledLava;
import io.drahlek.cinderstride.config.CinderStrideConfig;
import io.drahlek.cinderstride.datacomponents.CinderStrideDataComponents;
import io.drahlek.dirigo.annotation.EventSubscriber;
import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.event.PlayerMovedEvent;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

@Item(id = "cinder_stride_boots"/*, creativeTab = "minecraft:combat"*/)
public class CinderStrideBoots extends net.minecraft.world.item.Item  {
    static public final String NAME = "cinder_stride_boots";
    private static final TagKey<net.minecraft.world.item.Item> REPAIR_MATERIALS = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cinder_stride_boots_repair_materials")
    );

    public CinderStrideBoots(Properties properties) {
        super(properties
                .stacksTo(1)
                .fireResistant()
                .humanoidArmor( new ArmorMaterial(
                        ArmorMaterials.NETHERITE.durability(),
                        ArmorMaterials.NETHERITE.defense(),
                        ArmorMaterials.NETHERITE.enchantmentValue(),
                        ArmorMaterials.NETHERITE.equipSound(),
                        ArmorMaterials.NETHERITE.toughness(),
                        ArmorMaterials.NETHERITE.knockbackResistance(),
                        ArmorMaterials.NETHERITE.repairIngredient(),
                        ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Constants.MOD_ID, NAME))
                ), ArmorType.BOOTS)
                .repairable(REPAIR_MATERIALS)
                .durability(ArmorType.BOOTS.getDurability(ArmorMaterials.NETHERITE.durability())));
    }

    //TODO see https://wiki.fabricmc.net/tutorial:tooltip
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        String key = isUpgraded(stack)
                ? "tooltip.cinderstride.cinderboots.upgraded"
                : "tooltip.cinderstride.cinderboots.base";
        textConsumer.accept(Component.translatable(key).withStyle(ChatFormatting.RED));
    }

    @EventSubscriber(PlayerMovedEvent.class)
    public static void onPlayerMove(PlayerMovedEvent event) {
        var config = CinderStrideConfig.data();

        Player player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }

        //check if player is wearing boots
        ItemStack bootStack = getBoots(player);
        if(bootStack == null)
            return;

        //get blocks at same Y level and 1 below within radius that are lava
        List<BlockPos> lavaBlocks = getLavaBlocksWithinRadius(player, config.getRadius());
        if (lavaBlocks.isEmpty()) {
            return;
        }

        Constants.LOG.debug("Cooling {} lava source blocks near {} at {}", lavaBlocks.size(), player.getName().getString(), event.getNewPos());

        coolBlocks(lavaBlocks, player, bootStack);
    }

    private static List<BlockPos> getLavaBlocksWithinRadius(Player player, int radius) {
        List<BlockPos> lavaBlocks = new ArrayList<>();
        BlockPos center = player.getOnPos();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for(int y = -1; y <= 0; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = player.level().getBlockState(pos);

                    if ((state.is(Blocks.LAVA) && state.getValue(LiquidBlock.LEVEL) == 0) ||
                            state.is(BlockRegistrar.blocks.get(CooledLava.NAME).get())) {
                        //only cool if block above is air
                        BlockState stateAbove = player.level().getBlockState(pos.above());
                        if (stateAbove.isAir() && isAtOrAboveSurface(player, pos)) {
                            lavaBlocks.add(pos);
                        }
                    }
                }
            }
        }

        return lavaBlocks;
    }

    private static boolean isAtOrAboveSurface(Player player, BlockPos pos) {
        return player.getY() >= pos.getY() + 1.0D - 1.0E-3D;
    }

    private static ItemStack getBoots(Player player) {
        ItemStack bootStack = player.getItemBySlot(EquipmentSlot.FEET);
        if(bootStack.getItem() instanceof CinderStrideBoots) {
            return bootStack;
        } else {
            return null;
        }
    }

    private static void coolBlocks(List<BlockPos> blocks, Player player, ItemStack bootStack) {
        if (blocks.isEmpty()) {
            return;
        }

        var cooledLavaBlockSupplier = BlockRegistrar.blocks.get(CooledLava.NAME);
        if (cooledLavaBlockSupplier == null) {
            return;
        }
        var cooledLavaBlock = cooledLavaBlockSupplier.get();

        BlockState stageZero = cooledLavaBlock.defaultBlockState().setValue(CooledLava.STAGE, 0);
        blocks.forEach(blockPos -> coolBlock(player, bootStack, blockPos, stageZero));
    }

    private static void coolBlock(Player player, ItemStack bootStack, BlockPos blockPos, BlockState stageZero) {
        if (canCoolLava(bootStack)) {
            //transform to cooled_lava
            player.level().setBlock(blockPos, stageZero, UPDATE_ALL);

            // Upgraded boots do not lose durability while cooling lava.
            if (!isUpgraded(bootStack)) {
                float durabilityLossChance = CinderStrideConfig.data().getDurabilityLossChance();
                if (player.getRandom().nextFloat() < durabilityLossChance) {
                    bootStack.hurtAndBreak(1, player, EquipmentSlot.FEET);
                }
            }
        }
    }

    private static boolean canCoolLava(ItemStack bootStack) {
        return bootStack.getMaxDamage() - bootStack.getDamageValue() > 1;
    }

    private static boolean isUpgraded(ItemStack bootStack) {
        return Boolean.TRUE.equals(bootStack.get(CinderStrideDataComponents.UPGRADED));
    }
}
