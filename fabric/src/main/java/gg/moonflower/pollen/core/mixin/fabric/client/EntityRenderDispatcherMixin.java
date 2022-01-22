package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.client.render.AddRenderLayersEvent;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Final
    @Shadow
    private Map<String, PlayerRenderer> playerRenderers;

    @Final
    @Shadow
    private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, Font font, Options options, CallbackInfo ci) {
        AddRenderLayersEvent.EVENT.invoker().addLayers(new AddRenderLayersEvent.Context() {
            @Override
            public Set<String> getSkins() {
                return playerRenderers.keySet();
            }

            @Nullable
            @Override
            public PlayerRenderer getSkin(String skinName) {
                return playerRenderers.get(skinName);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> @Nullable R getRenderer(EntityType<? extends T> entityType) {
                return (R) renderers.get(entityType);
            }
        });
    }
}
