package gg.moonflower.pollen.api.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class ColorArgumentType implements ArgumentType<Number> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123", "0xffffff");

    private static final SimpleCommandExceptionType INVALID_TYPE = new SimpleCommandExceptionType(Component.translatable("argument." + Pollen.MOD_ID + ".color.invalid"));

    public static ColorArgumentType color() {
        return new ColorArgumentType();
    }

    public static int getColor(CommandContext<?> context, String name) {
        return context.getArgument(name, Number.class).intValue();
    }

    @Override
    public Number parse(final StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        if (!NumberUtils.isCreatable(input))
            throw INVALID_TYPE.create();

        return NumberUtils.createNumber(input);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
