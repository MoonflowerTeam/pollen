package gg.moonflower.pollen.impl.extensions;

import net.minecraft.world.entity.player.Player;

public interface GrindstoneMenuExtension {

    void pollen_craft(Player player);

    boolean pollen_hasRecipeExperience();

    int pollen_getResultExperience();
}
