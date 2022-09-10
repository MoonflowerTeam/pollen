package gg.moonflower.pollen.impl;

import gg.moonflower.pollen.api.registry.v1.PollinatedEntityRegistry;
import gg.moonflower.pollen.api.registry.v1.PollinatedRegistry;
import gg.moonflower.pollen.impl.entity.PollinatedBoat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public final class PollenEntityTypes {

    public static final PollinatedEntityRegistry ENTITY_TYPES = PollinatedRegistry.createEntity(PollenRegistryApiInitializer.MOD_ID);

    public static final Supplier<EntityType<Boat>> BOAT = ENTITY_TYPES.register("boat", () -> EntityType.Builder.<Boat>of(PollinatedBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("pollen:boat"));

    private PollenEntityTypes() {
    }
}
