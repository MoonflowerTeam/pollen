package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleMotionParametricComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysicsComponent;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class ParticleMotionParametricComponentImpl extends BedrockParticleComponentImpl implements BedrockParticlePhysicsComponent {

    private final ParticleMotionParametricComponent data;

    public ParticleMotionParametricComponentImpl(BedrockParticle particle, ParticleMotionParametricComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void physicsTick() {
        MolangEnvironment environment = this.particle.getEnvironment();
        Vector3dc emitterPos = this.particle.getEmitter().position();

        MolangExpression[] relativePos = this.data.relativePosition();
        if (relativePos != null) {
            double x = emitterPos.x() + relativePos[0].safeResolve(environment);
            double y = emitterPos.y() + relativePos[1].safeResolve(environment);
            double z = emitterPos.z() + relativePos[2].safeResolve(environment);
            this.particle.setPosition(x, y, z);
        }

        MolangExpression[] direction = this.data.direction();
        if (direction != null) {
            double dx = direction[0].safeResolve(environment);
            double dy = direction[1].safeResolve(environment);
            double dz = direction[2].safeResolve(environment);
            this.getPhysics().setDirection(dx, dy, dz);
        }

        this.particle.setRoll(this.data.rotation().safeResolve(environment));
    }
}
