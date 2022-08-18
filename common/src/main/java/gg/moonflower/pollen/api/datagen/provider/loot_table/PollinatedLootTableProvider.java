package gg.moonflower.pollen.api.datagen.provider.loot_table;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Generates block states and models for blocks and items.
 *
 * @author Ocelot
 * @since 1.4.10
 */
public class PollinatedLootTableProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<LootContextParamSet, Set<PollinatedLootGenerator>> lootGenerators;
    private final DataGenerator generator;

    public PollinatedLootTableProvider(DataGenerator generator) {
        this.lootGenerators = new HashMap<>();
        this.generator = generator;
    }

    @Override
    public void run(HashCache cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Map<ResourceLocation, LootTable> lootTables = new HashMap<>();

        this.lootGenerators.forEach((paramSet, lootGenerators) -> {
            BiConsumer<ResourceLocation, LootTable.Builder> registry = (name, builder) -> {
                if (lootTables.put(name, builder.setParamSet(paramSet).build()) != null)
                    throw new IllegalStateException("Duplicate loot table " + name);
            };

            lootGenerators.forEach(generator -> generator.run(registry));
        });

        ValidationContext validationContext = new ValidationContext(LootContextParamSets.ALL_PARAMS, resourceLocationx -> null, lootTables::get);
        lootTables.forEach((name, lootTable) -> LootTables.validate(validationContext, name, lootTable));
        Multimap<String, String> problems = validationContext.getProblems();
        if (!problems.isEmpty()) {
            problems.forEach((subject, description) -> LOGGER.warn("Found validation problem in " + subject + ": " + description));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }

        lootTables.forEach((name, lootTable) -> {
            Path path = createPath(outputFolder, name);

            try {
                DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
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
