package gg.moonflower.pollen.api.levelgen.biome.forge;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.levelgen.biome.ModifiedVanillaOverworldBuilder;
import gg.moonflower.pollen.api.levelgen.biome.BiomePlacementContext;
import gg.moonflower.pollen.api.levelgen.biome.PollinatedRegion;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.ApiStatus;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

@ApiStatus.Internal
public class ForgeRegion extends Region {

    private final PollinatedRegion parent;

    public ForgeRegion(ResourceLocation location, PollinatedRegion parent) {
        super(location, wrapType(parent.getType()), parent.getWeight());
        this.parent = parent;
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.parent.addBiomes(new BiomePlacementContext() {

            @Override
            public Registry<Biome> getRegistry() {
                return registry;
            }

            @Override
            public Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> getMapper() {
                return mapper;
            }

            @Override
            public void addBiome(Climate.ParameterPoint parameters, ResourceKey<Biome> biome) {
                ForgeRegion.this.addBiome(mapper, parameters, biome);
            }

            @Override
            public void addModifiedOverworldBiomes(Consumer<ModifiedVanillaOverworldBuilder> consumer) {
                ForgeRegion.this.addModifiedVanillaOverworldBiomes(mapper, b -> consumer.accept(new ModifiedVanillaOverworldBuilderImpl(b)));
            }
        });
    }

    private static RegionType wrapType(PollinatedRegion.Type type) {
        return switch (type) {
            case OVERWORLD -> RegionType.OVERWORLD;
            case NETHER -> RegionType.NETHER;
        };
    }
}
