package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class Entitlement {

    public static final Codec<Entitlement> CODEC = new Codec<Type>() {
        @Override
        public <U> DataResult<Pair<Type, U>> decode(DynamicOps<U> ops, U input) {
            return ops.compressMaps() ? ops.getNumberValue(input).flatMap(number -> {
                int ordinal = number.intValue();
                Type type = ordinal < 0 || ordinal >= Type.values().length ? null : Type.values()[ordinal];
                return type == null ? DataResult.error("Unknown entitlement type: " + number) : DataResult.success(type, Lifecycle.stable());
            }).map(type -> Pair.of(type, ops.empty())) : Codec.STRING.decode(ops, input).flatMap(pair -> {
                Type object = Type.byName(pair.getFirst());
                return object == null ? DataResult.error("Unknown entitlement type: " + pair.getFirst()) : DataResult.success(Pair.of(object, pair.getSecond()), Lifecycle.stable());
            });
        }

        @Override
        public <U> DataResult<U> encode(Type type, DynamicOps<U> ops, U prefix) {
            return ops.compressMaps() ? ops.mergeToPrimitive(prefix, ops.createInt(type.ordinal())).setLifecycle(Lifecycle.stable()) : ops.mergeToPrimitive(prefix, ops.createString(type.name().toLowerCase(Locale.ROOT))).setLifecycle(Lifecycle.stable());
        }
    }.dispatch(Entitlement::getType, Type::codec);

    private Component displayName;

    public abstract void updateSettings(JsonObject settings);

    public Component getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(Component displayName) {
        Validate.isTrue(this.displayName == null);
        this.displayName = displayName;
    }

    public abstract Type getType();

    public enum Type {
        COSMETIC(Cosmetic.CODEC);

        private final Codec<? extends Entitlement> codec;

        Type(Codec<? extends Entitlement> codec) {
            this.codec = codec;
        }

        public Codec<? extends Entitlement> codec() {
            return codec;
        }

        @Nullable
        public static Type byName(String name) {
            for (Type type : values())
                if (type.name().toLowerCase(Locale.ROOT).equals(name))
                    return type;
            return null;
        }
    }
}
