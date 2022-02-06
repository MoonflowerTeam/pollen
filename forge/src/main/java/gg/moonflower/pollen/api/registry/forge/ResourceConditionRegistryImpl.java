package gg.moonflower.pollen.api.registry.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import gg.moonflower.pollen.api.resource.condition.forge.*;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ApiStatus.Internal
public class ResourceConditionRegistryImpl {

    private static final Set<IConditionSerializer<?>> CONDITIONS = ConcurrentHashMap.newKeySet();
    private static final Logger LOGGER = LogManager.getLogger();

    public static void register(ResourceLocation name, PollinatedResourceCondition condition) {
        if (!CONDITIONS.add(new PollinatedRecipeConditionWrapper.Serializer(name, condition)))
            LOGGER.warn("Duplicate recipe condition with id: " + name);
    }

    @SubscribeEvent
    public static void registerRecipeConditions(RegistryEvent.Register<RecipeSerializer<?>> event) {
        CONDITIONS.forEach(CraftingHelper::register);
        CraftingHelper.register(BlockExistsCondition.Serializer.INSTANCE);
        CraftingHelper.register(FluidExistsCondition.Serializer.INSTANCE);
        CraftingHelper.register(ItemTagPopulatedCondition.Serializer.INSTANCE);
        CraftingHelper.register(BlockTagPopulatedCondition.Serializer.INSTANCE);
        CraftingHelper.register(FluidTagPopulatedCondition.Serializer.INSTANCE);
    }

    public static IConditionSerializer<?> get(PollinatedResourceCondition condition) {
        return CONDITIONS.stream().filter(serializer -> serializer instanceof PollinatedRecipeConditionWrapper.Serializer && ((PollinatedRecipeConditionWrapper.Serializer) serializer).getCondition() == condition).findFirst().orElseThrow(() -> new IllegalStateException("Unregistered condition: " + condition));
    }

    public static boolean test(JsonObject json) {
        return CraftingHelper.processConditions(json, "conditions");
    }

    public static String getConditionsKey() {
        return "conditions";
    }
}
