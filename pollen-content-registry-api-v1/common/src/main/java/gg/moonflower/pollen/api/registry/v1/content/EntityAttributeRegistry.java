package gg.moonflower.pollen.api.registry.v1.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

/**
 * @author Jackson
 * @since 1.0.0
 */
public interface EntityAttributeRegistry {

    /**
     * Registers attributes for the specified entity.
     *
     * @param entity           The entity to add attributes for
     * @param attributeBuilder The builder of attributes
     * @param <T>              The type of entity to register attributes for
     */
    @ExpectPlatform
    static <T extends LivingEntity> void register(Supplier<EntityType<T>> entity, Supplier<AttributeSupplier.Builder> attributeBuilder) {
        Platform.error();
    }
}
