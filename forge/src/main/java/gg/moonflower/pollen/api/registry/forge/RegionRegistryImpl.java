package gg.moonflower.pollen.api.registry.forge;

import gg.moonflower.pollen.api.levelgen.biome.PollinatedRegion;
import gg.moonflower.pollen.api.levelgen.biome.forge.ForgeRegion;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import terrablender.api.Regions;

@ApiStatus.Internal
public class RegionRegistryImpl {

    public static void register(String modId, PollinatedRegion region) {
        Regions.register(new ForgeRegion(new ResourceLocation(modId, region.getType().getPath()), region));
    }

    public static void register(ResourceLocation name, PollinatedRegion region) {
        Regions.register(new ForgeRegion(name, region));
    }
}
