package gg.moonflower.pollen.api.item.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpawnEggItemBaseImpl {

    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
    private static final Map<SpawnEggItem, Supplier<? extends EntityType<?>>> TYPES = new ConcurrentHashMap<>();

    public static void registerSpawnEgg(SpawnEggItem item, Supplier<? extends EntityType<?>> type) {
        TYPES.put(item, type);
    }

    @SubscribeEvent
    public static void onEvent(RegistryEvent.Register<EntityType<?>> event) {
        if (SPAWN_EGGS == null)
            throw new RuntimeException("Failed to inject spawns eggs");
        TYPES.forEach((spawnEggItem, supplier) -> SPAWN_EGGS.put(supplier.get(), spawnEggItem));
    }
}
