package io.drahlek.cinderstride.blocks;

import io.drahlek.dirigo.annotation.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

@Block(id="cooled_lava")
public class CooledLava extends net.minecraft.world.level.block.Block {
    public final static String NAME = "cooled_lava";
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);

    public CooledLava(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(STAGE);
    }
}
