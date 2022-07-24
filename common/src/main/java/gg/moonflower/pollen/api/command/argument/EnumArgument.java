package gg.moonflower.pollen.api.command.argument;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class EnumArgument implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("creative", "survival", "loot_chest", "accessory");
    private static final SimpleCommandExceptionType INVALID_TYPE = new SimpleCommandExceptionType(Component.translatable("argument." + Pollen.MOD_ID + ".enum.invalid"));

    private final String[] values;

    private EnumArgument(String[] values) {
        this.values = values;
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumArgument enumValues(Enum<T>... values) {
        return new EnumArgument(Arrays.stream(values).map(value -> value.name().toLowerCase(Locale.ROOT)).toArray(String[]::new));
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, CommandContext<?> context, String name) throws CommandSyntaxException {
        String value = context.getArgument(name, String.class);
        for (T enumValue : clazz.getEnumConstants())
            if (enumValue.name().equalsIgnoreCase(value))
                return enumValue;
        throw INVALID_TYPE.create();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        for (String value : this.values)
            if (value.equalsIgnoreCase(input))
                return value;
        throw INVALID_TYPE.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Stream.of(this.values), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @ApiStatus.Internal
    public static class Serializer implements ArgumentTypeInfo<EnumArgument, Serializer.Template> {
        @Override
        public void serializeToNetwork(Template argument, FriendlyByteBuf buf) {
            buf.writeVarInt(argument.values.length);
            for (String value : argument.values)
                buf.writeUtf(value);
        }

        @Override
        public Template deserializeFromNetwork(FriendlyByteBuf buf) {
            int length = buf.readVarInt();
            String[] values = new String[length];
            for (int i = 0; i < values.length; i++)
                values[i] = buf.readUtf();
            return new Serializer.Template(values);
        }

        @Override
        public void serializeToJson(Template argument, JsonObject json) {
            JsonArray valuesJson = new JsonArray();
            for (String value : argument.values)
                valuesJson.add(value);
            json.add("values", valuesJson);
        }

        @Override
        public Template unpack(EnumArgument argumentType) {
            return new Template(argumentType.values);
        }

        public final class Template implements ArgumentTypeInfo.Template<EnumArgument> {

            private final String[] values;

            public Template(String[] values) {
                this.values = values;
            }

            @Override
            public EnumArgument instantiate(CommandBuildContext commandBuildContext) {
                return new EnumArgument(this.values);
            }

            @Override
            public ArgumentTypeInfo<EnumArgument, ?> type() {
                return Serializer.this;
            }
        }
    }
}
