package gg.moonflower.pollen.core.client.render.entity;

import gg.moonflower.pollen.api.entity.PollinatedBoat;
import gg.moonflower.pollen.api.registry.client.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedBoatRenderer extends BoatRenderer {

    public PollinatedBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        return entity instanceof PollinatedBoat ? ((PollinatedBoat) entity).getBoatPollenType().getTexture() : super.getTextureLocation(entity);
    }
}
