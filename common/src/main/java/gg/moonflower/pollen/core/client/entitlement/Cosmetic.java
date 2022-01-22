package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.util.GsonHelper;

public class Cosmetic extends Entitlement {

    public static final Codec<Cosmetic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("modelUrl").forGetter(Cosmetic::getModelUrl),
            GeometryModelTextureTable.CODEC.fieldOf("textureTable").forGetter(Cosmetic::getTextureTable)
    ).apply(instance, Cosmetic::new));

    private final String modelUrl;
    private final GeometryModelTextureTable textureTable;
    private boolean enabled;

    public Cosmetic(String modelUrl, GeometryModelTextureTable textureTable) {
        this.modelUrl = modelUrl;
        this.textureTable = textureTable;
    }

    @Override
    public void updateSettings(JsonObject settings) {
        if (settings.has("enabled"))
            this.enabled = GsonHelper.getAsBoolean(settings, "enabled");
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public GeometryModelTextureTable getTextureTable() {
        return textureTable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
