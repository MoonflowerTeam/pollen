package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.fml.common.Mod;

@Mod(Pollen.MOD_ID)
public class PollenForge {
    public PollenForge() {
        Pollen.init();
    }
}
