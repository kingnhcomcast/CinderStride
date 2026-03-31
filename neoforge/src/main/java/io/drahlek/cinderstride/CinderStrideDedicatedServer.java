package io.drahlek.cinderstride;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static io.drahlek.cinderstride.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.DEDICATED_SERVER)
public class CinderStrideDedicatedServer {
    public CinderStrideDedicatedServer(IEventBus modBus) {
        Constants.LOG.info("{} DedicatedServer Initialize", Constants.MOD_ID);
    }
}