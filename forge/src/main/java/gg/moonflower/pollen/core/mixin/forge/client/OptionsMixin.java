package gg.moonflower.pollen.core.mixin.forge.client;

import gg.moonflower.pollen.api.util.forge.ForgeModResourcePack;
import gg.moonflower.pollen.api.util.forge.ModResourcePackCreator;
import net.minecraft.client.Options;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Options.class)
public class OptionsMixin {

    @Shadow
    public List<String> resourcePacks;

    @Inject(method = "load", at = @At("RETURN"))
    private void onLoad(CallbackInfo ci) {
        // Add built-in resource packs if they are enabled by default only if the options file is blank.
        if (this.resourcePacks.isEmpty()) {
            List<Pack> profiles = new ArrayList<>();
            ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.loadPacks(profiles::add, (id, name, required, supplier, metadataSection, position, source, hidden) -> new Pack(id, name, required, supplier, metadataSection, PackType.CLIENT_RESOURCES, position, source, hidden));
            this.resourcePacks = new ArrayList<>();

            for (Pack profile : profiles) {
                PackResources pack = profile.open();
                if (profile.getPackSource() == ModResourcePackCreator.RESOURCE_PACK_SOURCE || (pack instanceof ForgeModResourcePack && ((ForgeModResourcePack) pack).isEnabledByDefault())) {
                    this.resourcePacks.add(profile.getId());
                }
            }
        }
    }
}
