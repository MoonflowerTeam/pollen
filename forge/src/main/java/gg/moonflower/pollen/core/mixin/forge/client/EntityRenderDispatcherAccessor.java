package gg.moonflower.pollen.core.mixin.forge.client;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {

    @Accessor
    Map<EntityType<?>, EntityRenderer<?>> getRenderers();

    @Accessor
    Map<String, PlayerRenderer> getPlayerRenderers();
}
