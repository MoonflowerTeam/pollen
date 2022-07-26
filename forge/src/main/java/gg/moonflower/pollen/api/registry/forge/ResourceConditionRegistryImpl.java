package gg.moonflower.pollen.api.registry.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import gg.moonflower.pollen.api.resource.condition.forge.*;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
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
    public static void registerRecipeConditions(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
            return;

        CONDITIONS.forEach(CraftingHelper::register);
        CraftingHelper.register(BlockExistsCondition.Serializer.INSTANCE);
        CraftingHelper.register(FluidExistsCondition.Serializer.INSTANCE);
        CraftingHelper.register(new TagPopulatedCondition.Serializer<>(ItemTagPopulatedCondition.NAME, ItemTagPopulatedCondition::new));
        CraftingHelper.register(new TagPopulatedCondition.Serializer<>(BlockTagPopulatedCondition.NAME, BlockTagPopulatedCondition::new));
        CraftingHelper.register(new TagPopulatedCondition.Serializer<>(FluidTagPopulatedCondition.NAME, FluidTagPopulatedCondition::new));
    }

    public static IConditionSerializer<?> get(PollinatedResourceCondition condition) {
        return CONDITIONS.stream().filter(serializer -> serializer instanceof PollinatedRecipeConditionWrapper.Serializer && ((PollinatedRecipeConditionWrapper.Serializer) serializer).getCondition() == condition).findFirst().orElseThrow(() -> new IllegalStateException("Unregistered condition: " + condition));
    }

    public static boolean test(JsonObject json) {
        return CraftingHelper.processConditions(json, "conditions", ServerLifecycleHooks.getCurrentServer() == null ? ICondition.IContext.EMPTY : ServerLifecycleHooks.getCurrentServer().getServerResources().managers().getConditionContext());
    }

    public static String getConditionsKey() {
        return "conditions";
    }
}
