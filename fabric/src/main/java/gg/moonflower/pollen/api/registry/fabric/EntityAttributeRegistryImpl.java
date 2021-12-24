package gg.moonflower.pollen.api.registry.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class EntityAttributeRegistryImpl {

    public static <T extends LivingEntity> void register(Supplier<EntityType<T>> entity, Supplier<AttributeSupplier.Builder> attributeBuilder) {
        FabricDefaultAttributeRegistry.register(entity.get(), attributeBuilder.get());
    }
}
