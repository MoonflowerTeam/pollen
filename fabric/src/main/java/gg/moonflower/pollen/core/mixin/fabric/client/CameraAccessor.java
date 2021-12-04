package gg.moonflower.pollen.core.mixin.fabric.client;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor
    void setXRot(float xRot);

    @Accessor
    void setYRot(float yRot);
}
