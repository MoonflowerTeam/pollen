package gg.moonflower.pollen.core.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CustomLiquidSoundInstance extends AbstractTickableSoundInstance {

    private final LocalPlayer player;
    private final Tag<Fluid> fluid;
    private int fade;

    public CustomLiquidSoundInstance(LocalPlayer player, Tag<Fluid> fluid, SoundEvent sound) {
        super(sound, SoundSource.AMBIENT);
        this.player = player;
        this.fluid = fluid;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (!this.player.isRemoved() && this.fade >= 0) {
            if (this.player.getFluidHeight(this.fluid) > 0.0) {
                ++this.fade;
            } else {
                this.fade -= 2;
            }

            this.fade = Math.min(this.fade, 40);
            this.volume = Math.max(0.0F, Math.min((float) this.fade / 40.0F, 1.0F));
        } else {
            this.stop();
        }
    }
}
