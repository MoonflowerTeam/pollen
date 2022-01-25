package gg.moonflower.pollen.core.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.profile.ProfileData;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LinkPatreonScreen extends Screen {

    private final Screen previous;
    private static final Component TITLE = new TranslatableComponent("screen." + Pollen.MOD_ID + ".linkPatreon.header").withStyle(ChatFormatting.BOLD);
    private static final Component CONTENT = new TranslatableComponent("screen." + Pollen.MOD_ID + ".linkPatreon.message");
    private static final Component PATREON_FAIL = new TranslatableComponent("screen." + Pollen.MOD_ID + ".linkPatreon.fail");
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    private Button proceedButton;
    private Button backButton;

    private CompletableFuture<?> future;

    public LinkPatreonScreen(Screen screen) {
        super(NarratorChatListener.NO_TITLE);
        this.previous = screen;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, CONTENT, this.width - 50);
        int i = (this.message.getLineCount() + 1) * 9 * 2;
        this.addButton(this.proceedButton = new Button(this.width / 2 - 155, 100 + i, 150, 20, CommonComponents.GUI_PROCEED, arg -> {
            if (this.future != null && !this.future.isDone())
                return;
            UUID id = this.minecraft.getUser().getGameProfile().getId();
            this.proceedButton.active = false;
            this.backButton.active = false;
            this.future = CompletableFuture.supplyAsync(() -> {
                try {
                    return ProfileManager.CONNECTION.linkPatreon();
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, HttpUtil.DOWNLOAD_EXECUTOR).thenCompose(status -> status.getConnectFuture().thenRunAsync(() -> Util.getPlatform().openUri(status.getUrl()), this.minecraft).thenCompose(__ -> status.getResponseFuture().thenRunAsync(() -> {
                ProfileManager.clearCache(id);
                ProfileManager.getProfile(id).thenAcceptAsync(profile -> {
                    if (profile == ProfileData.EMPTY)
                        throw new CompletionException(new IllegalStateException("Failed to download profile"));
                    this.minecraft.setScreen(new EntitlementListScreen(this.previous));
                }, this.minecraft);
            }, this.minecraft))).exceptionally(e -> {
                e.printStackTrace();
                this.minecraft.execute(() -> {
                    this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_BACKUP, PATREON_FAIL, new TextComponent(e.getLocalizedMessage())));
                    this.proceedButton.active = true;
                    this.backButton.active = true;
                });
                return null;
            });
        }));
        this.addButton(this.backButton = new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, CommonComponents.GUI_BACK, arg -> this.minecraft.setScreen(this.previous)));
    }

    @Override
    public String getNarrationMessage() {
        return NARRATION.getString();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);
        drawCenteredString(poseStack, this.font, TITLE, this.width / 2, 30, 16777215);
        this.message.renderCentered(poseStack, this.width / 2, 70, 9 * 2, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return (this.future == null || this.future.isDone()) && super.shouldCloseOnEsc();
    }
}
