package io.drahlek.cinderstride.loot;

import io.drahlek.cinderstride.Constants;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

public class LootRegistrar {
    private static final ResourceKey<LootTable> BASTION_NON_TREASURE_SHARDS =
            injectTable("bastion_non_treasure_shards");
    private static final ResourceKey<LootTable> BASTION_TREASURE_SHARDS =
            injectTable("bastion_treasure_shards");
    private static final ResourceKey<LootTable> BASTION_TREASURE_TEMPLATE =
            injectTable("bastion_treasure_template");


    public static void registerLootInjection() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            if (BuiltInLootTables.BASTION_TREASURE.equals(key)) {
                tableBuilder.withPool(LootPool.lootPool().add(NestedLootTable.lootTableReference(BASTION_TREASURE_SHARDS)));
                tableBuilder.withPool(LootPool.lootPool().add(NestedLootTable.lootTableReference(BASTION_TREASURE_TEMPLATE)));
                return;
            }

            if (BuiltInLootTables.BASTION_BRIDGE.equals(key) ||
                    BuiltInLootTables.BASTION_HOGLIN_STABLE.equals(key) ||
                    BuiltInLootTables.BASTION_OTHER.equals(key)) {
                tableBuilder.withPool(LootPool.lootPool().add(NestedLootTable.lootTableReference(BASTION_NON_TREASURE_SHARDS)));
            }
        });
    }

    private static ResourceKey<LootTable> injectTable(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "inject/" + name));
    }
}
