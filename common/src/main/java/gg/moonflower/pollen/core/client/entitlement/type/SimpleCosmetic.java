package gg.moonflower.pollen.core.client.entitlement.type;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.screen.button.EntitlementEntry;
import gg.moonflower.pollen.core.client.screen.button.ToggleEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public abstract class SimpleCosmetic extends Entitlement {

    private boolean enabled;

    public SimpleCosmetic() {
        this.enabled = true;
    }

    @Override
    public void updateSettings(JsonObject settings) {
        if (settings.has("enabled"))
            this.enabled = GsonHelper.getAsBoolean(settings, "enabled");
    }

    @Override
    public JsonObject saveSettings() {
        JsonObject settings = new JsonObject();
        settings.addProperty("enabled", this.enabled);
        return settings;
    }

    @Override
    public void addEntries(Consumer<EntitlementEntry> entryConsumer) {
        entryConsumer.accept(new ToggleEntry(Component.literal("Enabled"), this, v -> this.enabled = v, this.enabled));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
