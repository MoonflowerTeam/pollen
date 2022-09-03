package gg.moonflower.pollen.api.pinwheel.v1.animation;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

/**
 * Processes animation timeline effects.
 *
 * @author Ocelot
 */
// TODO rewrite
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
            int soundId = this.soundId[i] - iteration * animation.soundEffects().length;
            if (soundId < 0 || soundId >= animation.soundEffects().length)
                continue;

            int oldId = soundId;
            while (oldId < animation.soundEffects().length && animationTime >= animation.soundEffects()[oldId].time()) {
                oldId++;
            }

            // Only play the most recent unplayed sound
            if (oldId != soundId) {
                AnimationData.SoundEffect soundEffect = animation.soundEffects()[oldId - 1];
                if (iteration == 0 || !soundEffect.loop())
                    this.source.handleSoundEffect(animation, soundEffect);
                this.soundId[i] = oldId;
            }
        }
    }
}
