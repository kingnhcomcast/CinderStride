package io.drahlek.cinderstride;

import io.drahlek.dirigo.registrars.ItemRegistrar;
import io.drahlek.dirigo.services.FabricItemRegistrar;
import net.fabricmc.api.ModInitializer;

public class CinderStride implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        CommonClass.init();
        ItemRegistrar.registerItems(new FabricItemRegistrar(), Constants.MOD_ID, "io.drahlek.cinderstride");
    }
}
