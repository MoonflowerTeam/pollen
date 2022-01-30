package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.client.render.AddRenderLayersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Shadow
    private Map<String, EntityRenderer<? extends Player>> playerRenderers;

    @Shadow
    private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    public void onResourceManagerReload(ResourceManager resourceManager, CallbackInfo ci) {
        Map<String, EntityRenderer<? extends Player>> playerRenderers = this.playerRenderers;
        Map<EntityType<?>, EntityRenderer<?>> renderers = this.renderers;

        AddRenderLayersEvent.EVENT.invoker().addLayers(new AddRenderLayersEvent.Context() {
            @Override
            public Set<String> getSkins() {
                return playerRenderers.keySet();
            }

            @Nullable
            @Override
            public PlayerRenderer getSkin(String skinName) {
                return (PlayerRenderer) playerRenderers.get(skinName);
            }

            @SuppressWarnings("unchecked")
            @Nullable
            @Override
            public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType) {
                return (R) renderers.get(entityType);
            }

            @Override
            public EntityModelSet getEntityModels() {
                return Minecraft.getInstance().getEntityModels();
            }
        });
    }
}
