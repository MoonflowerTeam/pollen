package gg.moonflower.pollen.core.client.screen;

import gg.moonflower.pollen.core.client.screen.button.EntitlementEntry;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EntitlementButton extends AbstractButton {

    private final EntitlementEntry entry;

    public EntitlementButton(Component title, EntitlementEntry entry, int x, int y, int width) {
        super(x, y, width, 20, title);
        this.entry = entry;
    }

    @Override
    public void onPress() {
        this.entry.updateButton(this);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
