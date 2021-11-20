package gg.moonflower.pollen.core.fabric;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import gg.moonflower.pollen.api.sync.fabric.FabricDataComponent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("UnstableApiUsage")
public class PollenComponents implements EntityComponentInitializer {

    public static final ComponentKey<FabricDataComponent> SYNCED_DATA = ComponentRegistry.getOrCreate(new ResourceLocation(Pollen.MOD_ID, "synced_data"), FabricDataComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SYNCED_DATA, FabricDataComponent::new);
    }
}
