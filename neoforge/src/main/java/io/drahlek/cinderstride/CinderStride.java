package io.drahlek.cinderstride;

import io.drahlek.dirigo.services.NeoForgeBlockRegistrar;
import io.drahlek.dirigo.services.NeoForgeItemRegistrar;
import io.drahlek.dirigo.services.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class CinderStride {

    public CinderStride(IEventBus eventBus) {
        // Perform logic in that should be executed on both sides
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        if (Services.ITEM_REGISTRAR instanceof NeoForgeItemRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        if (Services.BLOCK_REGISTRAR instanceof NeoForgeBlockRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        CinderStrideCommon.init();
    }
}
