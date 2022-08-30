package gg.moonflower.pollen.api.levelgen.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.levelgen.biome.parameters.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.function.Consumer;

/**
 * Context for regions to add and modify biome generation.
 *
 * @author ebo2022
 * @since 1.5.0
 */
public interface BiomePlacementContext {

    /**
     * @return The biome registry
     */
    Registry<Biome> getRegistry();

    /**
     * @return The mapper used to construct a list of {@link Climate.ParameterPoint} to biome mappings
     */
    Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> getMapper();

    /**
     * Adds a biome to the generation.
     *
     * @param parameters The parameter point corresponding to the added biome
     * @param biome      The biome to add
     */
    void addBiome(Climate.ParameterPoint parameters, ResourceKey<Biome> biome);

    /**
     * Adds a biome to the generation.
     *
     * @param temperature     The temperature value
     * @param humidity        The humidity value
     * @param continentalness The continentalness value
     * @param erosion         The erosion value
     * @param weirdness       The weirdness value
     * @param depth           The depth value
     * @param offset          The offset value
     * @param biome           The biome to add
     */
    default void addBiome(Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, Climate.Parameter depth, float offset, ResourceKey<Biome> biome) {
        this.addBiome(Climate.parameters(temperature, humidity, continentalness, erosion, depth, weirdness, offset), biome);
    }

    /**
     * Adds a biome to the generation.
     *
     * @param temperature     The temperature value
     * @param humidity        The humidity value
     * @param continentalness The continentalness value
     * @param erosion         The erosion value
     * @param weirdness       The weirdness value
     * @param depth           The depth value
     * @param offset          The offset value
     * @param biome           The biome to add
     */
    default void addBiome(Temperature temperature, Humidity humidity, Continentalness continentalness, Erosion erosion, Weirdness weirdness, Depth depth, float offset, ResourceKey<Biome> biome) {
        this.addBiome(Climate.parameters(temperature.parameter(), humidity.parameter(), continentalness.parameter(), erosion.parameter(), weirdness.parameter(), depth.parameter(), offset), biome);
    }

    /**
     * Adds a biome using climate parameters similar to those of the specified vanilla biome.
     *
     * @param similarBiome The similar vanilla biome
     * @param biome        The biome to add
     */
    default void addBiomeSimilar(ResourceKey<Biome> similarBiome, ResourceKey<Biome> biome) {
        List<Climate.ParameterPoint> points = ParameterPoints.getVanilla(similarBiome).stream().collect(ImmutableList.toImmutableList());
        points.forEach(point -> this.addBiome(point, biome));
    }

    /**
     * Adds all vanilla overworld biomes with any modifications made.
     *
     * @param consumer A consumer to modify overworld biome parameters
     */
    void addModifiedOverworldBiomes(Consumer<ModifiedVanillaOverworldBuilder> consumer);
}
