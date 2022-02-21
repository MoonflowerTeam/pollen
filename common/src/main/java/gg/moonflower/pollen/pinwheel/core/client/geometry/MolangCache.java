package gg.moonflower.pollen.pinwheel.core.client.geometry;

import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public class MolangCache {

    private final Map<Float, MolangEnvironment> environmentCache;
    private final Map<MolangExpression, Float> cache;

    public MolangCache() {
        this.environmentCache = new Float2ObjectArrayMap<>();
        this.cache = new Object2FloatArrayMap<>();
    }

    public MolangEnvironment get(MolangRuntime.Builder builder, float thisValue) {
        return this.environmentCache.computeIfAbsent(thisValue, builder::create);
    }

    public float resolve(MolangRuntime.Builder builder, float thisValue, MolangExpression expression) {
        return this.resolve(this.environmentCache.computeIfAbsent(thisValue, builder::create), expression);
    }

    public float resolve(MolangEnvironment environment, MolangExpression expression) {
        return this.cache.computeIfAbsent(expression, key -> expression.safeResolve(environment));
    }

    public void clear() {
        this.environmentCache.clear();
        this.cache.clear();
    }
}
