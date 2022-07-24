package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.datagen.SoundDefinitionBuilder;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedSoundProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final String domain;

    public PollinatedSoundProvider(DataGenerator generator, PollinatedModContainer container) {
        this.generator = generator;
        this.domain = container.getId();
    }

    /**
     * Registers all sound instances to be generated.
     *
     * @param registry The registry to validate and create files
     */
    protected abstract void registerSounds(Consumer<SoundDefinitionBuilder> registry);

    @Override
    public void run(CachedOutput output) {
        Path path = this.generator.getOutputFolder().resolve("assets/" + this.domain + "/sounds.json");
        Set<SoundDefinitionBuilder> sounds = new HashSet<>();
        Consumer<SoundDefinitionBuilder> registry = sound ->
        {
            if (!sounds.add(sound))
                throw new IllegalStateException("Duplicate sound " + sound.getSoundId());
        };

        this.registerSounds(registry);

        JsonObject json = new JsonObject();
        sounds.stream().sorted(Comparator.comparing(SoundDefinitionBuilder::getSoundId)).forEachOrdered(definition -> json.add(definition.getSoundId(), definition.toJson()));

        try {
            DataProvider.saveStable(output, json, path);
        } catch (IOException e) {
            LOGGER.error("Couldn't save {}", path, e);
        }
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}
