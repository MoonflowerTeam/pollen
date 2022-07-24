package gg.moonflower.pollen.core.client.entitlement.type;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.screen.button.ArrayEntry;
import gg.moonflower.pollen.core.client.screen.button.EntitlementEntry;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class DeveloperHalo extends AbstractHalo {

    public static final Codec<DeveloperHalo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, HaloData.CODEC).fieldOf("halos").forGetter(halo -> halo.halos)
    ).apply(instance, DeveloperHalo::new));

    private final Map<String, HaloData> halos;
    private String type;
    private ResourceLocation textureKey;

    public DeveloperHalo(Map<String, HaloData> halos) {
        this.halos = halos;
    }

    @Override
    protected Entitlement copyData() {
        return new DeveloperHalo(this.halos);
    }

    @Override
    public void updateSettings(JsonObject settings) {
        super.updateSettings(settings);
        if (settings.has("halo_type"))
            this.setType(GsonHelper.getAsString(settings, "halo_type"));
    }

    @Override
    public JsonObject saveSettings() {
        JsonObject settings = super.saveSettings();
        settings.addProperty("halo_type", this.type);
        return settings;
    }

    @Override
    public void addEntries(Consumer<EntitlementEntry> entryConsumer) {
        super.addEntries(entryConsumer);
        entryConsumer.accept(new ArrayEntry<>(Component.literal("Type"), this, this::setType, this.type, this.halos.keySet().toArray(new String[0])).setDisplayGenerator(s -> s.toUpperCase(Locale.ROOT)));
    }

    @Override
    public Type getType() {
        return Type.DEVELOPER_HALO;
    }

    @Override
    public void registerTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        this.halos.forEach((key, value) -> {
            textureConsumer.accept(new ResourceLocation(Pollen.MOD_ID, key), value.getTextureTable());
            textureConsumer.accept(new ResourceLocation(Pollen.MOD_ID, key + "_emissive"), new GeometryModelTextureTable(value.getTextureTable().getTextureDefinitions().entrySet().stream().collect(Collectors.<Map.Entry<String, GeometryModelTexture[]>, String, GeometryModelTexture[]>toMap(Map.Entry::getKey, entry -> {
                GeometryModelTexture[] textures = new GeometryModelTexture[entry.getValue().length];
                for (int i = 0; i < textures.length; i++) {
                    GeometryModelTexture texture = entry.getValue()[i];
                    textures[i] = GeometryModelTexture.texture(texture).setGlowing(true).build();
                }
                return textures;
            }))));
        });
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
        this.textureKey = new ResourceLocation(Pollen.MOD_ID, type + (this.isEmissive() ? "_emissive" : ""));
    }

    @Override
    public void setEmissive(boolean emissive) {
        super.setEmissive(emissive);
        this.textureKey = new ResourceLocation(Pollen.MOD_ID, this.type + (this.isEmissive() ? "_emissive" : ""));
    }
}
