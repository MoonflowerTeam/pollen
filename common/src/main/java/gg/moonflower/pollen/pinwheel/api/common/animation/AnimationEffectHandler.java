package gg.moonflower.pollen.pinwheel.api.common.animation;

import gg.moonflower.pollen.pinwheel.api.client.animation.AnimatedModel;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

/**
 * <p>Processes animation timeline effects.</p>
 *
 * @author Ocelot
 */
public class AnimationEffectHandler {
    private final AnimationEffectSource source;

    private int[] soundId;

    public AnimationEffectHandler(AnimationEffectSource source) {
        this.source = source;
        this.reset();
    }

    /**
     * Resets all sounds and replays the most recent one.
     */
    public void reset() {
        this.soundId = new int[0];
    }

    @ApiStatus.Internal
    public void tick(ResourceLocation[] animations, float animationTime) {
        if (this.soundId.length != animations.length)
            this.soundId = new int[animations.length];

        float animationLength = AnimatedModel.getAnimationLength(animationTime, Arrays.stream(animations).map(AnimationManager::getAnimation).toArray(AnimationData[]::new));
        int iteration = (int) (animationTime / animationLength);
        for (int i = 0; i < animations.length; i++) {
            AnimationData animation = AnimationManager.getAnimation(animations[i]);
            int soundId = this.soundId[i] - iteration * animation.getSoundEffects().length;
            if (soundId < 0 || soundId >= animation.getSoundEffects().length)
                continue;

            int oldId = soundId;
            while (oldId < animation.getSoundEffects().length && animationTime >= animation.getSoundEffects()[oldId].getTime()) {
                oldId++;
            }

            // Only play the most recent unplayed sound
            if (oldId != soundId) {
                AnimationData.SoundEffect soundEffect = animation.getSoundEffects()[oldId - 1];
                if (iteration == 0 || !soundEffect.isLoop())
                    this.source.handleSoundEffect(animation, soundEffect);
                this.soundId[i] = oldId;
            }
        }
    }
}
