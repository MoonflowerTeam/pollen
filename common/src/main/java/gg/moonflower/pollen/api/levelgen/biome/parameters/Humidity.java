package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

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

    public Climate.Parameter parameter() {
        return this.parameter;
    }

    public static Climate.Parameter span(Humidity min, Humidity max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}