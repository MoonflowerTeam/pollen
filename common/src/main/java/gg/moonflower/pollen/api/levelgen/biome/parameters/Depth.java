package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

public enum Depth {

    SURFACE(Climate.Parameter.point(0.0F)),
    UNDERGROUND(Climate.Parameter.span(0.2F, 0.9F)),
    FLOOR(Climate.Parameter.point(1.0F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Depth(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    public Climate.Parameter parameter() {
        return this.parameter;
    }

    public static Climate.Parameter span(Depth min, Depth max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}