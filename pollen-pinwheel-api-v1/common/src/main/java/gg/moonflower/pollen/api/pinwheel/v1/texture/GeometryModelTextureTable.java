package gg.moonflower.pollen.api.pinwheel.v1.texture;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A table of textures to be used for geometry model rendering.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GeometryModelTextureTable {

    public static final Codec<GeometryModelTextureTable> CODEC = Codec.unboundedMap(Codec.STRING,
                    Codec.either(
                            GeometryModelTexture.CODEC.listOf().xmap(list -> list.toArray(new GeometryModelTexture[0]), Arrays::asList),
                            GeometryModelTexture.CODEC.xmap(texture -> new GeometryModelTexture[]{texture}, array -> array.length > 0 ? array[0] : GeometryModelTexture.MISSING)
                    ).xmap(either -> either.left().orElseGet(() -> either.right().orElseThrow(() -> new NoSuchElementException("No value present"))), array -> array.length > 1 ? Either.left(array) : Either.right(array))) // Left is multiple layers, right is one layer
            .xmap(GeometryModelTextureTable::new, table -> table.textures);
    public static GeometryModelTextureTable EMPTY = new GeometryModelTextureTable(new HashMap<>());
    private static final GeometryModelTexture[] MISSING = new GeometryModelTexture[]{GeometryModelTexture.MISSING};

    private final Map<String, GeometryModelTexture[]> textures;

    public GeometryModelTextureTable(Map<String, GeometryModelTexture[]> textures) {
        this.textures = new HashMap<>(textures);
        this.textures.values().removeIf(layers -> layers.length == 0);
    }

    /**
     * Fetches a geometry model texture by the specified key.
     *
     * @param key The key of the textures to get
     * @return The texture with that key or {@link GeometryModelTexture#MISSING} if there is no texture bound to that key
     */
    public GeometryModelTexture[] getLayerTextures(@Nullable String key) {
        return this.textures.getOrDefault(key, MISSING);
    }

    /**
     * @return All textures that need to be loaded
     */
    public Collection<GeometryModelTexture[]> getTextures() {
        return this.textures.values();
    }

    /**
     * @return All definitions for textures
     */
    public Map<String, GeometryModelTexture[]> getTextureDefinitions() {
        return textures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeometryModelTextureTable that = (GeometryModelTextureTable) o;
        return textures.equals(that.textures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textures);
    }

    @Override
    public String toString() {
        return "GeometryModelTextureTable{" +
                "textures=" + textures +
                '}';
    }
}
