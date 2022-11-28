package gg.moonflower.pollen.pinwheel.core.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Cr
 * @author Ocelot
 * @since 1.6.0
 */
public class CachedBufferSource extends MultiBufferSource.BufferSource {

    private final Map<RenderType, BufferBuilder> layerBuffers;
    private final Set<RenderType> startedBuffers;

    @ApiStatus.Internal
    public CachedBufferSource() {
        super(null, Collections.emptyMap());
        this.layerBuffers = new HashMap<>();
        this.startedBuffers = new HashSet<>();
    }

    private BufferBuilder getBuilderRaw(RenderType renderType) {
        return this.layerBuffers.computeIfAbsent(renderType, type -> new BufferBuilder(type.bufferSize()));
    }

    @Override
    public BufferVertexConsumer getBuffer(RenderType renderType) {
        BufferBuilder bufferBuilder = this.getBuilderRaw(renderType);

        if (this.startedBuffers.add(renderType)) {
            bufferBuilder.begin(renderType.mode(), renderType.format());
        }

        return bufferBuilder;
    }

    @Override
    public void endLastBatch() {
    }

    @Override
    public void endBatch() {
        for (RenderType renderType : this.startedBuffers) {
            renderType.end(this.getBuilderRaw(renderType), 0, 0, 0);
        }
        this.startedBuffers.clear();
    }

    @Override
    public void endBatch(RenderType renderType) {
        BufferBuilder bufferBuilder = this.getBuilderRaw(renderType);
        if (this.startedBuffers.remove(renderType)) {
            renderType.end(bufferBuilder, 0, 0, 0);
        }
    }
}
