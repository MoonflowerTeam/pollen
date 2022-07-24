package gg.moonflower.pollen.api.datagen.provider.model;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Generates models for items and blocks, as well as block states. Generators can be added using {@link PollinatedModelGenerator}.
 *
 * @author Ocelot
 * @see PollinatedModelGenerator
 * @see PollinatedBlockModelGenerator
 * @see PollinatedItemModelGenerator
 * @since 1.0.0
 */
public class PollinatedModelProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Set<ModelGeneratorFactory> factories;
    private final DataGenerator generator;
    private final String domain;

    public PollinatedModelProvider(DataGenerator generator, PollinatedModContainer container) {
        this.factories = new HashSet<>();
        this.generator = generator;
        this.domain = container.getId();
    }

    /**
     * Adds a generator for creating models and block states.
     *
     * @param factory The factory to the provider
     */
    public PollinatedModelProvider addGenerator(ModelGeneratorFactory factory) {
        this.factories.add(factory);
        return this;
    }

    @Override
    public void run(CachedOutput output) {
        Path path = this.generator.getOutputFolder();

        Map<Block, BlockStateGenerator> blockStates = new HashMap<>();
        Consumer<BlockStateGenerator> blockStateOutput = blockStateGenerator -> {
            Block block = blockStateGenerator.getBlock();
            BlockStateGenerator blockState = blockStates.put(block, blockStateGenerator);
            if (blockState != null)
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
        };
        Map<ResourceLocation, Supplier<JsonElement>> models = Maps.newHashMap();
        Set<Item> skippedAutoModels = new HashSet<>();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (resourceLocation, supplier) -> {
            Supplier<JsonElement> model = models.put(resourceLocation, supplier);
            if (model != null)
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
        };
        Consumer<Item> skippedAutoModelsOutput = skippedAutoModels::add;

        this.factories.stream().map(factory -> factory.create(blockStateOutput, modelOutput, skippedAutoModelsOutput)).forEach(PollinatedModelGenerator::run);

        Registry.BLOCK.forEach(block -> {
            if (!this.domain.equals(Registry.BLOCK.getKey(block).getNamespace()))
                return;

            Item item = Item.BY_BLOCK.get(block);
            if (item != null) {
                if (skippedAutoModels.contains(item))
                    return;

                ResourceLocation itemLocation = ModelLocationUtils.getModelLocation(item);
                if (!models.containsKey(itemLocation))
                    models.put(itemLocation, new DelegatedModel(ModelLocationUtils.getModelLocation(block)));
            }
        });
        this.saveCollection(output, path, blockStates, PollinatedModelProvider::createBlockStatePath);
        this.saveCollection(output, path, models, PollinatedModelProvider::createModelPath);
    }

    private <T> void saveCollection(CachedOutput output, Path rootPath, Map<T, ? extends Supplier<JsonElement>> objectToJsonMap, BiFunction<Path, T, Path> resolveObjectPath) {
        objectToJsonMap.forEach((object, supplier) -> {
            Path path = resolveObjectPath.apply(rootPath, object);

            try {
                DataProvider.saveStable(output, supplier.get(), path);
            } catch (Exception var7) {
                LOGGER.error("Couldn't save {}", path, var7);
            }
        });
    }

    private static Path createBlockStatePath(Path rootPath, Block block) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(block);
        return rootPath.resolve("assets/" + resourceLocation.getNamespace() + "/blockstates/" + resourceLocation.getPath() + ".json");
    }

    private static Path createModelPath(Path rootPath, ResourceLocation modelLocation) {
        return rootPath.resolve("assets/" + modelLocation.getNamespace() + "/models/" + modelLocation.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Block State Definitions";
    }

    /**
     * Factory for creating new generators for models and block states.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ModelGeneratorFactory {
        PollinatedModelGenerator create(Consumer<BlockStateGenerator> blockStateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, Consumer<Item> skippedAutoModelsOutput);
    }
}
