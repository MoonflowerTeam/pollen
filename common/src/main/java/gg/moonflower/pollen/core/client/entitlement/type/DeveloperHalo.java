package gg.moonflower.pollen.core.client.entitlement.type;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

@ApiStatus.Internal
public class DeveloperHalo extends AbstractHalo {

    public static final Codec<DeveloperHalo> CODEC = Codec.unboundedMap(Codec.STRING, HaloData.CODEC).xmap(DeveloperHalo::new, halo -> halo.halos);

    private final Map<String, HaloData> halos;
    private String type;
    private ResourceLocation textureKey;

    public DeveloperHalo(Map<String, HaloData> halos) {
        this.halos = halos;
    }

    @Override
    public void updateSettings(JsonObject settings) {
        super.updateSettings(settings);
        if (settings.has("type")) {
            this.type = GsonHelper.getAsString(settings, "type");
            this.textureKey = new ResourceLocation(Pollen.MOD_ID, type);
        }
    }

    @Override
    public JsonObject saveSettings() {
        JsonObject settings = super.saveSettings();
        settings.addProperty("type", this.type);
        return settings;
    }

    @Override
    public Type getType() {
        return Type.DEVELOPER_HALO;
    }

    @Override
    public void registerTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        this.halos.forEach((key, value) -> textureConsumer.accept(new ResourceLocation(Pollen.MOD_ID, key), value.getTextureTable()));
    }

    @Nullable
    @Override
    public ResourceLocation getTextureKey() {
        return textureKey;
    }

    @Nullable
    @Override
    protected HaloData getData() {
        return this.halos.get(this.type);
    }

    @Override
    public String[] getModelUrls() {
        return this.halos.values().stream().map(HaloData::getModelUrl).toArray(String[]::new);
    }

    public void setType(String type) {
        this.type = type;
        this.textureKey = new ResourceLocation(Pollen.MOD_ID, type);
    }
}
