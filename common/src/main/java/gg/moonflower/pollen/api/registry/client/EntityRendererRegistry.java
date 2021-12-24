package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class EntityRendererRegistry {

    private EntityRendererRegistry() {
    }

    @ExpectPlatform
    public static <T extends Entity> void register(EntityType<T> type, EntityRendererProvider<T> factory) {
        Platform.error();
    }
}
