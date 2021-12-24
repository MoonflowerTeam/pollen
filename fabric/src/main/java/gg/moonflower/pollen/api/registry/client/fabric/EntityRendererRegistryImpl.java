package gg.moonflower.pollen.api.registry.client.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EntityRendererRegistryImpl {
    public static <T extends Entity> void register(EntityType<T> type, EntityRendererProvider<T> factory) {
        EntityRendererRegistry.register(type, factory);
    }
}
