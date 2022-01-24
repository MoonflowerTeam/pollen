package gg.moonflower.pollen.core.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntitlementListScreen extends Screen {

    private final Screen lastScreen;
    private EntitlementsList list;

    public EntitlementListScreen(Screen screen) {
        super(new TranslatableComponent("options." + Pollen.MOD_ID + ".entitlementList.title"));
        this.lastScreen = screen;
    }

    @Override
    protected void init() {
        this.list = new EntitlementsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        EntitlementManager.getEntitlementsFuture(this.minecraft.getUser().getGameProfile().getId()).thenAcceptAsync(map -> this.list.add(this, map.values().stream().filter(Entitlement::hasSettings).toArray(Entitlement[]::new)), this.minecraft);
        this.children.add(this.list);

        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
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

    @Nullable
    public static List<FormattedCharSequence> tooltipAt(EntitlementsList arg, int x, int y) {
        Optional<AbstractWidget> optional = arg.getMouseOver(x, y);
        if (optional.isPresent() && optional.get() instanceof TooltipAccessor)
            return ((TooltipAccessor) optional.get()).getTooltip().orElse(null);
        return null;
    }
}
