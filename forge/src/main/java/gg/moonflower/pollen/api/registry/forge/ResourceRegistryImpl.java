package gg.moonflower.pollen.api.registry.forge;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class ResourceRegistryImpl {

    private static final Map<PackType, Set<PreparableReloadListener>> LISTENERS = new HashMap<>();

    public static void registerReloadListener(PackType type, PreparableReloadListener listener) {
        if (!LISTENERS.computeIfAbsent(type, __ -> new HashSet<>()).add(listener))
            throw new RuntimeException("Attempted to add listener twice: " + listener.getName() + "");
    }

    public static void inject(PackType type, List<PreparableReloadListener> listeners) {
        Set<PreparableReloadListener> addedListeners = LISTENERS.get(type);
        if (addedListeners == null)
            return;

        listeners.removeAll(addedListeners);
        listeners.addAll(addedListeners);
    }
}
