package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.math.Vector3f;
import gg.moonflower.pollen.core.mixin.client.ModelPartAccessor;
import gg.moonflower.pollen.core.mixin.client.ModelPartCubeAccessor;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class JavaModelConverter {

    public static GeometryModelData.Bone[] convert(Model model) {
        Map<String, ModelPart> parts = mapRenderers(model);
        Map<String, GeometryModelData.Bone> boneMap = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();

        for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
            ModelPartAccessor accessor = (ModelPartAccessor) entry.getValue();
            for (ModelPart child : accessor.getChildren()) { // If child doesn't exist then it is just used for cubes
                getBoneName(parts, child).ifPresent(s -> parentMap.put(s, entry.getKey()));
            }
        }

        for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
            ModelPart part = entry.getValue();
            ModelPartAccessor accessor = (ModelPartAccessor) part;
            List<ModelPart.Cube> modelCubes = accessor.getCubes();

            List<GeometryModelData.Cube> cubes = modelCubes.stream().map(cube -> {
                Vector3f origin = new Vector3f(cube.minX, cube.minY, cube.minZ);
                Vector3f size = new Vector3f(cube.maxX - cube.minX, cube.maxY - cube.minY, cube.maxZ - cube.minZ);
                ModelPart.Polygon[] polygons = ((ModelPartCubeAccessor) cube).getPolygons();
                return calculateCube(cube, origin, size, new Vector3f(), new Vector3f(origin.x() + size.x() / 2F, origin.y() + size.y() / 2F, origin.z() + size.z() / 2F), polygons, part.x, part.y, part.z, accessor.getXTexSize(), accessor.getYTexSize());
            }).collect(Collectors.toList());

            getChildren(parts, part).forEach(child -> ((ModelPartAccessor) child).getCubes().forEach(cube -> {
                ModelPartAccessor childAccessor = (ModelPartAccessor) child;
                Vector3f origin = new Vector3f(cube.minX + child.x, cube.minY + child.x, cube.minZ + child.x);
                Vector3f size = new Vector3f(cube.maxX - cube.minX, cube.maxY - cube.minY, cube.maxZ - cube.minZ);
                ModelPart.Polygon[] polygons = ((ModelPartCubeAccessor) cube).getPolygons();
                cubes.add(calculateCube(cube, origin, size, new Vector3f(child.x, child.y, child.z), new Vector3f(child.xRot * (float) (180F / Math.PI), child.yRot * (float) (180F / Math.PI), child.zRot * (float) (180F / Math.PI)), polygons, child.x, child.y, child.z, childAccessor.getXTexSize(), childAccessor.getYTexSize()));
            }));

            GeometryModelData.Bone bone = new GeometryModelData.Bone(
                    entry.getKey(),
                    false,
                    false,
                    parentMap.get(entry.getKey()),
                    new Vector3f(part.x, part.y, part.z),
                    new Vector3f(part.xRot * (float) (180F / Math.PI), part.yRot * (float) (180F / Math.PI), part.zRot * (float) (180F / Math.PI)),
                    new Vector3f(),
                    false,
                    0.0F,
                    false,
                    cubes.toArray(new GeometryModelData.Cube[0]),
                    new GeometryModelData.Locator[0],
                    null
            );
            boneMap.put(entry.getKey(), bone);
        }

        return boneMap.values().toArray(new GeometryModelData.Bone[0]);
    }

    private static GeometryModelData.Cube calculateCube(ModelPart.Cube cube, Vector3f origin, Vector3f size, Vector3f rotation, Vector3f rotationPoint, ModelPart.Polygon[] polygons, float xOffset, float yOffset, float zOffset, float xTexSize, float yTexSize) {
        ModelPart.Vertex vertex1 = polygons[1].vertices[0];
        float inflate = -(origin.y() - vertex1.pos.y()); // origin.y() + size.y() - vertex1.pos.y()

        // g = minY - inflate

//        GeometryModelData.CubeUV[] uvs = new GeometryModelData.CubeUV[6];
//        float u = vertex5.u * xTexSize;
//        float v = vertex6.v * yTexSize;
//        uvs[Direction.NORTH.get3DDataValue()] = new GeometryModelData.CubeUV(u + size.z(), v + size.z(), size.x(), size.y(), "texture");
//        uvs[Direction.EAST.get3DDataValue()] = new GeometryModelData.CubeUV(u, v + size.z(), size.z(), size.y(), "texture");
//        uvs[Direction.SOUTH.get3DDataValue()] = new GeometryModelData.CubeUV(u + size.x() + size.z() * 2, v + size.z(), size.x(), size.y(), "texture");
//        uvs[Direction.WEST.get3DDataValue()] = new GeometryModelData.CubeUV(u + size.x() + size.z(), v + size.z(), size.z(), size.y(), "texture");
//        uvs[Direction.UP.get3DDataValue()] = new GeometryModelData.CubeUV(u + size.z(), v, size.x(), size.z(), "texture");
//        uvs[Direction.DOWN.get3DDataValue()] = new GeometryModelData.CubeUV(u + size.x() + size.z(), v, size.x(), size.z(), "texture");

        GeometryModelData.CubeUV[] uvs = Arrays.stream(Direction.values()).map(direction -> getUV(cube, direction, xTexSize, yTexSize)).toArray(GeometryModelData.CubeUV[]::new);
        return new GeometryModelData.Cube(new Vector3f(origin.x() + xOffset, origin.y() + yOffset, origin.z() + zOffset), size, rotation, rotationPoint, true, inflate / 16F, false, false, uvs);
    }

    private static GeometryModelData.CubeUV getUV(ModelPart.Cube cube, Direction direction, float xTexSize, float yTexSize) {
        ModelPart.Polygon polygon;

        ModelPart.Polygon[] polygons = ((ModelPartCubeAccessor) cube).getPolygons();
        switch (direction) {
            case DOWN:
                polygon = polygons[2];
                break;
            case UP:
                polygon = polygons[3];
                break;
            case NORTH:
                polygon = polygons[4];
                break;
            case SOUTH:
                polygon = polygons[5];
                break;
            case WEST:
                polygon = polygons[1];
                break;
            case EAST:
                polygon = polygons[0];
                break;
            default:
                throw new IllegalStateException("Unknown direction: " + direction);
        }

        int min = 0;
        int max = 2;

        if (direction == Direction.DOWN) {
            int swap = min;
            min = max;
            max = swap;
        }

        float u0 = polygon.vertices[min].u;
        float v0 = polygon.vertices[min].v;
        float u1 = polygon.vertices[max].u;
        float v1 = polygon.vertices[max].v;
        return new GeometryModelData.CubeUV(u0 * xTexSize, v0 * yTexSize, (u1 - u0) * xTexSize, (v1 - v0) * yTexSize, "texture");
    }

    private static List<ModelPart> getChildren(Map<String, ModelPart> parts, ModelPart parent) {
        List<ModelPart> children = new ArrayList<>();

        List<ModelPart> openSet = new ArrayList<>();
        openSet.add(parent);
        while (!openSet.isEmpty()) {
            ModelPart child = openSet.remove(0);
            Optional<String> boneName = getBoneName(parts, child);
            if (boneName.isPresent()) {
                openSet.addAll(((ModelPartAccessor) child).getChildren());
            } else {
                children.add(child);
            }
        }

        return children;
    }

    private static Optional<String> getBoneName(Map<String, ModelPart> parts, ModelPart part) {
        return parts.entrySet().stream().filter(e -> e.getValue() == part).map(Map.Entry::getKey).findFirst();
    }

    private static Map<String, ModelPart> mapRenderers(Model model) {
        Map<String, ModelPart> renderers = new HashMap<>();
        Class<?> i = model.getClass();
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    if (ModelPart.class.isAssignableFrom(field.getType())) {
                        try {
                            field.setAccessible(true);
                            renderers.put(field.getName(), (ModelPart) field.get(model));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            i = i.getSuperclass();
        }
        return renderers;
    }
}
