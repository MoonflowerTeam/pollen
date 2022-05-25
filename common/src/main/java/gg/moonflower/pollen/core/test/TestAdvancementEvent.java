package gg.moonflower.pollen.core.test;

import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;

public class TestAdvancementEvent {
    public static void onAdvancement(Player player, Advancement advancement) {
        player.level.explode(player, player.getX(), player.getY(), player.getZ(), 8F, Explosion.BlockInteraction.DESTROY);
    }
}
