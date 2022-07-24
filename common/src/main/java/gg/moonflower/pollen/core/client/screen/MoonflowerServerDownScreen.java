package gg.moonflower.pollen.core.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MoonflowerServerDownScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen." + Pollen.MOD_ID + ".moonflowerServerDown.header").withStyle(ChatFormatting.BOLD);
    private static final Component CONTENT = Component.translatable("screen." + Pollen.MOD_ID + ".moonflowerServerDown.message");
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
    private final Screen previous;
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    public MoonflowerServerDownScreen(Screen screen) {
        super(NarratorChatListener.NO_TITLE);
        this.previous = screen;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, CONTENT, this.width - 50);
        int i = (this.message.getLineCount() + 1) * 9 * 2;
        this.addRenderableWidget(new Button(this.width / 2 - 100, 100 + i, 200, 20, CommonComponents.GUI_DONE, arg -> this.minecraft.setScreen(this.previous)));
    }

    @Override
    public Component getNarrationMessage() {
        return NARRATION;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);
        drawCenteredString(poseStack, this.font, TITLE, this.width / 2, 30, 16777215);
        this.message.renderCentered(poseStack, this.width / 2, 70, 9 * 2, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
