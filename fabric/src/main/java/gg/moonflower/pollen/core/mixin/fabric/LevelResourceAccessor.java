package gg.moonflower.pollen.core.mixin.fabric;

import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelResource.class)
public interface LevelResourceAccessor {

    @Invoker("<init>")
    static LevelResource init(String string) {
        throw new AssertionError();
    }
}
