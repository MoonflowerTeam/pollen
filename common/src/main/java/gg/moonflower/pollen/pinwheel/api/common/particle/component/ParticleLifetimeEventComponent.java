package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component that listens for lifecycle events.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleLifetimeEventComponent implements CustomParticleComponent, CustomParticleListener {

    private final String[] creationEvent;
    private final String[] expirationEvent;
    private final TimelineEvent[] timelineEvents;
    private final Map<CustomParticle, Integer> currentEvent;

    public ParticleLifetimeEventComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        this.creationEvent = CustomParticleComponent.getEvents(jsonObject, "creation_event");
        this.expirationEvent = CustomParticleComponent.getEvents(jsonObject, "expiration_event");

        if (jsonObject.has("timeline")) {
            JsonObject timelineJson = GsonHelper.getAsJsonObject(jsonObject, "timeline");
            List<TimelineEvent> events = new ArrayList<>(timelineJson.entrySet().size());
            for (Map.Entry<String, JsonElement> entry : timelineJson.entrySet()) {
                try {
                    events.add(new TimelineEvent(Float.parseFloat(entry.getKey()), CustomParticleComponent.parseEvents(entry.getValue(), entry.getKey())));
                } catch (Exception e) {
                    throw new JsonSyntaxException("Failed to parse " + entry.getKey() + " in timeline", e);
                }
            }
            events.sort((a, b) -> Float.compare(a.time(), b.time()));
            this.timelineEvents = events.toArray(TimelineEvent[]::new);
        } else {
            this.timelineEvents = new TimelineEvent[0];
        }
        this.currentEvent = new HashMap<>();
    }

    @Override
    public void tick(CustomParticle particle) {
    }

    @Override
    public void onCreate(CustomParticle particle) {
        for (String event : this.creationEvent)
            particle.runEvent(event);
    }

    @Override
    public void onExpire(CustomParticle particle) {
        for (String event : this.expirationEvent)
            particle.runEvent(event);
        this.currentEvent.remove(particle);
    }

    @Override
    public void onTimeline(CustomParticle particle, float time) {
        int currentEvent = this.currentEvent.getOrDefault(particle, 0);
        if (currentEvent >= this.timelineEvents.length)
            return;
        TimelineEvent event = this.timelineEvents[currentEvent];
        while (time >= event.time()) { // Execute all events that have been passed
            for (String e : event.events)
                particle.runEvent(e);
            if (++currentEvent >= this.timelineEvents.length)
                break;
            event = this.timelineEvents[currentEvent];
        }
        this.currentEvent.put(particle, currentEvent);
    }

    private record TimelineEvent(float time, String[] events) {
    }
}
