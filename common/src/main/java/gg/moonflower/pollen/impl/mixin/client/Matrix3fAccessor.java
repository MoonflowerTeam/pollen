package gg.moonflower.pollen.impl.mixin.client;

import com.mojang.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix3f.class)
public interface Matrix3fAccessor {

    @Accessor("m00")
    float m00();

    @Accessor("m01")
    float m01();

    @Accessor("m02")
    float m02();

    @Accessor("m10")
    float m10();

    @Accessor("m11")
    float m11();

    @Accessor("m12")
    float m12();

    @Accessor("m20")
    float m20();

    @Accessor("m21")
    float m21();

    @Accessor("m22")
    float m22();

    @Accessor("m00")
    void m00(float value);

    @Accessor("m01")
    void m01(float value);

    @Accessor("m02")
    void m02(float value);

    @Accessor("m10")
    void m10(float value);

    @Accessor("m11")
    void m11(float value);

    @Accessor("m12")
    void m12(float value);

    @Accessor("m20")
    void m20(float value);

    @Accessor("m21")
    void m21(float value);

    @Accessor("m22")
    void m22(float value);
}
