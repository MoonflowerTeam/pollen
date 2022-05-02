package gg.moonflower.pollen.pinwheel.api.common.animation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Deserializes custom animations from JSON.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AnimationData {
    /**
     * A completely empty animation definition.
     */
    public static final AnimationData EMPTY = new AnimationData("empty", Loop.NONE, MolangExpression.ZERO, 0.0F, false, new BoneAnimation[0], new SoundEffect[0], new ParticleEffect[0], new TimelineEffect[0]);

    private final String name;
    private final Loop loop;
    private final MolangExpression blendWeight;
    private final float animationLength;
    private final boolean overridePreviousAnimation;
    private final BoneAnimation[] boneAnimations;
    private final SoundEffect[] soundEffects;
    private final ParticleEffect[] particleEffects;
    private final TimelineEffect[] timelineEffects;

    public AnimationData(String name, Loop loop, MolangExpression blendWeight, float animationLength, boolean overridePreviousAnimation, BoneAnimation[] boneAnimations, SoundEffect[] soundEffects, ParticleEffect[] particleEffects, TimelineEffect[] timelineEffects) {
        this.name = name;
        this.loop = loop;
        this.blendWeight = blendWeight;
        this.animationLength = animationLength;
        this.overridePreviousAnimation = overridePreviousAnimation;
        this.boneAnimations = boneAnimations;
        this.soundEffects = soundEffects;
        this.particleEffects = particleEffects;
        this.timelineEffects = timelineEffects;
    }

    /**
     * @return The name of this animation
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type of looping this animation uses
     */
    public Loop getLoop() {
        return loop;
    }

    /**
     * @return How much this animation should be blended with others
     */
    public MolangExpression getBlendWeight() {
        return blendWeight;
    }

    /**
     * @return The overall length of this animation
     */
    public float getAnimationLength() {
        return animationLength;
    }

    /**
     * @return Whether all animations leading up to this point should be overridden
     */
    public boolean isOverridePreviousAnimation() {
        return overridePreviousAnimation;
    }

    /**
     * @return The set of bones that are animated in this animation
     */
    public BoneAnimation[] getBoneAnimations() {
        return boneAnimations;
    }

    /**
     * @return All sounds that should play at their respective times
     */
    public SoundEffect[] getSoundEffects() {
        return soundEffects;
    }

    /**
     * @return All particles that should be spawned at their respective times
     */
    public ParticleEffect[] getParticleEffects() {
        return particleEffects;
    }

    /**
     * @return All effects that should be applied at their respective times
     */
    public TimelineEffect[] getTimelineEffects() {
        return timelineEffects;
    }

    @Override
    public String toString() {
        return "AnimationData{" +
                "name='" + name + '\'' +
                ", loop=" + loop +
                ", blendWeight=" + blendWeight +
                ", animationLength=" + animationLength +
                ", overridePreviousAnimation=" + overridePreviousAnimation +
                ", boneAnimations=" + Arrays.toString(boneAnimations) +
                ", soundEffects=" + Arrays.toString(soundEffects) +
                ", particleEffects=" + Arrays.toString(particleEffects) +
                ", timelineEffects=" + Arrays.toString(timelineEffects) +
                '}';
    }

    /**
     * Animation interpolation functions.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public enum LerpMode implements Function<Float, Float> {

        // Standard functions
        LINEAR(x -> x), CATMULLROM(x -> x),

        // Extended functions according to https://easings.net/
        EASE_IN_SINE(x -> 1 - Mth.cos(x * (float) Math.PI / 2.0F)),
        EASE_OUT_SINE(x -> Mth.sin(x * (float) Math.PI / 2.0F)),
        EASE_IN_OUT_SINE(x -> -(Mth.cos(x * (float) Math.PI) - 1.0F) / 2.0F),
        EASE_IN_QUAD(x -> x * x),
        EASE_OUT_QUAD(x -> {
            float a = 1.0F - x;
            return 1.0F - a * a;
        }),
        EASE_IN_OUT_QUAD(x -> {
            if (x < 0.5)
                return 2.0F * x * x;
            float a = -2.0F * x + 2.0F;
            return 1.0F - a * a / 2.0F;
        }),
        EASE_IN_CUBIC(x -> x * x * x),
        EASE_OUT_CUBIC(x -> {
            float a = 1.0F - x;
            return 1.0F - a * a * a;
        }),
        EASE_IN_OUT_CUBIC(x -> {
            if (x < 0.5)
                return 4.0F * x * x * x;
            float a = -2.0F * x + 2.0F;
            return 1.0F - a * a * a / 2.0F;
        }),
        EASE_IN_QUART(x -> x * x * x * x),
        EASE_OUT_QUART(x -> {
            float a = 1.0F - x;
            return 1.0F - a * a * a * a;
        }),
        EASE_IN_OUT_QUART(x -> {
            if (x < 0.5)
                return 8.0F * x * x * x * x;
            float a = -2.0F * x + 2.0F;
            return 1.0F - a * a * a * a / 2.0F;
        }),
        EASE_IN_QUINT(x -> x * x * x * x * x),
        EASE_OUT_QUINT(x -> {
            float a = 1.0F - x;
            return 1.0F - a * a * a * a * a;
        }),
        EASE_IN_OUT_QUINT(x -> {
            if (x < 0.5)
                return 16.0F * x * x * x * x * x;
            float a = -2.0F * x + 2.0F;
            return 1.0F - a * a * a * a * a / 2.0F;
        }),
        EASE_IN_EXPO(x -> x == 0.0F ? 0.0F : (float) Math.pow(2.0, 10.0 * x - 10.0)),
        EASE_OUT_EXPO(x -> x == 1.0F ? 1.0F : 1.0F - (float) Math.pow(2.0, -10.0 * x)),
        EASE_IN_OUT_EXPO(x -> {
            if (x == 0.0F)
                return 0.0F;
            if (x == 1.0F)
                return 1.0F;
            if (x < 0.5)
                return (float) Math.pow(2.0, 20.0 * x - 10.0) / 2.0F;
            return (2.0F - (float) Math.pow(2.0, -20.0 * x + 10.0)) / 2.0F;
        }),
        EASE_IN_CIRC(x -> 1.0F - Mth.sqrt(1.0F - x * x)),
        EASE_OUT_CIRC(x -> {
            float a = x - 1.0F;
            return Mth.sqrt(1.0F - a * a);
        }),
        EASE_IN_OUT_CIRC(x -> {
            if (x < 0.5)
                return (1.0F - Mth.sqrt(1.0F - 4 * x * x)) / 2.0F;
            float a = -2.0F * x + 2.0F;
            return (Mth.sqrt(1.0F - a * a) + 1.0F) / 2.0F;
        }),
        EASE_IN_BACK(x -> 2.70158F * x * x * x - 1.70158F * x * x),
        EASE_OUT_BACK(x -> {
            float a = x - 1.0F;
            return 1.0F + 2.70158F * a * a * a + 1.70158F * a * a;
        }),
        EASE_IN_OUT_BACK(x -> {
            if (x < 0.5)
                return (4.0F * x * x) * (3.5949095F * 2.0F * x - 2.5949095F) / 2.0F;
            float a = x - 2.0F;
            return ((4.0F * a * a) * (3.5949095F * (x * 2.0F - 2.0F) + 2.5949095F) + 2.0F) / 2.0F;
        }),
        EASE_IN_ELASTIC(x -> {
            if (x == 0.0F)
                return 0.0F;
            if (x == 1.0F)
                return 1.0F;
            return (float) -Math.pow(2.0, 10.0 * x - 10.0) * Mth.sin((x * 10.0F - 10.75F) * (2.0F * (float) Math.PI) / 3.0F);
        }),
        EASE_OUT_ELASTIC(x -> {
            if (x == 0.0F)
                return 0.0F;
            if (x == 1.0F)
                return 1.0F;
            return (float) Math.pow(2.0, -10.0 * x) * Mth.sin((x * 10.0F - 0.75F) * (2.0F * (float) Math.PI) / 3.0F) + 1.0F;
        }),
        EASE_IN_OUT_ELASTIC(x -> {
            float c5 = (2.0F * (float) Math.PI) / 4.5F;
            if (x == 0.0F)
                return 0.0F;
            if (x == 1.0F)
                return 1.0F;
            if (x < 0.5)
                return -((float) Math.pow(2.0, 20.0 * x - 10.0) * Mth.sin((20.0F * x - 11.125F) * c5)) / 2.0F;
            return ((float) Math.pow(2.0, -20.0 * x + 10.0) * Mth.sin((20.0F * x - 11.125F) * c5)) / 2.0F + 1.0F;
        }),
        EASE_OUT_BOUNCE(x -> {
            if (x < 0.36363636363)
                return 7.5625F * x * x;
            if (x < 0.72727272727)
                return 7.5625F * (x -= 0.54545454545F) * x + 0.75F;
            if (x < 0.90909090909)
                return 7.5625F * (x -= 0.81818181818F) * x + 0.9375F;
            return 7.5625F * (x -= 0.95454545454F) * x + 0.984375F;
        }),
        EASE_IN_BOUNCE(x -> 1.0F - EASE_OUT_BOUNCE.function.apply(1.0F - x)),
        EASE_IN_OUT_BOUNCE(x -> x < 0.5 ? (1.0F - EASE_OUT_BOUNCE.function.apply(1.0F - 2.0F * x)) / 2.0F : (1.0F + EASE_OUT_BOUNCE.function.apply(2.0F * x - 1.0F)) / 2.0F);

        private final Function<Float, Float> function;

        LerpMode(Function<Float, Float> function) {
            this.function = function;
        }

        @Override
        public Float apply(Float value) {
            return this.function.apply(Mth.clamp(value, 0.0F, 1.0F));
        }
    }

    /**
     * <p>The different types of animations looping that can occur.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public enum Loop {
        NONE, LOOP, HOLD_ON_LAST_FRAME
    }

    /**
     * <p>A collection of key frames to animate a single bone.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class BoneAnimation {
        private final String name;
        private final KeyFrame[] positionFrames;
        private final KeyFrame[] rotationFrames;
        private final KeyFrame[] scaleFrames;

        public BoneAnimation(String name, KeyFrame[] positionFrames, KeyFrame[] rotationFrames, KeyFrame[] scaleFrames) {
            this.name = name;
            this.positionFrames = positionFrames;
            this.rotationFrames = rotationFrames;
            this.scaleFrames = scaleFrames;
        }

        /**
         * @return The name of the bone to animate
         */
        public String getName() {
            return name;
        }

        /**
         * @return The position channel of key frames
         */
        public KeyFrame[] getPositionFrames() {
            return positionFrames;
        }

        /**
         * @return The rotation channel of key frames
         */
        public KeyFrame[] getRotationFrames() {
            return rotationFrames;
        }

        /**
         * @return The scale channel of key frames
         */
        public KeyFrame[] getScaleFrames() {
            return scaleFrames;
        }

        @Override
        public String toString() {
            return "BoneAnimation{" +
                    "name='" + name + '\'' +
                    ", positionFrames=" + Arrays.toString(positionFrames) +
                    ", rotationFrames=" + Arrays.toString(rotationFrames) +
                    ", scaleFrames=" + Arrays.toString(scaleFrames) +
                    '}';
        }
    }

    /**
     * <p>A key frame for a specific channel in an animation.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class KeyFrame {
        private final float time;
        private final LerpMode lerpMode;
        private final MolangExpression transformPreX;
        private final MolangExpression transformPreY;
        private final MolangExpression transformPreZ;
        private final MolangExpression transformPostX;
        private final MolangExpression transformPostY;
        private final MolangExpression transformPostZ;

        public KeyFrame(float time, LerpMode lerpMode, MolangExpression transformPreX, MolangExpression transformPreY, MolangExpression transformPreZ, MolangExpression transformPostX, MolangExpression transformPostY, MolangExpression transformPostZ) {
            this.time = time;
            this.lerpMode = lerpMode;
            this.transformPreX = transformPreX;
            this.transformPreY = transformPreY;
            this.transformPreZ = transformPreZ;
            this.transformPostX = transformPostX;
            this.transformPostY = transformPostY;
            this.transformPostZ = transformPostZ;
        }

        /**
         * @return The time this frame occurs at
         */
        public float getTime() {
            return time;
        }

        /**
         * @return The function to use when interpolating to and from this frame
         */
        public LerpMode getLerpMode() {
            return lerpMode;
        }

        /**
         * @return The position to use when transitioning to this frame in the x-axis
         */
        public MolangExpression getTransformPreX() {
            return transformPreX;
        }

        /**
         * @return The position to use when transitioning to this frame in the y-axis
         */
        public MolangExpression getTransformPreY() {
            return transformPreY;
        }

        /**
         * @return The position to use when transitioning to this frame in the z-axis
         */
        public MolangExpression getTransformPreZ() {
            return transformPreZ;
        }

        /**
         * @return The position to use when transitioning away from this frame in the x-axis
         */
        public MolangExpression getTransformPostX() {
            return transformPostX;
        }

        /**
         * @return The position to use when transitioning away from this frame in the y-axis
         */
        public MolangExpression getTransformPostY() {
            return transformPostY;
        }

        /**
         * @return The position to use when transitioning away from this frame in the z-axis
         */
        public MolangExpression getTransformPostZ() {
            return transformPostZ;
        }

        @Override
        public String toString() {
            return "KeyFrame{" +
                    "time=" + time +
                    ", transformPre=(" + transformPreX + ", " + transformPreY + ", " + transformPreZ + ")" +
                    ", transformPost=" + transformPostX + "," + transformPostY + ", " + transformPostZ + ")" +
                    '}';
        }
    }

    /**
     * <p>A sound event that plays during a key frame.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class SoundEffect {
        private final float time;
        private final String effect;
        private final MolangExpression pitch;
        private final MolangExpression volume;
        private final boolean loop;

        public SoundEffect(float time, String effect, MolangExpression pitch, MolangExpression volume, boolean loop) {
            this.time = time;
            this.effect = effect;
            this.pitch = pitch;
            this.volume = volume;
            this.loop = loop;
        }

        /**
         * @return The time in seconds this effect plays at
         */
        public float getTime() {
            return time;
        }

        /**
         * @return The sound event name that should play
         */
        public String getEffect() {
            return effect;
        }

        /**
         * @return The pitch multiplier from 0.5 to 2.0
         */
        public MolangExpression getPitch() {
            return pitch;
        }

        /**
         * @return The distance modifier this sound can be heard from
         */
        public MolangExpression getVolume() {
            return volume;
        }

        /**
         * @return Whether this sound should be played once and then looped
         */
        public boolean isLoop() {
            return loop;
        }

        @Override
        public String toString() {
            return "SoundEffect{" +
                    "time=" + time +
                    ", effect='" + effect + '\'' +
                    ", pitch='" + pitch + '\'' +
                    ", volume='" + volume + '\'' +
                    ", loop='" + loop + '\'' +
                    '}';
        }
    }

    /**
     * <p>A particle effect that spawns during a key frame.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class ParticleEffect {
        private final float time;
        private final String effect;
        private final String locator;

        public ParticleEffect(float time, String effect, String locator) {
            this.time = time;
            this.effect = effect;
            this.locator = locator;
        }

        /**
         * @return The time in seconds this effect plays at
         */
        public float getTime() {
            return time;
        }

        /**
         * @return The particle name that should be spawned
         */
        public String getEffect() {
            return effect;
        }

        /**
         * @return The name of the locator to place the particle at
         */
        public String getLocator() {
            return locator;
        }

        @Override
        public String toString() {
            return "ParticleEffect{" +
                    "time=" + time +
                    ", effect='" + effect + '\'' +
                    ", locator='" + locator + '\'' +
                    '}';
        }
    }

    /**
     * <p>Arbitrary instructions that happen during a key frame.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class TimelineEffect {
        private final float time;
        private final String data;

        public TimelineEffect(float time, String data) {
            this.time = time;
            this.data = data;
        }

        /**
         * @return The time in seconds this effect plays at
         */
        public float getTime() {
            return time;
        }

        /**
         * @return The data at this time
         */
        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "TimelineEffect{" +
                    "time=" + time +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    public static class Deserializer implements JsonDeserializer<AnimationData[]> {
        private static Loop parseLoop(JsonElement json) {
            if (!json.isJsonPrimitive())
                throw new JsonSyntaxException("Expected Boolean or String, was " + GsonHelper.getType(json));
            if (json.getAsJsonPrimitive().isBoolean())
                return json.getAsBoolean() ? Loop.LOOP : Loop.NONE;
            if (json.getAsJsonPrimitive().isString()) {
                for (Loop loop : Loop.values())
                    if (loop.name().equalsIgnoreCase(json.getAsString()))
                        return loop;
                throw new JsonSyntaxException("Unsupported loop: " + json.getAsString());
            }
            throw new JsonSyntaxException("Expected Boolean or String, was " + GsonHelper.getType(json));
        }

        private static void parseEffect(BiConsumer<Float, JsonElement> effectConsumer, JsonObject json, String name) {
            if (!json.has(name))
                return;

            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject(name).entrySet()) {
                try {
                    effectConsumer.accept(Float.parseFloat(entry.getKey()), entry.getValue());
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Failed to parse " + name + " at time '" + entry.getKey() + "'", e);
                }
            }
        }

        private static void parseTransform(Collection<KeyFrame> frames, JsonObject json, String name, Supplier<MolangExpression[]> defaultValue) throws JsonParseException {
            if (!json.has(name))
                return;

            JsonElement transformJson = json.get(name);
            if (transformJson.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : transformJson.getAsJsonObject().entrySet()) {
                    try {
                        float time = Float.parseFloat(entry.getKey());
                        if (frames.stream().anyMatch(keyFrame -> keyFrame.getTime() == time))
                            throw new JsonSyntaxException("Duplicate channel time '" + time + "'");

                        ChannelData data = parseChannel(transformJson.getAsJsonObject(), entry.getKey(), defaultValue);
                        frames.add(new KeyFrame(time, data.lerpMode, data.pre[0], data.pre[1], data.pre[2], data.post[0], data.post[1], data.post[2]));
                    } catch (NumberFormatException e) {
                        throw new JsonParseException("Invalid keyframe time '" + entry.getKey() + "'", e);
                    }
                }
            } else {
                MolangExpression[] values = JSONTupleParser.getExpression(json, name, 3, defaultValue);
                frames.add(new KeyFrame(0, LerpMode.LINEAR, values[0], values[1], values[2], values[0], values[1], values[2]));
            }
        }

        private static ChannelData parseChannel(JsonObject json, String name, Supplier<MolangExpression[]> defaultValue) throws JsonSyntaxException {
            if (!json.has(name) && !json.get(name).isJsonObject() && !json.get(name).isJsonArray())
                throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonObject or JsonArray");

            JsonElement transformationElement = json.get(name);
            if (transformationElement.isJsonObject()) {
                JsonObject transformationObject = transformationElement.getAsJsonObject();

                // Parse Lerp Mode
                LerpMode lerpMode = LerpMode.LINEAR;
                if (transformationObject.has("lerp_mode")) {
                    lerpMode = null;

                    String mode = GsonHelper.getAsString(transformationObject, "lerp_mode");
                    for (LerpMode m : LerpMode.values()) {
                        if (m.name().toLowerCase(Locale.ROOT).equals(mode)) {
                            lerpMode = m;
                            break;
                        }
                    }

                    if (lerpMode == null)
                        throw new JsonSyntaxException("Unknown Lerp Mode: " + mode);
                }

                // Parse channels. Pre will default to post if not present
                MolangExpression[] post = JSONTupleParser.getExpression(transformationObject, "post", 3, null);
                return new ChannelData(JSONTupleParser.getExpression(transformationObject, "pre", 3, () -> post), post, lerpMode);
            }

            MolangExpression[] transformation = JSONTupleParser.getExpression(json, name, 3, defaultValue);
            return new ChannelData(transformation, transformation, LerpMode.LINEAR);
        }

        @Override
        public AnimationData[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<AnimationData> animations = new HashSet<>();

            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> animationEntry : jsonObject.entrySet()) {
                JsonObject animationObject = animationEntry.getValue().getAsJsonObject();

                /* Parse global animation properties */
                String animationName = animationEntry.getKey();
                Loop loop = animationObject.has("loop") ? parseLoop(animationObject.get("loop")) : Loop.NONE; // bool
                MolangExpression blendWeight = JSONTupleParser.getExpression(animationObject, "blend_weight", () -> MolangExpression.of(1.0F)); // expression TODO Molang
                float animationLength = GsonHelper.getAsFloat(animationObject, "animation_length", -1); // float
                boolean overridePreviousAnimation = GsonHelper.getAsBoolean(animationObject, "override_previous_animation", false); // bool
                Set<BoneAnimation> bones = new HashSet<>();
                List<SoundEffect> soundEffects = new ArrayList<>();
                List<ParticleEffect> particleEffects = new ArrayList<>();
                List<TimelineEffect> timlineEffects = new ArrayList<>();

                /* Parse Bone Animations */
                List<KeyFrame> positions = new ArrayList<>();
                List<KeyFrame> rotations = new ArrayList<>();
                List<KeyFrame> scales = new ArrayList<>();
                for (Map.Entry<String, JsonElement> boneAnimationEntry : GsonHelper.getAsJsonObject(animationObject, "bones").entrySet()) {
                    JsonObject boneAnimationObject = boneAnimationEntry.getValue().getAsJsonObject();

                    parseTransform(positions, boneAnimationObject, "position", () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
                    parseTransform(rotations, boneAnimationObject, "rotation", () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
                    parseTransform(scales, boneAnimationObject, "scale", () -> new MolangExpression[]{MolangExpression.of(1), MolangExpression.of(1), MolangExpression.of(1)});

                    positions.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                    rotations.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                    scales.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                    bones.add(new BoneAnimation(boneAnimationEntry.getKey(), positions.toArray(new KeyFrame[0]), rotations.toArray(new KeyFrame[0]), scales.toArray(new KeyFrame[0])));

                    positions.clear();
                    rotations.clear();
                    scales.clear();
                }

                /* Parse Effects */
                parseEffect((time, effectJson) ->
                {
                    JsonObject soundEffectsJson = GsonHelper.convertToJsonObject(effectJson, "sound_effects");
                    soundEffects.add(new SoundEffect(time, GsonHelper.getAsString(soundEffectsJson, "effect"), JSONTupleParser.getExpression(soundEffectsJson, "pitch", () -> MolangExpression.of(1.0F)), JSONTupleParser.getExpression(soundEffectsJson, "volume", () -> MolangExpression.of(1.0F)), GsonHelper.getAsBoolean(soundEffectsJson, "loop", false)));
                }, animationObject, "sound_effects");
                parseEffect((time, effectJson) ->
                {
                    JsonObject particleEffectsJson = GsonHelper.convertToJsonObject(effectJson, "particle_effects");
                    particleEffects.add(new ParticleEffect(time, GsonHelper.getAsString(particleEffectsJson, "effect"), GsonHelper.getAsString(particleEffectsJson, "locator")));
                }, animationObject, "particle_effects");
                parseEffect((time, effectJson) -> timlineEffects.add(new TimelineEffect(time, GsonHelper.convertToString(effectJson, Float.toString(time)))), animationObject, "timeline");
                soundEffects.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                particleEffects.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                timlineEffects.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));

                animations.add(new AnimationData(animationName, loop, blendWeight, animationLength, overridePreviousAnimation, bones.toArray(new BoneAnimation[0]), soundEffects.toArray(new SoundEffect[0]), particleEffects.toArray(new ParticleEffect[0]), timlineEffects.toArray(new TimelineEffect[0])));
            }

            return animations.toArray(new AnimationData[0]);
        }

        private static class ChannelData {
            private final MolangExpression[] pre;
            private final MolangExpression[] post;
            private final LerpMode lerpMode;

            private ChannelData(MolangExpression[] pre, MolangExpression[] post, LerpMode lerpMode) {
                this.pre = pre;
                this.post = post;
                this.lerpMode = lerpMode;
            }
        }
    }
}
