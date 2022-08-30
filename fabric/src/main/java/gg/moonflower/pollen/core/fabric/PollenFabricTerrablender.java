package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.registry.fabric.RegionRegistryImpl;
import org.jetbrains.annotations.ApiStatus;
import terrablender.api.TerraBlenderApi;

@ApiStatus.Internal
public class PollenFabricTerrablender implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {
        RegionRegistryImpl.init();
    }
}
