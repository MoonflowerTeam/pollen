package gg.moonflower.pollen.api.util.fabric;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Optional;

@ApiStatus.Internal
public class PollinatedModContainerImpl implements PollinatedModContainer {

    private final ModContainer parent;

    public PollinatedModContainerImpl(ModContainer parent) {
        this.parent = parent;
    }

    public static Optional<PollinatedModContainerImpl> get(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).map(PollinatedModContainerImpl::new);
    }

    @Override
    public String getBrand() {
        return "Fabric Mod";
    }

    @Override
    public Path getRootPath() {
        return this.parent.getRootPath();
    }

    @Override
    public String getId() {
        return this.parent.getMetadata().getId();
    }

    @Override
    public String getName() {
        return this.parent.getMetadata().getName();
    }

    public ModContainer getFabricContainer() {
        return parent;
    }
}
