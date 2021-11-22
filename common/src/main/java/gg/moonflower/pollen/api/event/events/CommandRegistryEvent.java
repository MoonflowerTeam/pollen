package gg.moonflower.pollen.api.event.events;

import com.mojang.brigadier.CommandDispatcher;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Registers commands to the {@link CommandDispatcher} when ready.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class CommandRegistryEvent extends PollinatedEvent {

    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final Commands.CommandSelection selection;

    public CommandRegistryEvent(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment) {
        this.dispatcher = dispatcher;
        this.selection = environment;
    }

    /**
     * @return The dispatcher instance. This is used to physically register the commands
     */
    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }

    /**
     * @return The environment for what commands to register
     */
    public Commands.CommandSelection getSelection() {
        return selection;
    }
}
