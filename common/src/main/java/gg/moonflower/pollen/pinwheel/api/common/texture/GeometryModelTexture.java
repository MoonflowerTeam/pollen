package gg.moonflower.pollen.pinwheel.api.common.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.core.client.geometry.GeometryRenderTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * <p>A single texture used on a {@link GeometryModel}.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GeometryModelTexture {

    public static final GeometryModelTexture MISSING = new GeometryModelTexture(Type.UNKNOWN, TextureLayer.SOLID, "missingno", false, -1, false, false);
    public static final Codec<GeometryModelTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Type::byName, type -> type.name().toLowerCase(Locale.ROOT)).fieldOf("type").forGetter(GeometryModelTexture::getType),
            Codec.STRING.xmap(TextureLayer::byName, type -> type.name().toLowerCase(Locale.ROOT)).optionalFieldOf("layer", TextureLayer.SOLID).forGetter(GeometryModelTexture::getLayer),
            Codec.STRING.fieldOf("texture").forGetter(GeometryModelTexture::getData),
            Codec.BOOL.optionalFieldOf("cache", true).forGetter(GeometryModelTexture::canCache),
            Codec.STRING.optionalFieldOf("color", "0xFFFFFF").xmap(NumberUtils::createInteger, color -> "0x" + Integer.toHexString(color & 0xFFFFFF).toUpperCase(Locale.ROOT)).forGetter(GeometryModelTexture::getColor),
            Codec.BOOL.optionalFieldOf("glowing", false).forGetter(GeometryModelTexture::isGlowing),
            Codec.BOOL.optionalFieldOf("smoothShading", false).forGetter(GeometryModelTexture::isSmoothShading)
    ).apply(instance, GeometryModelTexture::new));
    private static final Pattern ONLINE_PATTERN = Pattern.compile("=");

    private final Type type;
    private final TextureLayer layer;
    private final String data;
    private final boolean cache;
    private final int color;
    private final boolean glowing;
    private final boolean smoothShading;
    private final ResourceLocation location;

    @ApiStatus.Internal
    public GeometryModelTexture(Type type, TextureLayer layer, String data, boolean cache, int color, boolean glowing, boolean smoothShading) {
        this.type = type;
        this.layer = layer;
        this.data = data;
        this.cache = cache;
        this.color = color;
        this.glowing = glowing;
        this.smoothShading = smoothShading;
        this.location = type.createLocation(data);
        Validate.notNull(this.location, "Invalid texture data: " + data);
    }

    /**
     * @return A new builder for constructing a texture
     */
    public static Builder texture() {
        return new Builder();
    }

    /**
     * @return The type of texture this cosmetic texture is
     */
    public Type getType() {
        return type;
    }

    /**
     * @return The layer this texture uses
     */
    public TextureLayer getLayer() {
        return layer;
    }

    /**
     * @return The additional data of this texture. Can be a URL string depending on {@link #getType()}
     */
    public String getData() {
        return data;
    }

    /**
     * @return Whether caching this texture value is allowed
     */
    public boolean canCache() {
        return cache;
    }

    /**
     * @return The color of this texture
     */
    public int getColor() {
        return color;
    }

    /**
     * @return The red color factor of this texture
     */
    public float getRed() {
        return ((this.color >> 16) & 0xff) / 255f;
    }

    /**
     * @return The green color factor of this texture
     */
    public float getGreen() {
        return ((this.color >> 8) & 0xff) / 255f;
    }

    /**
     * @return The blue color factor of this texture
     */
    public float getBlue() {
        return (this.color & 0xff) / 255f;
    }

    /**
     * @return Whether this texture should be "fullbright"
     */
    public boolean isGlowing() {
        return glowing;
    }

    /**
     * @return Whether smooth shading should be used
     */
    public boolean isSmoothShading() {
        return smoothShading;
    }

    /**
     * @return The location of this texture
     */
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeometryModelTexture that = (GeometryModelTexture) o;
        return cache == that.cache && color == that.color && glowing == that.glowing && smoothShading == that.smoothShading && type == that.type && layer == that.layer && data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, layer, data, cache, color, glowing, smoothShading);
    }

    @Override
    public String toString() {
        return "GeometryModelTexture{" +
                "type=" + type +
                ", layer=" + layer +
                ", data='" + data + '\'' +
                ", cache=" + cache +
                ", color=" + color +
                ", glowing=" + glowing +
                ", smoothShading=" + smoothShading +
                '}';
    }

    /**
     * <p>A type of {@link GeometryModelTexture}.</p>
     *
     * @author Ocelot
     */
    public enum Type {

        UNKNOWN(location -> new ResourceLocation("missingno")),
        LOCATION(ResourceLocation::tryParse),
        ONLINE(location -> new ResourceLocation(Pollen.MOD_ID, "base32" + ONLINE_PATTERN.matcher(new Base32().encodeAsString(location.getBytes()).toLowerCase(Locale.ROOT)).replaceAll("_")));

        private final Function<String, ResourceLocation> locationGenerator;

        Type(Function<String, ResourceLocation> locationGenerator) {
            this.locationGenerator = locationGenerator;
        }

        /**
         * Fetches a type of texture by the specified name.
         *
         * @param name The name of the type of texture
         * @return The type by that name or {@link #UNKNOWN} if there is no type by that name
         */
        public static Type byName(String name) {
            for (Type type : values())
                if (type.name().equalsIgnoreCase(name))
                    return type;
            return UNKNOWN;
        }

        /**
         * Creates a new {@link ResourceLocation} based on the specified data string.
         *
         * @param data The data to convert
         * @return The new location for that data
         */
        @Nullable
        public ResourceLocation createLocation(String data) {
            return this.locationGenerator.apply(data);
        }
    }

    /**
     * <p>Supported render types for textures.</p>
     *
     * @author Ocelot
     */
    public enum TextureLayer {

        SOLID(() -> GeometryRenderTypes::getGeometrySolid),
        CUTOUT(() -> GeometryRenderTypes::getGeometryCutout),
        CUTOUT_CULL(() -> GeometryRenderTypes::getGeometryCutoutCull),
        TRANSLUCENT(() -> GeometryRenderTypes::getGeometryTranslucent),
        TRANSLUCENT_CULL(() -> GeometryRenderTypes::getGeometryTranslucentCull);

        private final Supplier<BiFunction<GeometryModelTexture, ResourceLocation, RenderType>> renderTypeGetter;

        TextureLayer(Supplier<BiFunction<GeometryModelTexture, ResourceLocation, RenderType>> renderTypeGetter) {
            this.renderTypeGetter = renderTypeGetter;
        }

        /**
         * Fetches a texture layer by the specified name.
         *
         * @param name The name of the texture layer
         * @return The texture layer by that name or {@link #CUTOUT} if there is no layer by that name
         */
        public static TextureLayer byName(String name) {
            for (TextureLayer layer : values())
                if (layer.name().equalsIgnoreCase(name))
                    return layer;
            return CUTOUT;
        }

        /**
         * Fetches the render type for the specified location.
         *
         * @param texture       The texture to use in the render type
         * @param atlasLocation The location of the texture atlas to use
         * @return The render type for this layer
         */
        @Environment(EnvType.CLIENT)
        public RenderType getRenderType(GeometryModelTexture texture, ResourceLocation atlasLocation) {
            return this.renderTypeGetter.get().apply(texture, atlasLocation);
        }
    }

    /**
     * <p>Constructs new geometry model textures.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Builder {

        private Type type;
        private TextureLayer layer;
        private String data;
        private boolean cache;
        private int color;
        private boolean glowing;
        private boolean smoothShading;

        private Builder() {
            this.type = Type.UNKNOWN;
            this.layer = TextureLayer.SOLID;
            this.data = "";
            this.cache = false;
            this.color = -1;
            this.glowing = false;
            this.smoothShading = false;
        }

        /**
         * Sets the texture to a local location to a file.
         *
         * @param texture The location of the texture
         */
        public Builder setTextureLocation(ResourceLocation texture) {
            this.type = Type.LOCATION;
            this.data = texture.toString();
            this.cache = false;
            return this;
        }

        /**
         * Sets the texture to be an online URL resource.
         *
         * @param url   The URL to the texture
         * @param cache Whether to cache the resource or download it each time
         */
        public Builder setTextureOnline(String url, boolean cache) {
            this.type = Type.ONLINE;
            this.data = url;
            this.cache = cache;
            return this;
        }

        /**
         * Sets the layer for the texture to render in
         *
         * @param layer The layer to draw in
         */
        public Builder setTextureLayer(TextureLayer layer) {
            this.layer = layer;
            return this;
        }

        /**
         * Sets the color to tint this texture to.
         *
         * @param color The new color
         */
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        /**
         * Sets whether this texture should render "fullbright".
         *
         * @param glowing Whether this should ignore lighting
         */
        public Builder setGlowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        /**
         * Sets whether this texture should have a smooth polygon shading instead of the usual flat.
         *
         * @param smoothShading Whether this should use smooth polygon shading
         */
        public Builder setSmoothShading(boolean smoothShading) {
            this.smoothShading = smoothShading;
            return this;
        }

        /**
         * @return A new texture with all the properties defined
         */
        public GeometryModelTexture build() {
            return new GeometryModelTexture(this.type, this.layer, this.data, this.cache, this.color, this.glowing, this.smoothShading);
        }
    }
}
