package gg.moonflower.pollen.core.client.screen.button;

import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public abstract class EntitlementEntry {

    private final Component caption;
    private final Entitlement entitlement;
    private List<FormattedCharSequence> toolTip;

    protected EntitlementEntry(Component caption, Entitlement entitlement) {
        this.caption = caption;
        this.entitlement = entitlement;
        this.toolTip = null;
    }

    public abstract AbstractWidget createButton(int x, int y, int width);

    public abstract void updateButton(AbstractWidget widget);

    public abstract void save();

    protected Entitlement getEntitlement() {
        return entitlement;
    }

    protected Component getCaption() {
        return this.caption;
    }

    public void setTooltip(@Nullable List<FormattedCharSequence> toolTip) {
        this.toolTip = toolTip;
    }

    public Optional<List<FormattedCharSequence>> getTooltip() {
        return Optional.ofNullable(this.toolTip);
    }

    protected Component pixelValueLabel(int value) {
        return Component.translatable("options.pixel_value", this.getCaption(), value);
    }

    protected Component percentValueLabel(double percentage) {
        return Component.translatable("options.percent_value", this.getCaption(), (int) (percentage * 100.0));
    }

    protected Component percentAddValueLabel(int doubleValue) {
        return Component.translatable("options.percent_add_value", this.getCaption(), doubleValue);
    }

    protected Component genericValueLabel(Component valueMessage) {
        return Component.translatable("options.generic_value", this.getCaption(), valueMessage);
    }

    protected Component genericValueLabel(int value) {
        return this.genericValueLabel(Component.literal(Integer.toString(value)));
    }
}
