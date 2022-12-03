package gg.moonflower.pollen.pinwheel.core.client.geometry;

import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public class MolangCache {

    private final Map<MolangExpression, Float> cache;
    private MolangRuntime runtime;

    public MolangCache() {
        this.cache = new Object2FloatArrayMap<>();
        this.runtime = null;
    }

    public void setRuntime(MolangRuntime runtime) {
        this.runtime = runtime;
        this.cache.clear();
    }

    public float resolve(float thisValue, MolangExpression expression) {
        this.runtime.setThisValue(thisValue);
        return this.resolve(expression);
    }

    public float resolve(MolangExpression expression) {
        return this.cache.computeIfAbsent(expression, key -> expression.safeResolve(this.runtime));
    }

    public void clear() {
        this.cache.clear();
    }

    public MolangRuntime getRuntime() {
        return runtime;
    }
}
