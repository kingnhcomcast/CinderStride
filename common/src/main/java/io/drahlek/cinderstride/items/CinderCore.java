package io.drahlek.cinderstride.items;

import io.drahlek.cinderstride.blocks.HearthFireBlock;
import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;
import static net.minecraft.world.level.block.Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS;

@Item(id = "cinder_core", creativeTab = "ingredients")
public class CinderCore extends net.minecraft.world.item.Item  {
    static public final String NAME = "cinder_core";

    public CinderCore(Properties properties) {
        super(properties.fireResistant());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof HearthFireBlock) {
            return InteractionResult.SUCCESS;
        }

        if (!state.is(Blocks.CAMPFIRE)) {
            return super.useOn(context);
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        CampfireBlockEntity existingEntity = level.getBlockEntity(pos) instanceof CampfireBlockEntity campfireBlockEntity
                ? campfireBlockEntity
                : null;
        Supplier<Block> hearthfireBlockSupplier = BlockRegistrar.blocks.get(HearthFireBlock.NAME);
        if (hearthfireBlockSupplier == null) {
            return InteractionResult.FAIL;
        }

        Block hearthfireBlock = hearthfireBlockSupplier.get();
        if (hearthfireBlock == null) {
            return InteractionResult.FAIL;
        }

        BlockState transformedState = HearthFireBlock.copyCampfireState(state, hearthfireBlock.defaultBlockState());
        if (!level.setBlock(pos, transformedState, UPDATE_ALL | UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS)) {
            return InteractionResult.FAIL;
        }

        CampfireBlockEntity transformedEntity = level.getBlockEntity(pos) instanceof CampfireBlockEntity campfireBlockEntity
                ? campfireBlockEntity
                : null;
        HearthFireBlock.transferCampfireData(existingEntity, transformedEntity);

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.15F);
        return InteractionResult.SUCCESS_SERVER;
    }
}
