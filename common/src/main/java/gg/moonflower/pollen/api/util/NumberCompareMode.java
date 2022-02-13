package gg.moonflower.pollen.api.util;

import com.google.gson.JsonSyntaxException;

import java.util.Locale;
import java.util.function.BiPredicate;

/**
 * Modes of comparing two different numbers.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public enum NumberCompareMode implements BiPredicate<Number, Number> {

    GREATER_THAN(">", (t, u) -> t.doubleValue() > u.doubleValue()),
    LESS_THAN("<", (t, u) -> t.doubleValue() < u.doubleValue()),
    GREATER_THAN_OR_EQUAL(">=", (t, u) -> t.doubleValue() >= u.doubleValue()),
    LESS_THAN_OR_EQUAL("<=", (t, u) -> t.doubleValue() <= u.doubleValue()),
    EQUAL("=", (t, u) -> t.doubleValue() == u.doubleValue());

    private final String symbol;
    private final BiPredicate<Number, Number> comparator;

    NumberCompareMode(String symbol, BiPredicate<Number, Number> comparator) {
        this.symbol = symbol;
        this.comparator = comparator;
    }

    /**
     * @return The symbol representing this operation
     */
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean test(Number t, Number u) {
        return this.comparator.test(t, u);
    }

    /**
     * Retrieves this mode by name or symbol.
     *
     * @param name The name to check against
     * @return The compare mode found
     * @throws JsonSyntaxException If the compare mode could not be found
     */
    public static NumberCompareMode byName(String name) {
        for (NumberCompareMode mode : values())
            if (mode.name().toLowerCase(Locale.ROOT).equals(name) || mode.symbol.equals(name))
                return mode;
        throw new JsonSyntaxException("Unknown compare mode: " + name);
    }
}
