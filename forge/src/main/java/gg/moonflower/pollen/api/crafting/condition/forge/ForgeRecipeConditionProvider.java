package gg.moonflower.pollen.api.crafting.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeConditionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ForgeRecipeConditionProvider implements PollinatedRecipeConditionProvider {

    private static final Map<ResourceLocation, IConditionSerializer<?>> CONDITIONS;

    static {
        try {
            //noinspection unchecked
            CONDITIONS = (Map<ResourceLocation, IConditionSerializer<?>>) CraftingHelper.class.getDeclaredField("conditions").get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load conditions", e);
        }
    }

    private final ICondition condition;

    public ForgeRecipeConditionProvider(ICondition condition) {
        this.condition = condition;
    }

    @Override
    public void write(JsonObject json) {
        write(json, this.condition);
    }

    @Override
    public ResourceLocation getName() {
        return this.condition.getID();
    }

    @SuppressWarnings("unchecked")
    private static <T extends ICondition> void write(JsonObject json, T condition) {
        IConditionSerializer<T> serializer = (IConditionSerializer<T>) CONDITIONS.get(condition.getID());
        serializer.write(json, condition);
    }
}
