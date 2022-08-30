package gg.moonflower.pollen.api.levelgen.biome.fabric;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.levelgen.biome.ModifiedVanillaOverworldBuilder;
import gg.moonflower.pollen.api.levelgen.biome.PollinatedBiomeGenerator;
import gg.moonflower.pollen.api.levelgen.biome.PollinatedRegion;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class FabricRegion extends Region {

    private final PollinatedRegion parent;

    public FabricRegion(PollinatedRegion parent) {
        super(new ResourceLocation(parent.getModId(), parent.getType().getPath()), wrapType(parent.getType()), parent.getWeight());
        this.parent = parent;
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.parent.addBiomes(new PollinatedBiomeGenerator() {

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
                FabricRegion.this.addBiome(mapper, parameters, biome);
            }

            @Override
            public void addModifiedOverworldBiomes(Consumer<ModifiedVanillaOverworldBuilder> consumer) {
                FabricRegion.this.addModifiedVanillaOverworldBiomes(mapper, b -> consumer.accept(new ModifiedVanillaOverworldBuilderImpl(b)));
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
