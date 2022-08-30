package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

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

    private Climate.Parameter parameter;

    Continentalness(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    public Climate.Parameter parameter() {
        return this.parameter;
    }

    public static Climate.Parameter span(Continentalness min, Continentalness max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}