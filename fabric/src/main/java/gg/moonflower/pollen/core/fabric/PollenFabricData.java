package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.core.Pollen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabricData implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        Pollen.PLATFORM.dataSetup(dataGenerator);
    }
}
