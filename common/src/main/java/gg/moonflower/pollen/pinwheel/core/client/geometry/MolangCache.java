package gg.moonflower.pollen.pinwheel.core.client.geometry;

import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import io.github.ocelot.molangcompiler.api.object.MolangObject;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public class MolangCache implements MolangEnvironment {

    private final Map<MolangExpression, Float> cache;
    private MolangRuntime runtime;

    public MolangCache() {
        this.cache = new Object2FloatArrayMap<>();
        this.runtime = null;
        this.clear();
    }

    public void setRuntime(MolangRuntime runtime) {
        this.runtime = runtime;
        this.clear();
    }

    @Override
    public void loadLibrary(String name, MolangObject object) {
        this.runtime.loadLibrary(name, object);
    }

    @Override
    public void loadAlias(String name, MolangObject object) {
        this.runtime.loadAlias(name, object);
    }

    @Override
    public void loadParameter(int index, MolangExpression expression) {
        this.runtime.loadParameter(index, expression);
    }

    @Override
    public void clearParameters() {
        this.runtime.clearParameters();
    }

    @Override
    public float getThis() {
        return this.runtime.getThis();
    }

    @Override
    public MolangObject get(String name) throws MolangException {
        return this.runtime.get(name);
    }

    @Override
    public MolangExpression getParameter(int parameter) {
        return this.runtime.getParameter(parameter);
    }

    @Override
    public boolean hasParameter(int parameter) {
        return false;
    }

    @Override
    public void setThisValue(float thisValue) {
        this.runtime.setThisValue(thisValue);
    }

    @Override
    public float resolve(MolangExpression expression) {
        return this.cache.computeIfAbsent(expression, key -> expression.safeResolve(this.runtime));
    }

    public void clear() {
        this.cache.clear();
        this.cache.put(MolangExpression.ZERO, 0F);
    }

    public MolangRuntime getRuntime() {
        return runtime;
    }
}
