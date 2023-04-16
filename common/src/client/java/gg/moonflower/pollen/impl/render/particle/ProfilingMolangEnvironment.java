package gg.moonflower.pollen.impl.render.particle;

import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import io.github.ocelot.molangcompiler.api.object.MolangObject;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class ProfilingMolangEnvironment implements MolangEnvironment {

    private final MolangEnvironment environment;
    private final Supplier<ProfilerFiller> profiler;

    public ProfilingMolangEnvironment(MolangEnvironment environment, Supplier<ProfilerFiller> profiler) {
        this.environment = environment;
        this.profiler = profiler;
    }

    @Override
    public void loadLibrary(String name, MolangObject object) {
        this.environment.loadLibrary(name, object);
    }

    @Override
    public void loadAlias(String name, MolangObject object) {
        this.environment.loadAlias(name, object);
    }

    @Override
    public void loadParameter(int index, MolangExpression expression) throws MolangException {
        this.environment.loadParameter(index, expression);
    }

    @Override
    public void clearParameters() throws MolangException {
        this.environment.clearParameters();
    }

    @Override
    public float getThis() throws MolangException {
        return this.environment.getThis();
    }

    @Override
    public MolangObject get(String name) throws MolangException {
        ProfilerFiller profiler = this.profiler.get();
        profiler.push("molang");
        MolangObject value = this.environment.get(name);
        profiler.pop();
        return value;
    }

    @Override
    public MolangExpression getParameter(int parameter) throws MolangException {
        return this.environment.getParameter(parameter);
    }

    @Override
    public boolean hasParameter(int parameter) throws MolangException {
        return this.environment.hasParameter(parameter);
    }

    @Override
    public void setThisValue(float thisValue) {
        this.environment.setThisValue(thisValue);
    }
}
