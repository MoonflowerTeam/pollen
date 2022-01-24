package gg.moonflower.pollen.core.client.screen.button;

import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.screen.EntitlementButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class ToggleEntry extends EntitlementEntry {

    private final Consumer<Boolean> setter;
    private boolean value;

    public ToggleEntry(Component caption, Entitlement entitlement, Consumer<Boolean> setter, boolean value) {
        super(caption, entitlement);
        this.setter = setter;
        this.value = value;
    }

    private Component getDisplay() {
        return CommonComponents.optionStatus(this.getCaption(), this.value);
    }

    @Override
    public AbstractWidget createButton(int x, int y, int width) {
        return new EntitlementButton(this.getDisplay(), this, x, y, width);
    }

    @Override
    public void updateButton(AbstractWidget widget) {
        this.value = !this.value;
        widget.setMessage(this.getDisplay());
    }

    @Override
    public void save() {
        this.setter.accept(this.value);
    }
}
