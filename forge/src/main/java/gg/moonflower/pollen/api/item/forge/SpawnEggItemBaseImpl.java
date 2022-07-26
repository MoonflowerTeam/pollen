package gg.moonflower.pollen.api.item.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpawnEggItemBaseImpl {

    private static final Map<SpawnEggItem, Supplier<? extends EntityType<?>>> TYPES = new ConcurrentHashMap<>();

    public static void registerSpawnEgg(SpawnEggItem item, Supplier<? extends EntityType<?>> type) {
        TYPES.put(item, type);
    }

    @SubscribeEvent
    public static void onEvent(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registry.ENTITY_TYPE_REGISTRY))
            return;

        Map<EntityType<?>, SpawnEggItem> spawnEggs = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "f_43201_");
        if (spawnEggs == null)
            throw new RuntimeException("Failed to inject spawns eggs");
        TYPES.forEach((spawnEggItem, supplier) -> spawnEggs.put(supplier.get(), spawnEggItem));
        TYPES.clear();
    }
}
