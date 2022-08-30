package gg.moonflower.pollen.api.registry.fabric;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.levelgen.biome.PollinatedRegion;
import gg.moonflower.pollen.api.levelgen.biome.fabric.FabricRegion;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import terrablender.api.Regions;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class RegionRegistryImpl {

    private static final List<Pair<ResourceLocation, PollinatedRegion>> REGIONS = new ArrayList<>();

    public static void register(String modId, PollinatedRegion region) {
        REGIONS.add(Pair.of(new ResourceLocation(modId, region.getType().getPath()), region));
    }

    public static void register(ResourceLocation name, PollinatedRegion region) {
        REGIONS.add(Pair.of(name, region));
    }

    public static void init() {
        REGIONS.forEach(pair -> Regions.register(new FabricRegion(pair.getFirst(), pair.getSecond())));
    }
}
