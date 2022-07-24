package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * @author Ocelot
 * @since 1.0.0.
 */
public abstract class PollinatedLanguageProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<String, String> keys;
    private final DataGenerator generator;
    protected final String domain;
    protected final String locale;

    public PollinatedLanguageProvider(DataGenerator generator, PollinatedModContainer container, String locale) {
        this.keys = new TreeMap<>();
        this.generator = generator;
        this.domain = container.getId();
        this.locale = locale;
    }

    /**
     * Registers all translations to be placed inside the lang file.
     */
    protected abstract void registerTranslations();

    @Override
    public void run(CachedOutput output) {
        this.registerTranslations();

        Path path = this.generator.getOutputFolder().resolve("assets/" + this.domain + "/lang/" + this.locale + ".json");
        try {
            JsonElement json = GSON.toJsonTree(this.keys);
            DataProvider.saveStable(output, json, path);
        } catch (IOException e) {
            LOGGER.error("Couldn't save {}", path, e);
        }
    }

    public void addBlock(Supplier<? extends Block> key, String name) {
        this.add(key.get().getDescriptionId(), name);
    }

    public void addBlock(Block key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addItem(Supplier<? extends Item> key, String name) {
        this.add(key.get().getDescriptionId(), name);
    }

    public void addItem(Item key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        this.add(key.get().getDescriptionId(), name);
    }

    public void addEntityType(EntityType<?> key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addItemStack(Supplier<ItemStack> key, String name) {
        this.add(key.get().getDescriptionId(), name);
    }

    public void addItemStack(ItemStack key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
        this.add(key.get().getDescriptionId(), name);
    }

    public void addEnchantment(Enchantment key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEffect(Supplier<? extends MobEffect> key, String name) {
        this.add(key.get().getDescriptionId(), name);
    }

    public void addEffect(MobEffect key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void add(String key, String value) {
        if (this.keys.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }

    @Override
    public String getName() {
        return "Language: " + this.locale;
    }
}
