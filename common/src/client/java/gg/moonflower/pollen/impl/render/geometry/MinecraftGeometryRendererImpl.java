package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryBufferSource;
import gg.moonflower.pollen.api.render.geometry.v1.MinecraftGeometryRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class MinecraftGeometryRendererImpl implements MinecraftGeometryRenderer {

    public static final MinecraftGeometryRenderer INSTANCE = new MinecraftGeometryRendererImpl();

    private final Map<String, VertexConsumer> builders;
    private final Vector3f pos;
    private final Vector3f normal;

    private GeometryBufferSource bufferSource;
    private TextureTable textures;
    private int packedLight;
    private int packedOverlay;
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public MinecraftGeometryRendererImpl() {
        this.builders = new HashMap<>();
        this.pos = new Vector3f();
        this.normal = new Vector3f();
        this.bufferSource = null;
        this.textures = null;
    }

    @Override
    public void render(GeometryModel model, TextureTable textureTable, GeometryBufferSource bufferSource, MatrixStack matrixStack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bufferSource = bufferSource;
        this.textures = textureTable;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        model.render(this, matrixStack);
        this.textures = null;
        this.bufferSource = null;
    }

    @Override
    public void render(MatrixStack matrixStack, Polygon polygon) {
        if (this.bufferSource == null) {
            throw new IllegalStateException("Use MinecraftGeometryRenderer instead");
        }

        String material = polygon.material();
        Vertex[] vertices = polygon.vertices();
        Vector3fc[] normals = polygon.normals();

        Matrix4f positionMat = matrixStack.position();
        Matrix3f normalMat = matrixStack.normal();

        this.builders.clear();
        for (int i = 0; i < 4; i++) {
            int index = Mth.clamp(i, 0, vertices.length - 1);
            Vertex vertex = vertices[index];
            Vector3fc normal = normals[index];

            this.normal.set(normal.x(), normal.y(), normal.z());
            normalMat.transform(this.normal);

            this.pos.set(vertex.x(), vertex.y(), vertex.z());
            positionMat.transformPosition(vertex.x(), vertex.y(), vertex.z(), this.pos);

            VertexConsumer builder = this.builders.computeIfAbsent(material, this::createBuilder);
            builder.vertex(this.pos.x(), this.pos.y(), this.pos.z(),
                    this.red, this.green, this.blue, this.alpha,
                    vertex.u(), vertex.v(),
                    this.packedOverlay, this.packedLight,
                    this.normal.x(), this.normal.y(), this.normal.z());
        }
    }

    private VertexConsumer createBuilder(String material) {
        return VertexMultiConsumer.create(Arrays.stream(this.textures.getLayerTextures(material))
                .map(this.bufferSource::getBuffer)
                .toArray(VertexConsumer[]::new));
    }
}
