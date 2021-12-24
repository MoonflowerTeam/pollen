package gg.moonflower.pollen.api.registry.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class EntityAttributeRegistryImpl {

    private static final Set<Consumer<EntityAttributeCreationEvent>> ATTRIBUTE_FACTORIES = new HashSet<>();

    @SubscribeEvent
    public static void onEvent(EntityAttributeCreationEvent event) {
        ATTRIBUTE_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends LivingEntity> void register(Supplier<EntityType<T>> entity, AttributeSupplier.Builder attributeBuilder) {
        ATTRIBUTE_FACTORIES.add(event -> event.put(entity.get(), attributeBuilder.build()));
    }
}
