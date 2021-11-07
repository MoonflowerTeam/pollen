package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public class GeometryModelBufferSource extends MultiBufferSource.BufferSource {

    private final Map<Integer, Map<RenderType, BufferBuilder>> builders;

    private GeometryModelTexture.TextureLayer layer;

    public GeometryModelBufferSource() {
        super(new BufferBuilder(256), Collections.emptyMap());
        this.builders = Util.make(new Int2ObjectLinkedOpenHashMap<>(GeometryModelTexture.TextureLayer.values().length), map ->
        {
            for (GeometryModelTexture.TextureLayer layer : GeometryModelTexture.TextureLayer.values())
                map.put(layer.ordinal(), new Object2ObjectLinkedOpenHashMap<>());
        });
        this.layer = GeometryModelTexture.TextureLayer.SOLID;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        BufferBuilder builder = this.builders.get(this.layer.ordinal()).computeIfAbsent(renderType, __ -> new BufferBuilder(renderType.bufferSize()));
        if (this.startedBuffers.add(builder))
            builder.begin(renderType.mode(), renderType.format());
        return builder;
    }

    private Optional<BufferBuilder> getBuilderRaw(RenderType arg) {
        for (GeometryModelTexture.TextureLayer layer : GeometryModelTexture.TextureLayer.values()) {
            Map<RenderType, BufferBuilder> buffers = this.builders.get(layer.ordinal());
            if (buffers.containsKey(arg))
                return Optional.of(buffers.get(arg));
        }
        return Optional.empty();
    }

    @Override
    public void endBatch() {
        for (GeometryModelTexture.TextureLayer layer : GeometryModelTexture.TextureLayer.values())
            for (RenderType lv : this.builders.get(layer.ordinal()).keySet())
                this.endBatch(lv);
    }

    @Override
    public void endBatch(RenderType renderType) {
        this.getBuilderRaw(renderType).ifPresent(lv ->
        {
            if (this.startedBuffers.remove(lv)) {
                renderType.end(lv, 0, 0, 0);
                this.lastState = Optional.empty();
            }
        });
    }

    public void setLayer(GeometryModelTexture.TextureLayer layer) {
        this.layer = layer;
    }
}
