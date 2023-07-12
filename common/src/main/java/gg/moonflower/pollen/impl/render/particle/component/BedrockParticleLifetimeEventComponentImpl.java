package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleLifetimeEventComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BedrockParticleLifetimeEventComponentImpl implements BedrockParticleTickComponent, BedrockParticleListener {

    private final BedrockParticle particle;
    private final ParticleLifetimeEventComponent data;
    private int currentEvent;

    public BedrockParticleLifetimeEventComponentImpl(BedrockParticle particle, ParticleLifetimeEventComponent data) {
        this.particle = particle;
        this.data = data;
        this.currentEvent = 0;
    }

    @Override
    public void tick() {
        ParticleLifetimeEventComponent.TimelineEvent[] timelineEvents = this.data.timelineEvents();
        if (this.currentEvent >= timelineEvents.length) {
            return;
        }

        ParticleLifetimeEventComponent.TimelineEvent event = timelineEvents[this.currentEvent];
        float time = this.particle.getParticleAge();
        while (time >= event.time()) { // Execute all events that have been passed
            for (String e : event.events()) {
                this.particle.runEvent(e);
            }
            if (++this.currentEvent >= timelineEvents.length) {
                break;
            }
            event = timelineEvents[this.currentEvent];
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        for (String event : this.data.creationEvent()) {
            particle.runEvent(event);
        }
    }

    @Override
    public void onExpire(BedrockParticle particle) {
        for (String event : this.data.expirationEvent()) {
            particle.runEvent(event);
        }
    }
}
