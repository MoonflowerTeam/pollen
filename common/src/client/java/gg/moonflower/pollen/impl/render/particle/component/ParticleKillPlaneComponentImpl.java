package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleKillPlaneComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class ParticleKillPlaneComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleListener {

    private final ParticleKillPlaneComponent data;

    public ParticleKillPlaneComponentImpl(BedrockParticle particle, ParticleKillPlaneComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void onMove(BedrockParticle particle, double dx, double dy, double dz) {
        BedrockParticleEmitter emitter = particle.getEmitter();
        Vector3dc pos = particle.position();
        Vector3dc emitterPos = emitter.position();

        boolean result = this.data.solve(pos.x() - dx - emitterPos.x(), pos.y() - dy - emitterPos.y(), pos.z() - dz - emitterPos.z(), pos.x() - emitterPos.x(), pos.y() - emitterPos.y(), pos.z() - emitterPos.z());
        if (result) {
            particle.expire();
        }
    }
}
