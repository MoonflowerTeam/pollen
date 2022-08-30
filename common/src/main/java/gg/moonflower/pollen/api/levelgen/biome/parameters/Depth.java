package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

/**
 * Represents the depth at which a biome will spawn.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
 * @since 1.5.0
 */
public enum Depth {

    SURFACE(Climate.Parameter.point(0.0F)),
    UNDERGROUND(Climate.Parameter.span(0.2F, 0.9F)),
    FLOOR(Climate.Parameter.point(1.0F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Depth(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return The actual parameter the depth value represents
     */
    public Climate.Parameter parameter() {
        return this.parameter;
    }

    /**
     * Creates a {@link Climate.Parameter} with a range between the specified minimum and maximum values.
     *
     * @param min The minimum depth value
     * @param max The maximum depth value
     * @return A climate parameter spanning the given values
     */
    public static Climate.Parameter span(Depth min, Depth max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}