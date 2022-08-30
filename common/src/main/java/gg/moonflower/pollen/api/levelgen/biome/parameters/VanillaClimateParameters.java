package gg.moonflower.pollen.api.levelgen.biome.parameters;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for getting parameter points from biomes.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/worldgen/RegionUtils.java">RegionUtils</a>
 * @since 1.5.0
 */
public final class VanillaClimateParameters {

    private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> VANILLA_POINTS;
    private static final Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> PARAMETER_POINT_CACHE = new HashMap<>();

    /**
     * Gets {@link Climate.ParameterPoint}s for the given vanilla biome.
     *
     * @param biome The biome to get parameter points for
     * @return A list of parameter points corresponding to the given biomes
     */
    public static List<Climate.ParameterPoint> get(ResourceKey<Biome> biome) {
        if (PARAMETER_POINT_CACHE.containsKey(biome))
            return PARAMETER_POINT_CACHE.get(biome);
        List<Climate.ParameterPoint> points = VANILLA_POINTS.stream().filter(pair -> pair.getSecond() == biome).map(Pair::getFirst).collect(ImmutableList.toImmutableList());
        PARAMETER_POINT_CACHE.put(biome, points);
        return points;
    }

    static {
        ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = new ImmutableList.Builder<>();
        (new OverworldBiomeBuilder()).addBiomes(builder::add);
        VANILLA_POINTS = builder.build();
    }
}
