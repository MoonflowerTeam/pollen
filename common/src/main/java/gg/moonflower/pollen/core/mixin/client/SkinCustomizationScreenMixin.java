package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.profile.ProfileData;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import gg.moonflower.pollen.core.client.screen.EntitlementListScreen;
import gg.moonflower.pollen.core.client.screen.LinkPatreonScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SkinCustomizationScreen.class)
public class SkinCustomizationScreenMixin extends OptionsSubScreen {

    private SkinCustomizationScreenMixin(Screen screen, Options options, Component component) {
        super(screen, options, component);
    }

    @ModifyVariable(method = "init", ordinal = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/SkinCustomizationScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 1, shift = At.Shift.AFTER))
    public int init(int i) {
        ++i;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("options." + Pollen.MOD_ID + ".entitlementList"), __ -> this.minecraft.setScreen(ProfileManager.getProfile(this.minecraft.getUser().getGameProfile().getId()).join() == ProfileData.EMPTY ? new LinkPatreonScreen(this) : new EntitlementListScreen(this))));
        return i;
    }
}
