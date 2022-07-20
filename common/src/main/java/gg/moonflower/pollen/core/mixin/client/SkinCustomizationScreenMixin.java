package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.profile.ProfileData;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import gg.moonflower.pollen.core.client.screen.EntitlementListScreen;
import gg.moonflower.pollen.core.client.screen.LinkPatreonScreen;
import gg.moonflower.pollen.core.client.screen.MoonflowerServerDownScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mixin(SkinCustomizationScreen.class)
public class SkinCustomizationScreenMixin extends OptionsSubScreen {

    private SkinCustomizationScreenMixin(Screen screen, Options options, Component component) {
        super(screen, options, component);
    }

    @ModifyVariable(method = "init", ordinal = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/SkinCustomizationScreen;addButton(Lnet/minecraft/client/gui/components/AbstractWidget;)Lnet/minecraft/client/gui/components/AbstractWidget;", ordinal = 1, shift = At.Shift.AFTER))
    public int init(int i) {
        ++i;
        boolean disabled = Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.get();
        CompletableFuture<Boolean> serverDown = ProfileManager.CONNECTION.isServerDown();
        Component moonflowerProfilesDisabled = new TranslatableComponent("screen." + Pollen.MOD_ID + ".moonflowerProfilesDisabled", String.join(".", Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.getPath())).withStyle(ChatFormatting.RED);
        Button button = new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("options." + Pollen.MOD_ID + ".entitlementList"), __ -> {
            if (!disabled) {
                if (serverDown.join()) {
                    this.minecraft.setScreen(new MoonflowerServerDownScreen(this));
                    return;
                }
                this.minecraft.setScreen(ProfileManager.getProfile(this.minecraft.getUser().getGameProfile().getId()).join() == ProfileData.EMPTY ? new LinkPatreonScreen(this) : new EntitlementListScreen(this));
            }
        }, (button1, poseStack, mouseX, mouseY) -> {
            if (disabled)
                this.renderTooltip(poseStack, this.font.split(moonflowerProfilesDisabled, 200), mouseX, mouseY);
        });
        if (disabled) {
            button.active = false;
        } else if (!serverDown.isDone()) {
            button.active = false;
            serverDown.thenRunAsync(() -> button.active = true, this.minecraft);
        }
        this.addButton(button);
        return i;
    }
}
