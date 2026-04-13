package io.drahlek.cinderstride.blocks;

import io.drahlek.cinderstride.config.CinderStrideConfig;
import io.drahlek.dirigo.annotation.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.is(this)) {
            return;
        }

        int stage = state.getValue(STAGE);
        if (stage >= 3) {
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), UPDATE_ALL);
            return;
        }

        level.setBlock(pos, state.setValue(STAGE, stage + 1), UPDATE_ALL);
        level.scheduleTick(pos, this, getDecayDelay(level));
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
