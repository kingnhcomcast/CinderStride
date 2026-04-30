package io.drahlek.cinderstride.blocks;

import io.drahlek.cinderstride.config.CinderStrideConfig;
import io.drahlek.dirigo.annotation.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

@Block(id="cooled_lava")
public class CooledLava extends net.minecraft.world.level.block.Block {
    public final static String NAME = "cooled_lava";
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);

    public CooledLava(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_BLACK)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(1.25F, 4.2F)
                .lightLevel(state -> Math.round(state.getValue(STAGE) * (15f / 3f)))
                .sound(SoundType.BASALT));
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this)) {
            level.scheduleTick(pos, this, getDecayDelay(level));
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0F,  0.9F + level.getRandom().nextFloat() * 0.2F);
        }

    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) {
            if (!state.is(this)) {
                return;
            }

            int stage = state.getValue(STAGE);
            playSound(level, pos, stage);
            if (stage >= 3) {
                level.setBlock(pos, Blocks.LAVA.defaultBlockState(), UPDATE_ALL);
                return;
            }

            level.setBlock(pos, state.setValue(STAGE, stage + 1), UPDATE_ALL);
            level.scheduleTick(pos, this, getDecayDelay(level));
        }
    }

    private static void playSound(ServerLevel level, BlockPos pos, int stage) {
        switch (stage) {
            case 0:
                level.playSound(null, pos, SoundEvents.BASALT_HIT,
                        SoundSource.BLOCKS, 0.45f,
                        0.75F + level.getRandom().nextFloat() * 0.15F);
                break;

            case 1:
                level.playSound(null, pos, SoundEvents.BASALT_BREAK,
                        SoundSource.BLOCKS, 0.65f,
                        0.85F + level.getRandom().nextFloat() * 0.15F);
                break;

            case 2:
                level.playSound(null, pos, SoundEvents.NETHER_BRICKS_HIT,
                        SoundSource.BLOCKS, 0.85f,
                        0.95F + level.getRandom().nextFloat() * 0.25F);
                break;

            case 3:
                level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE,
                        SoundSource.BLOCKS, 1.1f,
                        0.85F + level.getRandom().nextFloat() * 0.15F);
                break;
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), UPDATE_ALL);
        }
    }

    //TODO get player to call player.getRandom
    private static int getDecayDelay(Level level) {
        long decayTicks = CinderStrideConfig.data().getDecayTicks();
        int variance = Math.toIntExact(decayTicks / 2L);
        long offset = variance > 0L
                ? level.getRandom().nextInt(-variance, variance + 1)
                : 0L;
        long randomizedDecayTicks = decayTicks + offset;
        return (int) Math.clamp(randomizedDecayTicks, 1L, Integer.MAX_VALUE);
    }
}
