package gg.moonflower.pollen.core.mixin.client;

import com.mojang.authlib.GameProfile;
import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import gg.moonflower.pollen.core.client.sound.CustomLiquidSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

    @Shadow
    @Final
    protected Minecraft minecraft;
    @Unique
    private final Set<Tag<Fluid>> wasInFluids = new HashSet<>();

    private LocalPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "updateIsUnderwater", at = @At("TAIL"))
    public void updateCustomFluidSounds(CallbackInfoReturnable<Boolean> cir) {
        FluidBehaviorRegistry.getFluids().forEach(tag -> {
            PollenFluidBehavior behavior = Objects.requireNonNull(FluidBehaviorRegistry.get(tag));
            boolean bl = this.wasInFluids.contains(tag);
            boolean bl2 = this.isEyeInFluid(tag);
            if (bl2) {
                this.wasInFluids.add(tag);
            } else {
                this.wasInFluids.remove(tag);
            }

            if (!bl && bl2) {
                SoundEvent ambientEnter = behavior.getAmbientEnter(this);
                SoundEvent ambientLoop = behavior.getAmbientLoop(this);
                if (ambientEnter != null)
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ambientEnter, SoundSource.AMBIENT, 1.0F, 1.0F, false);
                if (ambientLoop != null)
                    this.minecraft.getSoundManager().play(new CustomLiquidSoundInstance((LocalPlayer) (Object) this, tag, ambientLoop));
            }

            if (bl && !bl2) {
                SoundEvent ambientExit = behavior.getAmbientExit(this);
                if (ambientExit != null)
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ambientExit, SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
        });
    }
}
