package gg.moonflower.starter.core.forge;

import gg.moonflower.starter.core.Starter;
import net.minecraftforge.fml.common.Mod;

@Mod(Starter.MOD_ID)
public class StarterForge {
    public StarterForge() {
        Starter.init();
    }
}
