package io.drahlek.cinderstride;

import io.drahlek.cinderstride.blocks.HearthFireBlock;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class CinderStrideClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);

        BlockRenderLayerMap.INSTANCE.putBlock(
                BlockRegistrar.blocks.get(HearthFireBlock.NAME).get(),
                RenderType.cutout());
     }
}
