package gg.moonflower.pollen.api.util.forge;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
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
        return ModList.get().getModContainerById(modId).map(PollinatedModContainerImpl::new);
    }

    @Override
    public String getBrand() {
        return "Forge";
    }

    @Override
    public Path resolve(String path) {
        return ((ModFileInfo) this.parent.getModInfo().getOwningFile()).getFile().findResource(path);
    }

    @Override
    public String getId() {
        return this.parent.getModId();
    }

    @Override
    public String getName() {
        return this.parent.getModInfo().getDisplayName();
    }

    @Override
    public String getVersion() {
        return this.parent.getModInfo().getVersion().getQualifier();
    }

    public ModContainer getForgeContainer() {
        return parent;
    }
}
