package gg.moonflower.pollen.impl.animation.runtime;

import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangJavaFunction;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonAnimationRuntime implements SidedAnimationRuntime {

    private static final Logger LOGGER = LoggerFactory.getLogger("MoLang");

    private static final MolangJavaFunction APPROX_EQUALS = context ->
    {
        if (context.getParameters() <= 1) {
            return 1.0F;
        }

        float first = context.resolve(0);
        for (int i = 1; i < context.getParameters(); i++) {
            if (Math.abs(context.resolve(i) - first) > 1.0E-6) {
                return 0.0F;
            }
        }
        return 1.0F;
    };
    private static final MolangJavaFunction LOG = context ->
    {
        int size = context.getParameters();
        if (size == 1) {
            float value = context.resolve(0);
            LOGGER.info(String.valueOf(value));
            return value;
        }

        String[] values = new String[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = Float.toString(context.resolve(i));
        }
        LOGGER.info(String.join(", ", values));
        return 0.0F;
    };

    @Override
    public void addGlobal(MolangRuntime.Builder builder) {
        builder.setQuery("approx_eq", -1, APPROX_EQUALS);
        builder.setQuery("log", -1, LOG);
    }

    @Override
    public void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client) {

    }
}
