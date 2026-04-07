package io.drahlek.cinderstride.items;

import io.drahlek.cinderstride.Constants;
import io.drahlek.cinderstride.blocks.CooledLava;
import io.drahlek.cinderstride.config.CinderStrideConfig;
import io.drahlek.dirigo.annotation.EventSubscriber;
import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.dirigo.schedule.EventScheduler;
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
    private static final int MAX_COOLED_LAVA_STAGE = 3;

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
        Player player = event.getPlayer();
        //check if player is wearing boots
        if(!isWearingBoots(player))
            return;

        //check if player is on ground
        if(!player.onGround()) {
            return;
        }

        //get blocks at same Y level within radius that re lava
        List<BlockPos> lavaBlocks = getLavaBlocksWithinRadius(player, CinderStrideConfig.data().getRadius());

        Constants.LOG.info("Player {} moved to {} while wearing boots, level {} {}", player.getName(), event.getNewPos(), player.level(), player.level().dimension());

        //turn all source blocks with radius to basalt
        coolBlocks(lavaBlocks, player.level());
    }

    private static List<BlockPos> getLavaBlocksWithinRadius(Player player, int radius) {
        List<BlockPos> lavaBlocks = new ArrayList<>();
        BlockPos center = player.getOnPos();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = center.offset(x, 0, z);
                BlockState state = player.level().getBlockState(pos);

                if (state.is(Blocks.LAVA) && state.getValue(LiquidBlock.LEVEL) == 0) {
                    lavaBlocks.add(pos);
                }
            }
        }
        return lavaBlocks;
    }


    private static boolean isWearingBoots(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof CinderStrideBoots;
    }

    private static void coolBlocks(List<BlockPos> blocks, Level level) {
        if (blocks.isEmpty()) {
            return;
        }

        var cooledLavaBlock = BlockRegistrar.blocks.get(CooledLava.NAME);
        if (cooledLavaBlock == null) {
            return;
        }

        BlockState stageZero = cooledLavaBlock.defaultBlockState().setValue(CooledLava.STAGE, 0);
        blocks.forEach(blockPos -> level.setBlock(blockPos, stageZero, UPDATE_ALL));
        scheduleStageTransition(level, blocks, 0);
    }

    private static void scheduleStageTransition(Level level, List<BlockPos> blocks, int currentStage) {
        EventScheduler.INSTANCE.scheduleCallback(level, () -> {
            if (currentStage >= MAX_COOLED_LAVA_STAGE) {
                revertBlock(level, blocks);
                return;
            }

            int nextStage = currentStage + 1;
            blocks.forEach(blockPos -> {
                BlockState state = level.getBlockState(blockPos);
                if (!state.hasProperty(CooledLava.STAGE)) {
                    return;
                }

                if (state.getValue(CooledLava.STAGE) != currentStage) {
                    return;
                }

                level.setBlock(blockPos, state.setValue(CooledLava.STAGE, nextStage), UPDATE_ALL);
            });

            scheduleStageTransition(level, blocks, nextStage);
        }, CinderStrideConfig.data().getDecayTicks());
    }

    private static void revertBlock(Level level, List<BlockPos> blocks) {
        blocks.forEach(blockPos -> {
            level.setBlock(blockPos, Blocks.LAVA.defaultBlockState(), UPDATE_ALL);
        });
    }

}
