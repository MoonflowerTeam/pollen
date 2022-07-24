package gg.moonflower.pollen.core.client.screen.button;

import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.screen.EntitlementButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.Internal
public class ArrayEntry<T> extends EntitlementEntry {

    private final Consumer<T> setter;
    private final T[] values;
    private int index;
    private Function<T, String> displayGenerator;

    @SafeVarargs
    public ArrayEntry(Component caption, Entitlement entitlement, Consumer<T> setter, T value, T... values) {
        super(caption, entitlement);
        this.setter = setter;
        this.values = values;
        this.index = Math.max(0, ArrayUtils.indexOf(this.values, value));
        this.setDisplayGenerator(null);
    }

    private Component getDisplay() {
        return this.genericValueLabel(Component.literal(this.displayGenerator.apply(this.values[this.index])));
    }

    @Override
    public AbstractWidget createButton(int x, int y, int width) {
        return new EntitlementButton(this.getDisplay(), this, x, y, width);
    }

    @Override
    public void updateButton(AbstractWidget widget) {
        if (!Screen.hasAltDown()) {
            this.showNext();
        } else {
            this.showPrevious();
        }

        widget.setMessage(this.getDisplay());
    }

    @Override
    public void save() {
        this.setter.accept(this.values[this.index]);
    }

    private void showNext() {
        this.index++;
        if (this.index >= this.values.length)
            this.index = 0;
    }

    private void showPrevious() {
        this.index--;
        if (this.index < 0)
            this.index = this.values.length - 1;
    }

    public ArrayEntry<T> setDisplayGenerator(@Nullable Function<T, String> displayGenerator) {
        this.displayGenerator = displayGenerator != null ? displayGenerator : String::valueOf;
        return this;
    }
}
