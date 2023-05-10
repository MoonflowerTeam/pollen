package gg.moonflower.pollen.impl.animation.runtime;

import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.entity.Entity;

public interface SidedAnimationRuntime {

    void addGlobal(MolangRuntime.Builder builder);

    void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client);
}
