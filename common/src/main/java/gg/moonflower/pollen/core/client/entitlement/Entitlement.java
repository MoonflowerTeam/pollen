package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * @author Ocelot
 */
public abstract class Entitlement {

    private ResourceLocation registryName;
    private Component displayName;

    public abstract void updateSettings(JsonObject settings);

    public abstract JsonObject saveSettings();

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public final void setRegistryName(String registryName) {
        Validate.isTrue(this.registryName == null);
        this.registryName = new ResourceLocation(Pollen.MOD_ID, registryName);
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
