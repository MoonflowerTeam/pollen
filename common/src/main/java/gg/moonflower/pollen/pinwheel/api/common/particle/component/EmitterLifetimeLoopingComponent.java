package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;

/**
 * Component that spawns particles during the active time.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterLifetimeLoopingComponent implements CustomParticleEmitterComponent, CustomParticleListener, CustomEmitterListener {

    private final MolangExpression activeTime;
    private final MolangExpression sleepTime;
    private int activeTimeEval;
    private int sleepTimer;

    public EmitterLifetimeLoopingComponent(JsonElement json) throws JsonParseException {
        this.activeTime = JSONTupleParser.getExpression(json.getAsJsonObject(), "active_time", () -> MolangExpression.of(10));
        this.sleepTime = JSONTupleParser.getExpression(json.getAsJsonObject(), "sleep_time", () -> MolangExpression.ZERO);
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
        emitter.setLifetime(this.activeTimeEval);
        if (!emitter.isActive()) {
            if (this.sleepTimer > 0) { // Wait for sleep to complete
                this.sleepTimer--;
            } else {
                emitter.restart();
            }
        }
    }

    @Override
    public void onCreate(CustomParticle particle) {
        MolangEnvironment runtime = particle.getRuntime();
        this.activeTimeEval = (int) this.activeTime.safeResolve(runtime);
        this.sleepTimer = (int) this.sleepTime.safeResolve(runtime);
    }
}
