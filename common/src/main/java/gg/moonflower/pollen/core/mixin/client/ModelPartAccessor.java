package gg.moonflower.pollen.core.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {

    @Accessor
    float getXTexSize();

    @Accessor
    float getYTexSize();

    @Accessor
    ObjectList<ModelPart.Cube> getCubes();

    @Accessor
    ObjectList<ModelPart> getChildren();
}
