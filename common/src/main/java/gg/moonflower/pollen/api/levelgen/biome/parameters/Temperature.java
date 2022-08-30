package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

/**
 * Represents the temperature at a given point in the world.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
 * @since 1.5.0
 */
public enum Temperature {

    ICY(Climate.Parameter.span(-1.0F, -0.45F)),
    COOL(Climate.Parameter.span(-0.45F, -0.15F)),
    NEUTRAL(Climate.Parameter.span(-0.15F, 0.2F)),
    WARM(Climate.Parameter.span(0.2F, 0.55F)),
    HOT(Climate.Parameter.span(0.55F, 1.0F)),
    FROZEN(Climate.Parameter.span(-1.0F, -0.45F)),
    UNFROZEN(Climate.Parameter.span(-0.45F, 1.0F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Temperature(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return The actual parameter the temperature value represents
     */
    public Climate.Parameter parameter() {
        return this.parameter;
    }

    /**
     * Creates a {@link Climate.Parameter} with a range between the specified minimum and maximum values.
     *
     * @param min The minimum temperature value
     * @param max The maximum temperature value
     * @return A climate parameter spanning the given values
     */
    public static Climate.Parameter span(Temperature min, Temperature max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}