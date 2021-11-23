package gg.moonflower.pollen.api.command.argument;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.LongFunction;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class TimeArgumentType implements ArgumentType<Long> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0t", "12s", "98h", "4d");
    private static final DynamicCommandExceptionType UNKNOWN_UNIT = new DynamicCommandExceptionType(arg -> new TranslatableComponent("argument." + Pollen.MOD_ID + ".time.unknown_unit", arg));
    private static final Dynamic2CommandExceptionType TIME_TOO_LOW = new Dynamic2CommandExceptionType((result, min) -> new TranslatableComponent("argument.time." + Pollen.MOD_ID + ".low", min, result));
    private static final Dynamic2CommandExceptionType TIME_TOO_HIGH = new Dynamic2CommandExceptionType((result, max) -> new TranslatableComponent("argument.time." + Pollen.MOD_ID + ".big", max, result));
    private static final ImmutableMap<String, Pair<Component, LongFunction<Long>>> TIME_UNITS;

    static {
        ImmutableMap.Builder<String, Pair<Component, LongFunction<Long>>> builder = ImmutableMap.builder();
        builder.put("ns", Pair.of(new TextComponent("Nanoseconds"), time -> time));
        builder.put("us", Pair.of(new TextComponent("Microseconds"), time -> time * 1_000L));
        builder.put("ms", Pair.of(new TextComponent("Milliseconds"), time -> time * 1_000_000L));
        builder.put("s", Pair.of(new TextComponent("Seconds"), time -> time * 1_000_000_000L));
        builder.put("m", Pair.of(new TextComponent("Minutes"), time -> time * 60_000_000_000L));
        builder.put("h", Pair.of(new TextComponent("Hours"), time -> time * 3_600_000_000_000L));
        builder.put("d", Pair.of(new TextComponent("Days"), time -> time * 86_400_000_000_000L));
        builder.put("t", Pair.of(new TextComponent("Game Ticks"), time -> time * 50_000_000L));
        TIME_UNITS = builder.build();
    }

    private final long min;
    private final long max;
    private final TimeUnit unit;

    private TimeArgumentType(long min, long max, TimeUnit unit) {
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public static TimeArgumentType time() {
        return new TimeArgumentType(Long.MIN_VALUE, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    public static TimeArgumentType time(long min, long max, TimeUnit unit) {
        return new TimeArgumentType(min, max, unit);
    }

    /**
     * Fetches a time argument in nanoseconds,
     *
     * @param context The context of the command
     * @param name    The name of the argument
     * @return The time specified in nanoseconds
     */
    public static long getTime(CommandContext<?> context, String name) {
        return context.getArgument(name, Number.class).longValue();
    }

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        long time = reader.readLong();
        String unit = reader.readUnquotedString();
        if (!TIME_UNITS.containsKey(unit))
            throw UNKNOWN_UNIT.createWithContext(reader, unit);

        Component unitName = TIME_UNITS.get(unit).getLeft();
        long nanos = TIME_UNITS.get(unit).getRight().apply(time);
        long localNanos = this.unit.convert(nanos, TimeUnit.NANOSECONDS);
        if (localNanos < this.min)
            throw TIME_TOO_LOW.createWithContext(reader, new TextComponent(this.min + " ").append(unitName), new TextComponent(localNanos + " ").append(unitName));
        if (localNanos > this.max)
            throw TIME_TOO_HIGH.createWithContext(reader, new TextComponent(this.max + " ").append(unitName), new TextComponent(localNanos + " ").append(unitName));

        return nanos;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public long getMinimum() {
        return min;
    }

    public long getMaximum() {
        return max;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getRemaining());
        while (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
            reader.skip();
        }
        TIME_UNITS.keySet().stream().filter(unit -> reader.getRemaining().isEmpty() || unit.startsWith(reader.getRemaining())).forEach(unit -> builder.suggest(builder.getInput().substring(builder.getStart(), builder.getStart() + reader.getCursor()) + unit, TIME_UNITS.get(unit).getLeft()));
        return builder.buildFuture();
    }

    @ApiStatus.Internal
    public static class Serializer implements ArgumentSerializer<TimeArgumentType> {
        @Override
        public void serializeToNetwork(TimeArgumentType argument, FriendlyByteBuf buf) {
            boolean flag = argument.getMinimum() != Long.MIN_VALUE;
            boolean flag1 = argument.getMaximum() != Long.MAX_VALUE;
            buf.writeByte(BrigadierArgumentSerializers.createNumberFlags(flag, flag1));
            if (flag)
                buf.writeLong(argument.getMinimum());
            if (flag1)
                buf.writeLong(argument.getMaximum());
            buf.writeUtf(argument.unit.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public TimeArgumentType deserializeFromNetwork(FriendlyByteBuf buf) {
            byte b0 = buf.readByte();
            long i = BrigadierArgumentSerializers.numberHasMin(b0) ? buf.readLong() : Long.MIN_VALUE;
            long j = BrigadierArgumentSerializers.numberHasMax(b0) ? buf.readLong() : Long.MAX_VALUE;
            TimeUnit unit = TimeUnit.valueOf(buf.readUtf().toUpperCase(Locale.ROOT));
            return TimeArgumentType.time(i, j, unit);
        }

        @Override
        public void serializeToJson(TimeArgumentType argument, JsonObject json) {
            if (argument.getMinimum() != Long.MIN_VALUE)
                json.addProperty("min", argument.getMinimum());
            if (argument.getMaximum() != Long.MAX_VALUE)
                json.addProperty("max", argument.getMaximum());
            json.addProperty("unit", argument.getUnit().name().toLowerCase(Locale.ROOT));
        }
    }
}
