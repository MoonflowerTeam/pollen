package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

/**
 * Fired when a projectile impacts something.
 *
 * @author ebo2022
 * @since
 */
@FunctionalInterface
public interface ProjectileImpactEvent {

    PollinatedEvent<ProjectileImpactEvent> EVENT = EventRegistry.createCancellable(ProjectileImpactEvent.class);


    /**
     * Called when a projectile is going to make an impact.
     *
     * @param projectile The projectile that is impacting something
     * @param ray        The {@link HitResult} of the projectile
     * @return Whether the impact should continue being processed
     */
    boolean onProjectileImpact(Projectile projectile, HitResult ray);
}
