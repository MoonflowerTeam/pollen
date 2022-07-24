package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedAdvancementProvider extends SimpleConditionalDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public PollinatedAdvancementProvider(DataGenerator generator) {
        this.generator = generator;
    }

    /**
     * Registers all advancements to be generated.
     *
     * @param registry The registry to validate and create files
     */
    protected abstract void registerAdvancements(Consumer<Advancement> registry);

    @Override
    public void run(CachedOutput output) {
        Path folder = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        Consumer<Advancement> registry = advancement ->
        {
            if (!set.add(advancement.getId()))
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());

            Path path = createPath(folder, advancement);

            JsonObject json = advancement.deconstruct().serializeToJson();
            this.injectConditions(advancement.getId(), json);

            try {
                DataProvider.saveStable(output, json, path);
            } catch (IOException e) {
                LOGGER.error("Couldn't save advancement {}", path, e);
            }
        };

        this.registerAdvancements(registry);
    }

    private static Path createPath(Path path, Advancement advancement) {
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Advancements";
    }
}
