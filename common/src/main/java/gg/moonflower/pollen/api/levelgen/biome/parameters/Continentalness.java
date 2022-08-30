package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

/**
 * Represents how far inland a biome will spawn.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
 * @since 1.5.0
 */
public enum Continentalness {

    MUSHROOM_FIELDS(Climate.Parameter.span(-1.2F, -1.05F)),
    DEEP_OCEAN(Climate.Parameter.span(-1.05F, -0.455F)),
    OCEAN(Climate.Parameter.span(-0.455F, -0.19F)),
    COAST(Climate.Parameter.span(-0.19F, -0.11F)),
    NEAR_INLAND(Climate.Parameter.span(-0.11F, 0.03F)),
    MID_INLAND(Climate.Parameter.span(0.03F, 0.3F)),
    FAR_INLAND(Climate.Parameter.span(0.3F, 1.0F)),
    INLAND(Climate.Parameter.span(-0.11F, 0.55F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Continentalness(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return The actual parameter the continentalness value represents
     */
    public Climate.Parameter parameter() {
        return this.parameter;
    }

    /**
     * Creates a {@link Climate.Parameter} with a range between the specified minimum and maximum values.
     *
     * @param min The minimum continentalness value
     * @param max The maximum continentalness value
     * @return A climate parameter spanning the given values
     */
    public static Climate.Parameter span(Continentalness min, Continentalness max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}