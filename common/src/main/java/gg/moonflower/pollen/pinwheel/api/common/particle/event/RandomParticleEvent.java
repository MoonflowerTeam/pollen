package gg.moonflower.pollen.pinwheel.api.common.particle.event;

import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.List;
import java.util.stream.IntStream;

public class RandomParticleEvent implements ParticleEvent {

    private final List<WeightedEvent> events;

    public RandomParticleEvent(ParticleEvent[] events, Weight[] weights) {
        if (events.length != weights.length)
            throw new IllegalArgumentException("Expected " + events.length + " weights, got " + weights.length);
        this.events = IntStream.range(0, events.length).mapToObj(i -> new WeightedEvent(events[i], weights[i])).toList();
    }

    @Override
    public void execute(Context context) {
        WeightedRandom.getRandomItem(context.getRandom(), this.events).ifPresent(event -> event.event.execute(context));
    }

    private record WeightedEvent(ParticleEvent event, Weight weight) implements WeightedEntry {

        @Override
        public Weight getWeight() {
            return weight;
        }
    }
}
