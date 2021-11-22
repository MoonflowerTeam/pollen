package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.EventListener;
import gg.moonflower.pollen.api.event.events.CommandRegistryEvent;
import gg.moonflower.pollen.core.command.ConfigCommand;
import net.minecraft.commands.Commands;

public class FabricEvents {

    @EventListener
    public static void onEvent(CommandRegistryEvent event) {
        ConfigCommand.register(event.getDispatcher(), event.getSelection() == Commands.CommandSelection.DEDICATED);
    }
}
