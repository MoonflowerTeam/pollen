package gg.moonflower.pollen.api.event.events.registry;

import com.mojang.brigadier.CommandDispatcher;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;

/**
 * Registers commands to the {@link CommandDispatcher} when ready.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface CommandRegistryEvent {

    PollinatedEvent<CommandRegistryEvent> EVENT = EventRegistry.create(CommandRegistryEvent.class, events -> (dispatcher, registryAccess, selection) -> {
        for (CommandRegistryEvent event : events)
            event.registerCommands(dispatcher, registryAccess, selection);
    });

    /**
     * Called to add commands to the dispatcher.
     *
     * @param dispatcher The dispatcher instance. This is used to physically register the commands
     * @param context The command build context
     * @param selection  The environment for what commands to register
     */
    void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection);
}
