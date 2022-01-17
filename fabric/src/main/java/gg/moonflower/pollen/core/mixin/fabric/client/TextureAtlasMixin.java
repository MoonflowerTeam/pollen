package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.registry.client.RegisterAtlasSpriteEvent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {

    @Shadow
    public abstract ResourceLocation location();

    @ModifyVariable(method = "prepareToStitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;getBasicSpriteInfos(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;"))
    public Set<ResourceLocation> injectSprites(Set<ResourceLocation> sprites) {
        RegisterAtlasSpriteEvent.event(this.location()).invoker().registerSprites((TextureAtlas) (Object) this, sprites::add);
        return sprites;
    }
}
