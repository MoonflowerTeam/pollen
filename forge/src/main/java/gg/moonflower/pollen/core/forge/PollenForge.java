package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PollenForge::init);
    }

    private static void init(FMLCommonSetupEvent event) {
    }
}
