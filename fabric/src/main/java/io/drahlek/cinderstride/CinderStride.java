package io.drahlek.cinderstride;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CinderStride implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        CinderStrideCommon.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CinderStrideCommon.registerCommands(dispatcher));
    }
}
