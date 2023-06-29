package gg.moonflower.pollen.impl.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.impl.animation.PlayingAnimationImpl;
import gg.moonflower.pollen.api.animation.v1.RenderAnimationTimer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Internal
public class PollenPlayingAnimationImpl extends PlayingAnimationImpl {

    private RenderAnimationTimer timer;
    private float lastTime;
    private float renderTime;

    public PollenPlayingAnimationImpl(AnimationData animation) {
        super(animation);
        this.timer = RenderAnimationTimer.LINEAR;
    }

    public void tick() {
        float time = this.getAnimationTime();
        this.setAnimationTime(time + 0.05F);
        this.lastTime = time;
    }

    public void setRenderTime(float partialTicks) {
        this.renderTime = this.timer.getRenderAnimationTime(this, this.lastTime, partialTicks);
    }

    @Override
    public float getRenderAnimationTime() {
        return switch (this.getAnimation().loop()) {
            case NONE -> this.renderTime;
            case LOOP -> this.renderTime % this.getLength();
            case HOLD_ON_LAST_FRAME -> Math.min(this.renderTime, this.getLength());
        };
    }

    @Override
    public void setAnimationTime(float time) {
        super.setAnimationTime(time);
        this.lastTime = time;
    }

    public void setTimer(@Nullable RenderAnimationTimer timer) {
        this.timer = timer != null ? timer : RenderAnimationTimer.LINEAR;
    }
}
