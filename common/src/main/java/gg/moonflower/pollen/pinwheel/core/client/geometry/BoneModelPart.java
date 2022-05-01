package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimatedModelPart;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class BoneModelPart extends ModelPart implements AnimatedModelPart {

    private static final Vector4f TRANSFORM_VECTOR = new Vector4f();
    private static final Vector3f NORMAL_VECTOR = new Vector3f();

    private final BedrockGeometryModel parent;
    private final GeometryModelData.Bone bone;
    private final Set<BoneModelPart> children;
    private final Map<String, ObjectList<Quad>> quads;
    private final ObjectList<Polygon> polygons;
    private final Matrix4f copyPosition;
    private final Matrix3f copyNormal;
    private final AnimatedModelPart.AnimationPose animationPose;
    private boolean copyVanilla;

    public BoneModelPart(BedrockGeometryModel parent, GeometryModelData.Bone bone) {
        super(Collections.emptyList(), Collections.emptyMap());
        this.parent = parent;
        this.bone = bone;
        this.children = new HashSet<>();
        this.quads = new Object2ObjectArrayMap<>();
        this.polygons = new ObjectArrayList<>();
        this.copyPosition = new Matrix4f();
        this.copyNormal = new Matrix3f();
        this.animationPose = new AnimationPose();
        this.resetTransform(false);
        Arrays.stream(bone.getCubes()).forEach(this::addCube);
        GeometryModelData.PolyMesh polyMesh = bone.getPolyMesh();
        if (polyMesh != null)
            this.addPolyMesh(polyMesh);
    }

    private static void addVertex(VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Matrix4f matrix4f, Vertex vertex) {
        TRANSFORM_VECTOR.set(vertex.x, vertex.y, vertex.z, 1);
        TRANSFORM_VECTOR.transform(matrix4f);
        builder.vertex(TRANSFORM_VECTOR.x(), TRANSFORM_VECTOR.y(), TRANSFORM_VECTOR.z(), red, green, blue, alpha, vertex.u, vertex.v, packedOverlay, packedLight, NORMAL_VECTOR.x(), NORMAL_VECTOR.y(), NORMAL_VECTOR.z());
    }

    private void addCube(GeometryModelData.Cube cube) {
        boolean empty = true;
        for (Direction direction : Direction.values()) {
            if (cube.getUV(direction) != null) {
                empty = false;
                break;
            }
        }

        if (empty)
            return;

        Vector3f origin = cube.getOrigin();
        Vector3f size = cube.getSize();
        float x = origin.x() / 16f;
        float y = origin.y() / 16f;
        float z = origin.z() / 16f;
        float sizeX = size.x() / 16f;
        float sizeY = size.y() / 16f;
        float sizeZ = size.z() / 16f;
        float inflate = (cube.isOverrideInflate() ? cube.getInflate() : this.bone.getInflate()) / 16f;

        float x1 = x + sizeX;
        float y1 = y + sizeY;
        float z1 = z + sizeZ;
        x = x - inflate;
        y = y - inflate;
        z = z - inflate;
        x1 = x1 + inflate;
        y1 = y1 + inflate;
        z1 = z1 + inflate;

        if (x == x1 && y == y1 && z == z1)
            return;

        boolean mirror = cube.isOverrideMirror() ? cube.isMirror() : this.bone.isMirror();
        if (mirror) {
            float f3 = x1;
            x1 = x;
            x = f3;
        }

        Vector3f rotation = cube.getRotation();
        Vector3f pivot = cube.getPivotX();
        float rotationX = rotation.x();
        float rotationY = rotation.y();
        float rotationZ = rotation.z();
        float pivotX = pivot.x() / 16f;
        float pivotY = -pivot.y() / 16f;
        float pivotZ = pivot.z() / 16f;

        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(pivotX, pivotY, pivotZ);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rotationZ));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotationY));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(rotationX));
        matrixStack.translate(-pivotX, -pivotY, -pivotZ);
        PoseStack.Pose entry = matrixStack.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();

        if (y != y1) {
            if (x != x1) {
                this.addFace(cube, matrix4f, matrix3f, x1, y1, z, x, y1, z, x, y, z, x1, y, z, Direction.NORTH);
                this.addFace(cube, matrix4f, matrix3f, x, y1, z1, x1, y1, z1, x1, y, z1, x, y, z1, Direction.SOUTH);
            }
            if (z != z1) {
                this.addFace(cube, matrix4f, matrix3f, x, y1, z, x, y1, z1, x, y, z1, x, y, z, Direction.EAST);
                this.addFace(cube, matrix4f, matrix3f, x1, y1, z1, x1, y1, z, x1, y, z, x1, y, z1, Direction.WEST);
            }
        }

        if (x != x1 && z != z1) {
            this.addFace(cube, matrix4f, matrix3f, x, y, z1, x1, y, z1, x1, y, z, x, y, z, Direction.DOWN);
            this.addFace(cube, matrix4f, matrix3f, x1, y1, z1, x, y1, z1, x, y1, z, x1, y1, z, Direction.UP);
        }
    }

    private void addPolyMesh(GeometryModelData.PolyMesh polyMesh) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        for (GeometryModelData.Poly poly : polyMesh.getPolys()) {
            Vertex[] vertices = new Vertex[polyMesh.getPolyType().getVertices()];
            Vector3f[] normals = new Vector3f[polyMesh.getPolyType().getVertices()];
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = this.getVertex(polyMesh, poly, matrix4f, i);
                normals[i] = polyMesh.getNormals()[poly.getNormals()[i]].copy();
                normals[i].mul(1, -1, 1);
            }
            this.polygons.add(new Polygon(vertices, normals));
        }
    }

    private Vertex getVertex(GeometryModelData.PolyMesh polyMesh, GeometryModelData.Poly poly, Matrix4f matrix4f, int index) {
        Vector3f position = polyMesh.getPositions()[poly.getPositions()[index]];
        Vec2 uv = polyMesh.getUvs()[poly.getUVs()[index]];
        return new Vertex(matrix4f, position.x(), -position.y(), position.z(), polyMesh.isNormalizedUvs() ? uv.x : uv.x / this.parent.getTextureWidth(), 1 - (polyMesh.isNormalizedUvs() ? uv.y : uv.y / this.parent.getTextureHeight()));
    }

    private void addFace(GeometryModelData.Cube cube, Matrix4f matrix4f, Matrix3f matrix3f, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Direction face) {
        GeometryModelData.CubeUV uv = cube.getUV(face);
        if (uv != null) {
            this.quads.computeIfAbsent(uv.getMaterialInstance(), __ -> new ObjectArrayList<>()).add(new Quad(new Vertex[]{
                    new Vertex(matrix4f, x0, -y0, z0, (uv.getU() + uv.getUSize()) / this.parent.getTextureWidth(), uv.getV() / this.parent.getTextureHeight()),
                    new Vertex(matrix4f, x1, -y1, z1, uv.getU() / this.parent.getTextureWidth(), uv.getV() / this.parent.getTextureHeight()),
                    new Vertex(matrix4f, x2, -y2, z2, uv.getU() / this.parent.getTextureWidth(), (uv.getV() + uv.getVSize()) / this.parent.getTextureHeight()),
                    new Vertex(matrix4f, x3, -y3, z3, (uv.getU() + uv.getUSize()) / this.parent.getTextureWidth(), (uv.getV() + uv.getVSize()) / this.parent.getTextureHeight())
            }, matrix3f, uv.getMaterialInstance(), cube.isOverrideMirror() ? cube.isMirror() : this.bone.isMirror(), face.getOpposite()));
        }
    }

    void addChild(BoneModelPart part) {
        this.children.add(part);
    }

    /**
     * Resets the transformation of this part.
     *
     * @param resetChildren Whether to reset the transformations of all child parts
     */
    public void resetTransform(boolean resetChildren) {
        Vector3f rotation = this.bone.getRotation();
        Vector3f pivot = this.bone.getPivot();
        this.xRot = (float) (Math.PI / 180f) * rotation.x();
        this.yRot = (float) (Math.PI / 180f) * rotation.y();
        this.zRot = (float) (Math.PI / 180f) * rotation.z();
        this.x = pivot.x();
        this.y = -pivot.y();
        this.z = pivot.z();
        this.copyPosition.setIdentity();
        this.copyNormal.setIdentity();
        this.animationPose.reset();
        if (resetChildren)
            this.children.forEach(boneModelPart -> boneModelPart.resetTransform(true));
        this.copyVanilla = false;
    }

    @Override
    public void render(PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.render(matrixStack, builder, packedLight, packedOverlay, red, green, blue, alpha);

        if (this.visible && alpha > 0 && (!this.quads.isEmpty() || !this.polygons.isEmpty() || !this.children.isEmpty())) {
            matrixStack.pushPose();
            this.translateAndRotate(matrixStack);

            if (this.copyVanilla)
                matrixStack.translate(-this.x / 16.0F, -this.y / 16.0F, -this.z / 16.0F);

            Matrix4f matrix4f = matrixStack.last().pose();
            Matrix3f matrix3f = matrixStack.last().normal();
            Collection<Quad> quads = this.quads.get(this.parent.getActiveMaterial());
            if (quads != null) {
                for (Quad quad : quads) {
                    NORMAL_VECTOR.set(-quad.normal.x(), quad.normal.y(), -quad.normal.z());
                    NORMAL_VECTOR.transform(matrix3f);
                    for (Vertex vertex : quad.vertices) {
                        addVertex(builder, packedLight, packedOverlay, red, green, blue, alpha, matrix4f, vertex);
                    }
                }
            }
            if ("poly_mesh.texture".equals(this.parent.getActiveMaterial())) {
                for (Polygon polygon : this.polygons) {
                    for (int i = 0; i < 4; i++) {
                        int index = Mth.clamp(i, 0, polygon.vertices.length - 1);
                        Vertex vertex = polygon.vertices[index];
                        Vector3f normal = polygon.normals[index];
                        NORMAL_VECTOR.set(normal.x(), normal.y(), normal.z());
                        NORMAL_VECTOR.transform(matrix3f);
                        addVertex(builder, packedLight, packedOverlay, red, green, blue, alpha, matrix4f, vertex);
                    }
                }
            }

            for (ModelPart part : this.children)
                part.render(matrixStack, builder, packedLight, packedOverlay, red, green, blue, alpha);

            matrixStack.popPose();
        }
    }

    @Override
    public void copyFrom(ModelPart part) {
        this.copyPosition.setIdentity();
        this.copyNormal.setIdentity();
        PoseStack matrixStack = new PoseStack();
        part.translateAndRotate(matrixStack);
        this.copyPosition.multiply(matrixStack.last().pose());
        this.copyNormal.mul(matrixStack.last().normal());
        this.copyVanilla = !BoneModelPart.class.isAssignableFrom(part.getClass());
    }

    @Override
    public void translateAndRotate(PoseStack matrixStack) {
        matrixStack.last().pose().multiply(this.copyPosition);
        matrixStack.last().normal().mul(this.copyNormal);
        matrixStack.translate((this.animationPose.getPosition().x() + this.x) / 16.0F, (-this.animationPose.getPosition().y() + this.y) / 16.0F, (this.animationPose.getPosition().z() + this.z) / 16.0F);
        if (this.animationPose.getScale().hashCode() != 1333788672) // 1333788672 is the hash code of a 1, 1, 1 vector;
            matrixStack.scale(this.animationPose.getScale().x(), this.animationPose.getScale().y(), this.animationPose.getScale().z());
        if (this.zRot + this.animationPose.getRotation().z() != 0)
            matrixStack.mulPose(Vector3f.ZP.rotation(this.zRot + (float) (this.animationPose.getRotation().z() / 180.0F * Math.PI)));
        if (this.yRot + this.animationPose.getRotation().y() != 0)
            matrixStack.mulPose(Vector3f.YP.rotation(this.yRot + (float) (this.animationPose.getRotation().y() / 180.0F * Math.PI)));
        if (this.xRot + this.animationPose.getRotation().x() != 0)
            matrixStack.mulPose(Vector3f.XP.rotation(this.xRot + (float) (this.animationPose.getRotation().x() / 180.0F * Math.PI)));
        matrixStack.translate(-this.x / 16.0F, -this.y / 16.0F, -this.z / 16.0F);
    }

    /**
     * @return The bone this model renderer is rendering
     */
    public GeometryModelData.Bone getBone() {
        return bone;
    }

    @Override
    public AnimationPose getAnimationPose() {
        return animationPose;
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return this.bone.getLocators();
    }

    private static class Vertex {
        private final float x;
        private final float y;
        private final float z;
        private final float u;
        private final float v;

        private Vertex(Matrix4f matrix4f, float x, float y, float z, float u, float v) {
            TRANSFORM_VECTOR.set(x, y, z, 1.0F);
            TRANSFORM_VECTOR.transform(matrix4f);
            this.x = TRANSFORM_VECTOR.x();
            this.y = TRANSFORM_VECTOR.y();
            this.z = TRANSFORM_VECTOR.z();
            this.u = u;
            this.v = v;
        }
    }

    private static class Quad {
        private final Vertex[] vertices;
        private final Vector3f normal;
        private final String material;

        public Quad(Vertex[] vertices, Matrix3f normal, String material, boolean mirror, Direction direction) {
            this.vertices = vertices;
            this.material = material;
            if (mirror) {
                int i = vertices.length;

                for (int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }

            this.normal = direction.step();
            if (mirror) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }
            this.normal.transform(normal);
        }
    }

    private static class Polygon {
        private final Vertex[] vertices;
        private final Vector3f[] normals;

        public Polygon(Vertex[] vertices, Vector3f[] normals) {
            this.vertices = vertices;
            this.normals = normals;
        }
    }
}
