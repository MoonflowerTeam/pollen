package gg.moonflower.pollen.core.mixin.forge.client;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.util.forge.ForgeModResourcePack;
import gg.moonflower.pollen.api.util.forge.ModResourcePackCreator;
import gg.moonflower.pollen.core.mixin.forge.PackRepositoryAccessor;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Shadow
    private PackRepository tempDataPackRepository;

    @ModifyArg(method = "openFresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;<init>(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/world/level/DataPackConfig;Lnet/minecraft/client/gui/screens/worldselection/WorldGenSettingsComponent;)V"), index = 1)
    private static DataPackConfig onNew(DataPackConfig settings) {
        ModResourcePackCreator modResourcePackCreator = new ModResourcePackCreator(PackType.SERVER_DATA);
        List<Pack> moddedResourcePacks = new ArrayList<>();
        modResourcePackCreator.loadPacks(moddedResourcePacks::add, (id, name, required, supplier, metadataSection, position, source, hidden) -> new Pack(id, name, required, supplier, metadataSection, PackType.SERVER_DATA, position, source, hidden));

        List<String> enabled = new ArrayList<>(settings.getEnabled());
        List<String> disabled = new ArrayList<>(settings.getDisabled());

        for (Pack pack : moddedResourcePacks) {
            try (PackResources resources = pack.open()) {

                if (resources instanceof ForgeModResourcePack && ((ForgeModResourcePack) resources).isEnabledByDefault()) {
                    enabled.add(pack.getId());
                } else {
                    disabled.add(pack.getId());
                }
            }
        }

        return new DataPackConfig(enabled, disabled);
    }

    @Inject(method = "getDataPackSelectionSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V", shift = At.Shift.BEFORE))
    private void onScanPacks(CallbackInfoReturnable<Pair<File, PackRepository>> cir) {
        ((PackRepositoryAccessor) this.tempDataPackRepository).getSources().add(new ModResourcePackCreator(PackType.SERVER_DATA));
    }
}
