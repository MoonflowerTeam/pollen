package gg.moonflower.pollen.api.registry;

import com.mojang.brigadier.arguments.ArgumentType;
import gg.moonflower.pollen.core.mixin.ArgumentTypeInfosAccessor;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

public class PollinatedCommandArgumentRegistry extends WrapperPollinatedRegistry<ArgumentTypeInfo<?, ?>> {

    PollinatedCommandArgumentRegistry(PollinatedRegistry<ArgumentTypeInfo<?, ?>> commandArgumentRegistry) {
        super(commandArgumentRegistry);
    }

    /**
     * Registers a command argument type.
     *
     * @param id         The id of the argument type
     * @param clazz      The class of the argument type
     * @param serializer The serializer for the argument type
     * @param <R>        The argument type being registered
     * @return The registered block
     */
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, R extends ArgumentTypeInfo<A, T>> RegistryValue<R> register(String id, Class<? extends A> clazz, R serializer) {
        RegistryValue<R> register = this.register(id, () -> serializer);
        ArgumentTypeInfosAccessor.getByClass().put(clazz, serializer);
        return register;
    }
}
