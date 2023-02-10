package gg.moonflower.pollen.api.datagen.provider.loot_table;

import com.google.common.collect.Sets;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.mixin.loot.BlockLootAccessor;
import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Generates loot tables for blocks.
 *
 * @author Ocelot
 * @since 1.4.10
 */
public abstract class PollinatedBlockLootGenerator extends BlockLoot implements PollinatedLootGenerator {

    private final String domain;

    public PollinatedBlockLootGenerator(PollinatedModContainer container) {
        this.domain = container.getId();
    }

    /**
     * Registers all block loot tables.
     */
    protected abstract void run();

    /**
     * @return All blocks for this mod for verification
     */
    protected Collection<Block> blocks() {
        return Registry.BLOCK.keySet().stream().filter(name -> name.getNamespace().equals(this.domain)).map(Registry.BLOCK::get).collect(Collectors.toSet());
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> registry) {
        this.run();

        Map<ResourceLocation, LootTable.Builder> map = ((BlockLootAccessor) this).getMap();
        Set<ResourceLocation> set = Sets.newHashSet();

        Collection<Block> blocks = this.blocks();
        for (Block block : blocks) {
            ResourceLocation resourceLocation = block.getLootTable();
            if (resourceLocation != BuiltInLootTables.EMPTY && set.add(resourceLocation)) {
                LootTable.Builder builder = map.remove(resourceLocation);
                if (builder == null)
                    throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourceLocation, Registry.BLOCK.getKey(block)));
                registry.accept(resourceLocation, builder);
            }
        }

        if (!map.isEmpty())
            throw new IllegalStateException("Created block loot tables for non-blocks: " + map.keySet());
    }
}
