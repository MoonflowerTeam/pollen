package gg.moonflower.pollen.api.entity;

import gg.moonflower.pollen.api.registry.PollinatedEntityRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Built-in Pollen entity types and implementations.
 *
 * @author Ocelot
 * @since 1.4.0
 */
public final class PollenEntityTypes {

    @ApiStatus.Internal
    public static final PollinatedEntityRegistry ENTITY_TYPES = PollinatedRegistry.createEntity(Pollen.MOD_ID);

    public static final Supplier<EntityType<Boat>> BOAT = ENTITY_TYPES.register("boat", () -> EntityType.Builder.<Boat>of(PollinatedBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("pollen:boat"));
    public static final Supplier<EntityType<Boat>> CHEST_BOAT = ENTITY_TYPES.register("chest_boat", () -> EntityType.Builder.<Boat>of(PollinatedChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("pollen:chest_boat"));

    private PollenEntityTypes() {
    }
}
