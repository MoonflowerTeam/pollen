package gg.moonflower.pollen.core.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.screen.button.EntitlementEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntitlementsList extends ContainerObjectSelectionList<EntitlementsList.Entry> {

    public EntitlementsList(Minecraft arg, int i, int j, int k, int l, int m) {
        super(arg, i, j, k, l, m);
        this.centerListVertically = false;
        this.setRenderBackground(arg.level == null);
    }

    private static AbstractWidget createButton(Screen parent, Entitlement entitlement, int x) {
        return new Button(x, 0, 150, 20, entitlement.getDisplayName(), button -> Minecraft.getInstance().setScreen(new ConfigureEntitlementScreen(parent, entitlement)));
    }

    public void add(Screen parent, Entitlement... entitlements) {
        for (int i = 0; i < entitlements.length; i += 2) {
            this.addEntry(EntitlementsList.Entry.small(parent, this.width, entitlements[i], i < entitlements.length - 1 ? entitlements[i + 1] : null));
        }
    }

    public void addEntries(EntitlementEntry... entitlements) {
        for (int i = 0; i < entitlements.length; i += 2) {
            this.addEntry(EntitlementsList.Entry.small(this.width, entitlements[i], i < entitlements.length - 1 ? entitlements[i + 1] : null));
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    public Optional<AbstractWidget> getMouseOver(double mouseX, double mouseY) {
        for (EntitlementsList.Entry lv : this.children()) {
            for (AbstractWidget lv2 : lv.children) {
                if (lv2.isMouseOver(mouseX, mouseY)) {
                    return Optional.of(lv2);
                }
            }
        }

        return Optional.empty();
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<EntitlementsList.Entry> {

        private final List<AbstractWidget> children;

        private Entry(List<AbstractWidget> list) {
            this.children = list;
        }

        public static EntitlementsList.Entry small(Screen parent, int guiWidth, Entitlement leftEntitlement, @Nullable Entitlement rightEntitlement) {
            AbstractWidget lv = createButton(parent, leftEntitlement, guiWidth / 2 - 155);
            return rightEntitlement == null ? new EntitlementsList.Entry(ImmutableList.of(lv)) : new EntitlementsList.Entry(ImmutableList.of(lv, createButton(parent, rightEntitlement, guiWidth / 2 - 155 + 160)));
        }

        public static EntitlementsList.Entry small(int guiWidth, EntitlementEntry left, @Nullable EntitlementEntry right) {
            AbstractWidget lv = left.createButton(guiWidth / 2 - 155, 0, 150);
            return right == null ? new EntitlementsList.Entry(ImmutableList.of(lv)) : new EntitlementsList.Entry(ImmutableList.of(lv, right.createButton(guiWidth / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(PoseStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            this.children.forEach(widget -> {
                widget.y = top;
                widget.render(matrixStack, mouseX, mouseY, partialTicks);
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
