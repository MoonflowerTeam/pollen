package gg.moonflower.pollen.api.resource.condition.fabric;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import gg.moonflower.pollen.core.Pollen;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

@ApiStatus.Internal
public class PollinatedResourceConditionImpl {

    public static final ResourceLocation TRUE = new ResourceLocation(Pollen.MOD_ID, "true");
    public static final ResourceLocation FALSE = new ResourceLocation(Pollen.MOD_ID, "false");
    public static final ResourceLocation ITEM_EXISTS = new ResourceLocation(Pollen.MOD_ID, "item_exists");
    public static final ResourceLocation BLOCK_EXISTS = new ResourceLocation(Pollen.MOD_ID, "block_exists");
    public static final ResourceLocation FLUID_EXISTS = new ResourceLocation(Pollen.MOD_ID, "fluid_exists");

    private static final PollinatedResourceConditionProvider TRUE_CONDITION = wrap(new ConditionJsonProvider() {
        @Override
        public ResourceLocation getConditionId() {
            return TRUE;
        }

        @Override
        public void writeParameters(JsonObject object) {
        }
    });
    private static final PollinatedResourceConditionProvider FALSE_CONDITION = wrap(new ConditionJsonProvider() {
        @Override
        public ResourceLocation getConditionId() {
            return FALSE;
        }

        @Override
        public void writeParameters(JsonObject object) {
        }
    });

    public static void init() {
        ResourceConditions.register(TRUE, object -> true);
        ResourceConditions.register(FALSE, object -> false);
        ResourceConditions.register(ITEM_EXISTS, object -> registryKeyExists(object, "item", Registry.ITEM));
        ResourceConditions.register(BLOCK_EXISTS, object -> registryKeyExists(object, "block", Registry.BLOCK));
        ResourceConditions.register(FLUID_EXISTS, object -> registryKeyExists(object, "fluid", Registry.FLUID));
    }

    public static PollinatedResourceConditionProvider and(PollinatedResourceConditionProvider... values) {
        return wrap(DefaultResourceConditions.and(Arrays.stream(values).map(PollinatedResourceConditionImpl::wrap).toArray(ConditionJsonProvider[]::new)));
    }

    public static PollinatedResourceConditionProvider FALSE() {
        return FALSE_CONDITION;
    }

    public static PollinatedResourceConditionProvider TRUE() {
        return TRUE_CONDITION;
    }

    public static PollinatedResourceConditionProvider not(PollinatedResourceConditionProvider value) {
        return wrap(DefaultResourceConditions.not(wrap(value)));
    }

    public static PollinatedResourceConditionProvider or(PollinatedResourceConditionProvider... values) {
        return wrap(DefaultResourceConditions.or(Arrays.stream(values).map(PollinatedResourceConditionImpl::wrap).toArray(ConditionJsonProvider[]::new)));
    }

    public static PollinatedResourceConditionProvider itemExists(ResourceLocation name) {
        return wrap(registryKeyExistsProvider(ITEM_EXISTS, "item", name));
    }

    public static PollinatedResourceConditionProvider blockExists(ResourceLocation name) {
        return wrap(registryKeyExistsProvider(BLOCK_EXISTS, "block", name));
    }

    public static PollinatedResourceConditionProvider fluidExists(ResourceLocation name) {
        return wrap(registryKeyExistsProvider(FLUID_EXISTS, "fluid", name));
    }

    @SuppressWarnings("unchecked")
    public static PollinatedResourceConditionProvider itemTagPopulated(TagKey<Item> tag) {
        return wrap(DefaultResourceConditions.itemTagsPopulated(tag));
    }

    @SuppressWarnings("unchecked")
    public static PollinatedResourceConditionProvider blockTagPopulated(TagKey<Block> tag) {
        return wrap(DefaultResourceConditions.blockTagsPopulated(tag));
    }

    @SuppressWarnings("unchecked")
    public static PollinatedResourceConditionProvider fluidTagPopulated(TagKey<Fluid> tag) {
        return wrap(DefaultResourceConditions.fluidTagsPopulated(tag));
    }

    public static PollinatedResourceConditionProvider allModsLoaded(String... modIds) {
        return wrap(DefaultResourceConditions.allModsLoaded(modIds));
    }

    public static PollinatedResourceConditionProvider anyModsLoaded(String... modIds) {
        return wrap(DefaultResourceConditions.anyModLoaded(modIds));
    }

    public static PollinatedResourceConditionProvider wrap(ConditionJsonProvider condition) {
        return new PollinatedResourceConditionWrapper(condition);
    }

    public static ConditionJsonProvider wrap(PollinatedResourceConditionProvider condition) {
        return new ConditionJsonWrapper(condition);
    }

    private static ConditionJsonProvider registryKeyExistsProvider(ResourceLocation id, String jsonKey, ResourceLocation key) {
        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject json) {
                json.addProperty(jsonKey, key.toString());
            }
        };
    }

    private static <T> boolean registryKeyExists(JsonObject object, String jsonKey, Registry<T> registry) {
        return registry.containsKey(new ResourceLocation(GsonHelper.getAsString(object, jsonKey)));
    }

    private static class ConditionJsonWrapper implements ConditionJsonProvider {

        private final PollinatedResourceConditionProvider provider;

        private ConditionJsonWrapper(PollinatedResourceConditionProvider provider) {
            this.provider = provider;
        }

        @Override
        public ResourceLocation getConditionId() {
            return this.provider.getName();
        }

        @Override
        public void writeParameters(JsonObject object) {
            this.provider.write(object);
        }
    }

    private static class PollinatedResourceConditionWrapper implements PollinatedResourceConditionProvider {

        private final ConditionJsonProvider condition;

        public PollinatedResourceConditionWrapper(ConditionJsonProvider condition) {
            this.condition = condition;
        }

        @Override
        public void write(JsonObject json) {
            this.condition.writeParameters(json);
        }

        @Override
        public ResourceLocation getName() {
            return this.condition.getConditionId();
        }
    }
}
