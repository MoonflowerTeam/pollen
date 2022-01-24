package gg.moonflower.pollen.core.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.screen.button.EntitlementEntry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;

public class ConfigureEntitlementScreen extends Screen {

    private final Screen lastScreen;
    private final Entitlement entitlement;
    private final List<EntitlementEntry> entries;
    private EntitlementsList list;

    public ConfigureEntitlementScreen(Screen screen, Entitlement entitlement) {
        super(entitlement.getDisplayName());
        this.lastScreen = screen;
        this.entitlement = entitlement;
        this.entries = new ArrayList<>();
        this.entitlement.addEntries(this.entries::add);
    }

    @Override
    protected void init() {
        this.list = new EntitlementsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addEntries(this.entries.toArray(new EntitlementEntry[0]));
        this.addWidget(this.list);

        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, mouseX, mouseY, partialTicks);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void removed() {
        EntitlementManager.updateEntitlementSettings(this.minecraft.getUser().getGameProfile().getId(), this.entitlement.getRegistryName().getPath(), e -> this.entries.forEach(EntitlementEntry::save));
    }
}
