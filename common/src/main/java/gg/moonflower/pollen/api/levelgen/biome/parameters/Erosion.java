package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

public enum Erosion {

    EROSION_0(Climate.Parameter.span(-1.0F, -0.78F)),
    EROSION_1(Climate.Parameter.span(-0.78F, -0.375F)),
    EROSION_2(Climate.Parameter.span(-0.375F, -0.2225F)),
    EROSION_3(Climate.Parameter.span(-0.2225F, 0.05F)),
    EROSION_4(Climate.Parameter.span(0.05F, 0.45F)),
    EROSION_5(Climate.Parameter.span(0.45F, 0.55F)),
    EROSION_6(Climate.Parameter.span(0.55F, 1.0F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Erosion(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    public Climate.Parameter parameter() {
        return this.parameter;
    }

    public static Climate.Parameter span(Erosion min, Erosion max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}