package io.drahlek.cinderstride.client;

import io.drahlek.cinderstride.Constants;
import io.drahlek.cinderstride.blocks.HearthFireBlock;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import static io.drahlek.cinderstride.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.CLIENT)
public final class CinderStrideClient {
    public CinderStrideClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);

        // Register client-only events
        modBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    BlockRegistrar.blocks.get(HearthFireBlock.NAME).get(),
                    RenderType.cutout()
            );
        });
    }
}
