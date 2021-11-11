package gg.moonflower.pollen.api.registry.forge;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.ForgeModResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

@ApiStatus.Internal
public class ResourceRegistryImpl {

    private static final Map<PackType, Set<PreparableReloadListener>> LISTENERS = new HashMap<>();
    private static final Set<Pair<String, ForgeModResourcePack>> builtinResourcePacks = new HashSet<>();

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

    public static boolean registerBuiltinResourcePack(ResourceLocation id, PollinatedModContainer container, boolean enabledByDefault) {
        String separator = container.getRootPath().getFileSystem().getSeparator();
        String subPath = ("resourcepacks/" + id.getPath()).replace("/", separator);

        Path resourcePackPath = container.getRootPath().resolve(subPath).toAbsolutePath().normalize();

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
}
