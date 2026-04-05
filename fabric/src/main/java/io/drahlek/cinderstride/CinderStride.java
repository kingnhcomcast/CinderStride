package io.drahlek.cinderstride;

import net.fabricmc.api.ModInitializer;

public class CinderStride implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        DirigoCommon.init();
    }
}
