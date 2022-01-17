package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.block.model.ItemOverride;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemOverride.Deserializer.class)
public interface ItemOverrideDeserializerAccessor {

    @Invoker("<init>")
    static ItemOverride.Deserializer invokeInit() {
        return Platform.error();
    }
}
