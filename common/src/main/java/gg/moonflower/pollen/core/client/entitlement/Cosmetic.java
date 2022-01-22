package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Objects;

public class Cosmetic extends Entitlement implements TexturedEntitlement, ModelEntitlement {

    public static final Codec<Cosmetic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("modelUrl").forGetter(Cosmetic::getModelUrl),
            Codec.STRING.fieldOf("modelKey").forGetter(cosmetic -> Objects.requireNonNull(cosmetic.getModelKey()).getPath()),
            GeometryModelTextureTable.CODEC.fieldOf("textureTable").forGetter(Cosmetic::getTexture)
    ).apply(instance, Cosmetic::new));

    private final String modelUrl;
    private final ResourceLocation modelKey;
    private final GeometryModelTextureTable textureTable;
    private boolean enabled;

    public Cosmetic(String modelUrl, String modelKey, GeometryModelTextureTable textureTable) {
        this.modelUrl = modelUrl;
        this.modelKey = new ResourceLocation(Pollen.MOD_ID, modelKey);
        this.textureTable = textureTable;
    }

    @Override
    public void updateSettings(JsonObject settings) {
        if (settings.has("enabled"))
            this.enabled = GsonHelper.getAsBoolean(settings, "enabled");
    }

    @Override
    public JsonObject saveSettings() {
        JsonObject settings = new JsonObject();
        settings.addProperty("enabled", this.enabled);
        return settings;
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public String getModelUrl() {
        return modelUrl;
    }

    @Override
    public ResourceLocation getModelKey() {
        return modelKey;
    }

    @Override
    public GeometryModelTextureTable getTexture() {
        return textureTable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
