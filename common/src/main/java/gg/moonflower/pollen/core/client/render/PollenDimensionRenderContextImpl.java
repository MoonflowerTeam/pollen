package gg.moonflower.pollen.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.api.client.render.PollenDimensionSpecialEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollenDimensionRenderContextImpl implements PollenDimensionSpecialEffects.RenderContext {

    private final IntSupplier tickSupplier;
    private final Supplier<Float> partialTickSupplier;
    private final Supplier<Camera> cameraSupplier;
    private final Supplier<ClientLevel> levelSupplier;
    private final Supplier<PoseStack> matrixStackSupplier;
    private final Supplier<Matrix4f> projectionSupplier;

    public PollenDimensionRenderContextImpl(IntSupplier tickSupplier, Supplier<Float> partialTickSupplier, Supplier<Camera> cameraSupplier, Supplier<ClientLevel> levelSupplier, Supplier<PoseStack> matrixStackSupplier, Supplier<Matrix4f> projectionSupplier) {
        this.tickSupplier = tickSupplier;
        this.partialTickSupplier = partialTickSupplier;
        this.cameraSupplier = cameraSupplier;
        this.levelSupplier = levelSupplier;
        this.matrixStackSupplier = matrixStackSupplier;
        this.projectionSupplier = projectionSupplier;
    }

    @Override
    public int getTicks() {
        return this.tickSupplier.getAsInt();
    }

    @Override
    public float getPartialTicks() {
        return this.partialTickSupplier.get();
    }

    @Override
    public Camera getCamera() {
        return this.cameraSupplier.get();
    }

    @Override
    public ClientLevel getLevel() {
        return this.levelSupplier.get();
    }

    @Override
    public PoseStack getMatrixStack() {
        return this.matrixStackSupplier.get();
    }

    @Override
    public Matrix4f getProjection() {
        return this.projectionSupplier.get();
    }
}
