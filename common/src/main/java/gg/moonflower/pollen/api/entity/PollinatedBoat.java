package gg.moonflower.pollen.api.entity;

import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.item.PollinatedBoatItem;
import gg.moonflower.pollen.api.util.NbtConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ocelot
 * @since 1.4.0
 */
public class PollinatedBoat extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(PollinatedBoat.class, EntityDataSerializers.INT);

    public PollinatedBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public PollinatedBoat(Level level, double d, double e, double f) {
        this(PollenEntityTypes.BOAT.get(), level);
        this.setPos(d, e, f);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = d;
        this.yo = e;
        this.zo = f;
    }

    @Override
    public void tick() {
        super.tick();

        // Transform into an oak boat if the type doesn't exist
        if (this.getBoatPollenType() == null) {
            Boat boat = new Boat(this.level, this.getX(), this.getY(), this.getZ());
            boat.copyPosition(this);
            if (this.hasCustomName()) {
                boat.setCustomName(this.getCustomName());
                boat.setCustomNameVisible(this.isCustomNameVisible());
            }

            boat.setInvulnerable(this.isInvulnerable());

            this.level.addFreshEntity(boat);
            if (this.isPassenger())
                boat.startRiding(this.getVehicle(), true);

            for (Entity passenger : this.getPassengers())
                passenger.startRiding(boat, true);

            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public Item getDropItem() {
        PollinatedBoatType type = this.getBoatPollenType();
        return type != null ? PollinatedBoatItem.getBoatItem(type, false) : null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, -1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        ResourceLocation id = PollenRegistries.BOAT_TYPE_REGISTRY.getKey(this.getBoatPollenType());
        if (id != null)
            compound.putString("Type", id.toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Type", NbtConstants.STRING))
            this.setPollenType(PollenRegistries.BOAT_TYPE_REGISTRY.get(new ResourceLocation(compound.getString("Type"))));
    }

    @Override
    public void setType(Boat.Type boatType) {
    }

    @Override
    public Boat.Type getBoatType() {
        return Type.OAK;
    }

    public void setPollenType(@Nullable PollinatedBoatType boatType) {
        this.entityData.set(DATA_ID_TYPE, boatType == null ? -1 : PollenRegistries.BOAT_TYPE_REGISTRY.getId(boatType));
    }

    public PollinatedBoatType getBoatPollenType() {
        int id = this.entityData.get(DATA_ID_TYPE);
        return id == -1 ? null : PollenRegistries.BOAT_TYPE_REGISTRY.byId(id);
    }
}
