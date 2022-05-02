package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeAccessor {

    @Invoker
    static WoodType invokeRegister(WoodType type) {
        return Platform.error();
    }
}
