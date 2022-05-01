package gg.moonflower.pollen.core.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.entity.PollinatedBoat;
import gg.moonflower.pollen.api.entity.PollinatedBoatType;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;

@ApiStatus.Internal
public class PollinatedBoatRenderer extends BoatRenderer {
    private final Map<PollinatedBoatType, Pair<ResourceLocation, BoatModel>> boatResources;

    public PollinatedBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.boatResources = PollenRegistries.BOAT_TYPE_REGISTRY.stream().collect(ImmutableMap.toImmutableMap((type) -> type, (type) -> Pair.of(type.getTexture(), new BoatModel(context.bakeLayer(createBoatModelName(type))))));
    }

    public static ModelLayerLocation createBoatModelName(PollinatedBoatType type) {
        return new ModelLayerLocation(Objects.requireNonNull(PollenRegistries.BOAT_TYPE_REGISTRY.getKey(type)), "main");
    }

    public Map<PollinatedBoatType, Pair<ResourceLocation, BoatModel>> getBoatResources() {
        return boatResources;
    }

    public ResourceLocation getTextureLocation(Boat boat) {
        return this.boatResources.get(((PollinatedBoat) boat).getBoatPollenType()).getFirst();
    }
}
