package gg.moonflower.pollen.api.datagen.provider.loot_table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Generates block states and models for blocks and items.
 *
 * @author Ocelot
 * @since 1.5.0
 */
public class PollinatedLootTableProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final List<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>> VANILLA_PROVIDERS = ImmutableList.
            of(FishingLoot::new, ChestLoot::new, EntityLoot::new, BlockLoot::new, PiglinBarterLoot::new, GiftLoot::new);

    private final Map<LootContextParamSet, Set<PollinatedLootGenerator>> lootGenerators;
    private final DataGenerator generator;

    public PollinatedLootTableProvider(DataGenerator generator) {
        this.lootGenerators = new HashMap<>();
        this.generator = generator;
    }

    @Override
    public void run(CachedOutput cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Map<ResourceLocation, LootTable> lootTables = new HashMap<>();

        this.lootGenerators.forEach((paramSet, lootGenerators) -> {
            BiConsumer<ResourceLocation, LootTable.Builder> registry = (name, builder) -> {
                if (lootTables.put(name, builder.setParamSet(paramSet).build()) != null)
                    throw new IllegalStateException("Duplicate loot table " + name);
            };

            lootGenerators.forEach(generator -> generator.accept(registry));
        });

        Map<ResourceLocation, LootTable> registry = new HashMap<>(lootTables);
        try {
            // Add vanilla block loot for validation
            VANILLA_PROVIDERS.forEach(provider -> provider.get().accept((name, builder) -> {
                if (registry.put(name, builder.build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + name);
                }
            }));
        } catch (Throwable ignored) {
            // Don't care, didn't ask
        }

        ValidationContext validationContext = new ValidationContext(LootContextParamSets.ALL_PARAMS, resourceLocationx -> null, registry::get);
        lootTables.forEach((name, lootTable) -> LootTables.validate(validationContext, name, lootTable));
        Multimap<String, String> problems = validationContext.getProblems();
        if (!problems.isEmpty()) {
            problems.forEach((subject, description) -> LOGGER.warn("Found validation problem in " + subject + ": " + description));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }

        lootTables.forEach((name, lootTable) -> {
            Path path = createPath(outputFolder, name);

            try {
                DataProvider.saveStable(cache, LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't save loot table {}", path, e);
            }
        });
    }

    /**
     * Adds the specified generator to the provider.
     *
     * @param paramSet  The type of loot table to add
     * @param generator The generator for loot tables
     */
    public PollinatedLootTableProvider add(LootContextParamSet paramSet, PollinatedLootGenerator generator) {
        this.lootGenerators.computeIfAbsent(paramSet, __ -> new HashSet<>()).add(generator);
        return this;
    }

    private static Path createPath(Path path, ResourceLocation id) {
        return path.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "LootTables";
    }
}
