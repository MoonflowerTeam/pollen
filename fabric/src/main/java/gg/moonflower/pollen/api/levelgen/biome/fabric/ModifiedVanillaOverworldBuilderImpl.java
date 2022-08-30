package gg.moonflower.pollen.api.levelgen.biome.fabric;

import gg.moonflower.pollen.api.levelgen.biome.ModifiedVanillaOverworldBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ModifiedVanillaOverworldBuilderImpl implements ModifiedVanillaOverworldBuilder {

    private final terrablender.api.ModifiedVanillaOverworldBuilder parent;

    public ModifiedVanillaOverworldBuilderImpl(terrablender.api.ModifiedVanillaOverworldBuilder parent) {
        this.parent = parent;
    }

    @Override
    public void replaceBiome(ResourceKey<Biome> original, ResourceKey<Biome> replacement) {
        this.parent.replaceBiome(original, replacement);
    }

    @Override
    public void replaceBiome(Climate.ParameterPoint parameterPoint, ResourceKey<Biome> replacement) {
        this.parent.replaceBiome(parameterPoint, replacement);
    }

    @Override
    public void replaceParameter(Climate.ParameterPoint original, Climate.ParameterPoint replacement) {
        this.parent.replaceParameter(original, replacement);
    }
}
