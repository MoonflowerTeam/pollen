package gg.moonflower.pollen.core.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.entity.PollinatedBoat;
import gg.moonflower.pollen.api.entity.PollinatedBoatType;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@ApiStatus.Internal
public class PollinatedBoatRenderer extends BoatRenderer {
    private final Map<PollinatedBoatType, Pair<ResourceLocation, BoatModel>> boatResources;

    public PollinatedBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest);
        this.boatResources = PollenRegistries.BOAT_TYPE_REGISTRY
            .stream()
            .collect(
                ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(chest ? type.getChestedTexture() : type.getTexture(), this.createBoatModel(context, type, chest)))
            );
    }

    private BoatModel createBoatModel(EntityRendererProvider.Context context, PollinatedBoatType type, boolean bl) {
        ModelLayerLocation modelLayerLocation = bl ? PollinatedBoatRenderer.createChestBoatModelName(type) : PollinatedBoatRenderer.createBoatModelName(type);
        return new BoatModel(context.bakeLayer(modelLayerLocation), bl);
    }

    public static ModelLayerLocation createBoatModelName(PollinatedBoatType type) {
        ResourceLocation location =  Objects.requireNonNull(PollenRegistries.BOAT_TYPE_REGISTRY.getKey(type));
        return new ModelLayerLocation(new ResourceLocation(location.getNamespace(), "chest_boat/" + location.getPath()), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(PollinatedBoatType type) {
        ResourceLocation location =  Objects.requireNonNull(PollenRegistries.BOAT_TYPE_REGISTRY.getKey(type));
        return new ModelLayerLocation(new ResourceLocation(location.getNamespace(), "chest_boat/" + location.getPath()), "main");
    }

    public Map<PollinatedBoatType, Pair<ResourceLocation, BoatModel>> getBoatResources() {
        return boatResources;
    }

    public ResourceLocation getTextureLocation(Boat boat) {
        return this.boatResources.get(((PollinatedBoat) boat).getBoatPollenType()).getFirst();
    }
}
