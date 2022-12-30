package gg.moonflower.pollen.api.events.v1.registry;

import com.mojang.brigadier.CommandDispatcher;
import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Registers commands to the {@link CommandDispatcher} when ready.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface CommandRegistryEvent {

    PollinatedEvent<CommandRegistryEvent> EVENT = EventRegistry.create(CommandRegistryEvent.class, events -> (dispatcher, selection) -> {
        for (CommandRegistryEvent event : events)
            event.registerCommands(dispatcher, selection);
    });

    /**
     * Called to add commands to the dispatcher.
     *
     * @param dispatcher The dispatcher instance. This is used to physically register the commands
     * @param selection  The environment for what commands to register
     */
    void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection);
}
