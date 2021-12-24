package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class EntityAttributeRegistry {

    @ExpectPlatform
    public static void register(Supplier<EntityType<? extends LivingEntity>> entity, AttributeSupplier.Builder attributeBuilder) {
        Platform.error();
    }
}
