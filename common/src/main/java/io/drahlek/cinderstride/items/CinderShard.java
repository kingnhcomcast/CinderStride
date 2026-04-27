package io.drahlek.cinderstride.items;

import io.drahlek.dirigo.annotation.Item;

@Item(id = "cinder_shard")
public class CinderShard extends net.minecraft.world.item.Item {
    static public final String NAME = "cinder_shard";

    public CinderShard(Properties properties) {
        super(properties.fireResistant());
    }
}
