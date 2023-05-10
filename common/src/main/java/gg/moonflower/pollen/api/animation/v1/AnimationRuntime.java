package gg.moonflower.pollen.api.animation.v1;

import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.entity.Entity;

public interface AnimationRuntime {

    static void addClient(MolangRuntime.Builder builder) {

    }

    static void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client) {

    }
}
