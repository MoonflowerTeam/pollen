package gg.moonflower.pollen.api.registry.resource.forge;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.ForgeModResourcePack;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public class ResourceRegistryImpl {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<PackType, Set<PollinatedPreparableReloadListener>> LISTENERS = new HashMap<>();
    private static final Set<Pair<String, ForgeModResourcePack>> builtinResourcePacks = new HashSet<>();

    public static synchronized void registerReloadListener(PackType type, PollinatedPreparableReloadListener listener) {
        if (!LISTENERS.computeIfAbsent(type, __ -> new HashSet<>()).add(listener))
            throw new RuntimeException("Attempted to add listener twice: " + listener.getName() + "");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEvent(AddReloadListenerEvent event) {
        ResourceRegistryImpl.inject(PackType.SERVER_DATA, event.getListeners());
    }

    private static void inject(PackType type, List<PreparableReloadListener> listeners) {
        Set<PollinatedPreparableReloadListener> addedListeners = LISTENERS.get(type);
        if (addedListeners == null)
            return;

        List<PollinatedPreparableReloadListener> listenersToAdd = new ArrayList<>(addedListeners);
        Set<ResourceLocation> resolvedIds = new HashSet<>();

        for (PreparableReloadListener listener : listeners)
            if (listener instanceof PollinatedPreparableReloadListener)
                resolvedIds.add(((PollinatedPreparableReloadListener) listener).getPollenId());

        int lastSize = -1;

        while (listeners.size() != lastSize) {
            lastSize = listeners.size();

            Iterator<PollinatedPreparableReloadListener> it = listenersToAdd.iterator();

            while (it.hasNext()) {
                PollinatedPreparableReloadListener listener = it.next();

                if (resolvedIds.containsAll(listener.getPollenDependencies())) {
                    resolvedIds.add(listener.getPollenId());
                    listeners.add(listener);
                    it.remove();
                }
            }
        }

        for (PreparableReloadListener listener : listenersToAdd) {
            LOGGER.warn("Could not resolve dependencies for listener: " + listener.getName() + "!");
        }
    }

    public static void inject(PackType type, Consumer<Pack> consumer, Pack.PackConstructor factory) {
        for (Pair<String, ForgeModResourcePack> entry : builtinResourcePacks) {
            ForgeModResourcePack pack = entry.getSecond();

            if (!pack.getNamespaces(type).isEmpty()) {
                Pack profile = Pack.create(entry.getFirst(), false, entry::getSecond, factory, Pack.Position.TOP, PackSource.BUILT_IN);
                if (profile != null) {
                    consumer.accept(profile);
                }
            }
        }
    }

    public static synchronized boolean registerBuiltinResourcePack(ResourceLocation id, PollinatedModContainer container, boolean enabledByDefault) {
        String separator = container.resolve("").getFileSystem().getSeparator();
        String subPath = ("resourcepacks/" + id.getPath()).replace("/", separator);

        Path resourcePackPath = container.resolve(subPath).toAbsolutePath().normalize();

        if (!Files.exists(resourcePackPath))
            return false;

        String name = id.getNamespace() + "/" + id.getPath();
        builtinResourcePacks.add(Pair.of(name, new ForgeModResourcePack(container, resourcePackPath, null, enabledByDefault) {
            @Override
            public String getName() {
                return name; // Built-in resource pack provided by a mod, the name is overriden.
            }
        }));

        return true;
    }

    @Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusImpl {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onEvent(RegisterClientReloadListenersEvent event) {
            List<PreparableReloadListener> listeners = new ArrayList<>();
            ResourceRegistryImpl.inject(PackType.CLIENT_RESOURCES, listeners);
            for (PreparableReloadListener listener : listeners) {
                event.registerReloadListener(listener);
            }
        }
    }
}
