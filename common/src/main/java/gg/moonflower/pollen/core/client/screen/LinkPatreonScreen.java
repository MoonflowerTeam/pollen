package gg.moonflower.pollen.core.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.profile.ProfileConnection;
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
import net.minecraft.util.HttpUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LinkPatreonScreen extends Screen {

    private final Screen previous;
    private static final Component TITLE = Component.translatable("screen." + Pollen.MOD_ID + ".linkPatreon.header").withStyle(ChatFormatting.BOLD);
    private static final Component CONTENT = Component.translatable("screen." + Pollen.MOD_ID + ".linkPatreon.message");
    private static final Component WAITING_CONTENT = Component.translatable("screen." + Pollen.MOD_ID + ".linkPatreon.waiting");
    private static final Component PATREON_FAIL = Component.translatable("screen." + Pollen.MOD_ID + ".linkPatreon.fail");
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    private Button cancelButton;
    private Button proceedButton;
    private Button backButton;

    private boolean cancelled;
    private CompletableFuture<ProfileConnection.LinkStatus> requestFuture;
    private CompletableFuture<?> completeFuture;

    public LinkPatreonScreen(Screen screen) {
        super(NarratorChatListener.NO_TITLE);
        this.previous = screen;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, this.completeFuture != null && !this.completeFuture.isDone() ? WAITING_CONTENT : CONTENT, this.width - 50);
        int i = (this.message.getLineCount() + 1) * 9 * 2;
        this.addRenderableWidget(this.cancelButton = new Button(this.width / 2 - 100, 100 + i, 200, 20, CommonComponents.GUI_CANCEL, arg -> {
            if (this.requestFuture == null)
                return;
            this.requestFuture.thenAcceptAsync(ProfileConnection.LinkStatus::cancel, this.minecraft);
            this.cancelButton.active = false;
            this.cancelled = true;
        }));

        this.addRenderableWidget(this.proceedButton = new Button(this.width / 2 - 155, 100 + i, 150, 20, CommonComponents.GUI_PROCEED, arg -> {
            if (this.completeFuture != null && !this.completeFuture.isDone())
                return;
            UUID id = this.minecraft.getUser().getGameProfile().getId();
            this.cancelButton.visible = true;
            this.proceedButton.visible = false;
            this.backButton.visible = false;
            this.message = MultiLineLabel.create(this.font, WAITING_CONTENT, this.width - 50);
            this.requestFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return ProfileManager.CONNECTION.linkPatreon();
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, HttpUtil.DOWNLOAD_EXECUTOR);
            this.completeFuture = this.requestFuture.thenCompose(status -> status.getConnectFuture().thenRunAsync(() -> Util.getPlatform().openUri(status.getUrl()), this.minecraft).thenCompose(__ -> status.getResponseFuture().thenRunAsync(() -> {
                ProfileManager.clearCache(id);
                EntitlementManager.clearCache(id);
                ProfileManager.getProfile(id).thenAcceptAsync(profile -> {
                    if (profile == ProfileData.EMPTY)
                        throw new CompletionException(new IllegalStateException("Failed to download profile"));
                    this.minecraft.setScreen(new EntitlementListScreen(this.previous));
                }, this.minecraft);
            }, this.minecraft))).exceptionally(e -> {
                if (!this.cancelled)
                    e.printStackTrace();
                this.minecraft.execute(() -> {
                    if (this.cancelled) {
                        this.minecraft.setScreen(this.previous);
                        return;
                    }

                    this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_BACKUP, PATREON_FAIL, Component.literal(e.getLocalizedMessage())));
                    this.cancelButton.visible = false;
                    this.cancelButton.active = true;
                    this.proceedButton.visible = true;
                    this.backButton.visible = true;
                    this.cancelled = false;
                    this.message = MultiLineLabel.create(this.font, CONTENT, this.width - 50);
                });
                return null;
            });
        }));
        this.addRenderableWidget(this.backButton = new Button(this.width / 2 - 155 + 160, 100 + i, 150, 20, CommonComponents.GUI_BACK, arg -> this.minecraft.setScreen(this.previous)));

        this.cancelButton.visible = this.completeFuture != null && !this.completeFuture.isDone();
        this.cancelButton.active = !this.cancelled;
        this.proceedButton.visible = !this.cancelButton.visible;
        this.backButton.visible = !this.cancelButton.visible;
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

    @Override
    public boolean shouldCloseOnEsc() {
        return (this.completeFuture == null || this.completeFuture.isDone()) && super.shouldCloseOnEsc();
    }
}
