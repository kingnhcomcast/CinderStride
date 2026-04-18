package io.drahlek.cinderstride.items;

import io.drahlek.cinderstride.Constants;
import io.drahlek.cinderstride.blocks.CooledLava;
import io.drahlek.cinderstride.config.CinderStrideConfig;
import io.drahlek.dirigo.annotation.EventSubscriber;
import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.dirigo.event.PlayerMovedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.Level;
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
                .durability(ArmorType.BOOTS.getDurability(ArmorMaterials.NETHERITE.durability())));
    }

    //TODO see https://wiki.fabricmc.net/tutorial:tooltip
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.cinderstride." + NAME).withStyle(ChatFormatting.RED));
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

        //check if player is on ground
        if(!player.onGround()) {
            return;
        }

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
                        if(stateAbove.isAir()) {
                            lavaBlocks.add(pos);
                        }
                    }
                }
            }
        }

        return lavaBlocks;
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

            //cause durability loss
            float durabilityLossChance = CinderStrideConfig.data().getDurabilityLossChance();
            if (player.getRandom().nextFloat() < durabilityLossChance) {
                bootStack.hurtAndBreak(1, player, EquipmentSlot.FEET);
            }
        }
    }

    private static boolean canCoolLava(ItemStack bootStack) {
        return bootStack.getMaxDamage() - bootStack.getDamageValue() > 1;
    }
}
