package gg.moonflower.pollen.api.levelgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

/**
 * Allows for modification of vanilla overworld biomes and parameters. A wrapper for TerraBlender's modified overworld builder.
 * <p>Only valid for regions that are {@link PollinatedRegion.Type#OVERWORLD}.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ModifiedVanillaOverworldBuilder.java">ModifiedVanillaOverworldBuilder</a>
 * @since 1.5.0
 */
public interface ModifiedVanillaOverworldBuilder {

    /**
     * Replaces a vanilla biome with the specified replacement.
     *
     * @param original    The biome that should be replaced
     * @param replacement The new biome that should replace the old one
     */
    void replaceBiome(ResourceKey<Biome> original, ResourceKey<Biome> replacement);

    /**
     * Replaces vanilla biomes at the given {@link Climate.ParameterPoint} with the specified replacement.
     *
     * @param parameterPoint The parameter point to replace
     * @param replacement    The new biome to replace any corresponding biomes with
     */
    void replaceBiome(Climate.ParameterPoint parameterPoint, ResourceKey<Biome> replacement);

    /**
     * Replaces the given {@link Climate.ParameterPoint} with the specified replacement.
     *
     * @param original    The parameter point to replace
     * @param replacement The new parameter point that should replace the old one
     */
    void replaceParameter(Climate.ParameterPoint original, Climate.ParameterPoint replacement);
}
