package gg.moonflower.pollen.api.scheduler.v1;

import gg.moonflower.pollen.impl.scheduler.SchedulerImpl;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Automatically queues tasks into the main loop executor from a {@link ScheduledExecutorService}.
 * <p>As the scheduler is automatically shut down when it is no longer able to be used, manually trying to shut it down is unsupported.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface Scheduler extends ScheduledExecutorService {

    /**
     * Retrieves the scheduler for the specified side.
     *
     * @param client Whether to retrieve the client scheduler
     * @return The scheduler for that side
     */
    static Scheduler get(boolean client) {
        return SchedulerImpl.get(client);
    }
}
