package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.util.forge.ModResourcePackCreator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Shadow
    @Final
    @Mutable
    private Set<RepositorySource> sources;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void init(Pack.PackConstructor arg, RepositorySource[] resourcePackProviders, CallbackInfo info) {
        this.sources = new HashSet<>(this.sources);

        for (RepositorySource provider : this.sources) {
            if (provider instanceof FolderRepositorySource && (((FolderRepositorySourceAccessor) provider).getPackSource() == PackSource.WORLD || ((FolderRepositorySourceAccessor) provider).getPackSource() == PackSource.SERVER)) {
                this.sources.add(new ModResourcePackCreator(PackType.SERVER_DATA));
                return;
            }
        }
    }
}
