package gg.moonflower.pollen.api.util.forge;

import gg.moonflower.pollen.api.registry.resource.forge.ResourceRegistryImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class ModResourcePackCreator implements RepositorySource {

    public static final PackSource RESOURCE_PACK_SOURCE = text -> Component.translatable("pack.nameAndSource", text, Component.translatable("pack.source.forgemod"));
    public static final ModResourcePackCreator CLIENT_RESOURCE_PACK_PROVIDER = new ModResourcePackCreator(PackType.CLIENT_RESOURCES);
    private final PackType type;

    public ModResourcePackCreator(PackType type) {
        this.type = type;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer, Pack.PackConstructor factory) {
        ResourceRegistryImpl.inject(this.type, consumer, factory);
    }
}
