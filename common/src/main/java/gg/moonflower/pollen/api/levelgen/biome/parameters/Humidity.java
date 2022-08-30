package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

/**
 * Represents the humidity at a given point in the world.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
 * @since 1.5.0
 */
public enum Humidity {

    ARID(Climate.Parameter.span(-1.0F, -0.35F)),
    DRY(Climate.Parameter.span(-0.35F, -0.1F)),
    NEUTRAL(Climate.Parameter.span(-0.1F, 0.1F)),
    WET(Climate.Parameter.span(0.1F, 0.3F)),
    HUMID(Climate.Parameter.span(0.3F, 1.0F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Humidity(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return The actual parameter the humidity value represents
     */
    public Climate.Parameter parameter() {
        return this.parameter;
    }

    /**
     * Creates a {@link Climate.Parameter} with a range between the specified minimum and maximum values.
     *
     * @param min The minimum humidity value
     * @param max The maximum humidity value
     * @return A climate parameter spanning the given values
     */
    public static Climate.Parameter span(Humidity min, Humidity max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}