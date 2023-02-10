package gg.moonflower.pollen.api.command;

import gg.moonflower.pollen.api.command.argument.ColorArgumentType;
import gg.moonflower.pollen.api.command.argument.EnumArgument;
import gg.moonflower.pollen.api.command.argument.TimeArgumentType;
import gg.moonflower.pollen.api.registry.PollinatedCommandArgumentRegistry;
import gg.moonflower.pollen.api.registry.RegistryValue;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public final class PollenArgumentTypes {

    public static final PollinatedCommandArgumentRegistry ARGUMENT_TYPES = PollinatedCommandArgumentRegistry.createCommandArgument(Pollen.MOD_ID);

    public static final RegistryValue<SingletonArgumentInfo<ColorArgumentType>> COLOR = ARGUMENT_TYPES.register("color", ColorArgumentType.class, SingletonArgumentInfo.contextFree(ColorArgumentType::color));
    public static final RegistryValue<TimeArgumentType.Serializer> TIME = ARGUMENT_TYPES.register("time", TimeArgumentType.class, new TimeArgumentType.Serializer());
    public static final RegistryValue<EnumArgument.Serializer> ENUM = ARGUMENT_TYPES.register("enum", EnumArgument.class, new EnumArgument.Serializer());

    private PollenArgumentTypes() {
    }
}
