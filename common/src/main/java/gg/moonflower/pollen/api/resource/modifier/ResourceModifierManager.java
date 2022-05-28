package gg.moonflower.pollen.api.resource.modifier;

import com.google.common.base.Suppliers;
import com.google.gson.*;
import gg.moonflower.pollen.api.event.events.AdvancementConstructingEvent;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.event.events.client.resource.ModelEvents;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ReloadStartListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.api.resource.modifier.serializer.DataModifierSerializer;
import gg.moonflower.pollen.api.resource.modifier.serializer.ResourceModifierSerializer;
import gg.moonflower.pollen.api.resource.modifier.type.AdvancementModifier;
import gg.moonflower.pollen.api.resource.modifier.type.LootModifier;
import gg.moonflower.pollen.api.resource.modifier.type.ModelOverrideModifier;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Manages all custom modifiers for resources and data.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class ResourceModifierManager {

    public static final PollinatedRegistry<ResourceModifierType> REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(Pollen.MOD_ID, "resource_modifier"));

    public static final Supplier<ResourceModifierType> ADVANCEMENT = REGISTRY.register("advancement", () -> ResourceModifierType.create(AdvancementModifier.Builder::fromJson));
    public static final Supplier<ResourceModifierType> LOOT = REGISTRY.register("loot", () -> ResourceModifierType.create(LootModifier.Builder::fromJson));
    public static final Supplier<ResourceModifierType> MODEL_OVERRIDE = REGISTRY.register("model_override", () -> ResourceModifierType.create(ModelOverrideModifier.Builder::fromJson));

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, ResourceModifier<?>> DATA_MODIFIERS = new HashMap<>();
    private static final Map<ResourceLocation, ResourceModifier<?>> RESOURCE_MODIFIERS = new HashMap<>();

    private static volatile SidedReloader serverReloader = null;
    private static final Supplier<SidedReloader> CLIENT_RELOADER = Suppliers.memoize(() -> new SidedReloader("resource", RESOURCE_MODIFIERS, type -> (n, json, inject, priority) -> {
        if (!(type.getSerializer() instanceof ResourceModifierSerializer))
            throw new JsonSyntaxException(REGISTRY.getKey(type) + " is not a resource modifier");
        return ((ResourceModifierSerializer) type.getSerializer()).deserialize(n, json, inject, priority);
    }));

    private ResourceModifierManager() {
    }

    @ApiStatus.Internal
    public static void init() {
        AdvancementConstructingEvent.EVENT.register((builder, context) -> getDataModifiersFor(ADVANCEMENT.get(), context.getAdvancementId()).forEachOrdered(modifier -> {
            try {
                modifier.modify(builder);
            } catch (Exception e) {
                LOGGER.error("Failed to apply advancement modifier {}: {}", modifier.getId(), e.getMessage());
            }
        }));
        LootTableConstructingEvent.EVENT.register(context -> {
            ResourceLocation id = context.getId();
            getDataModifiersFor(LOOT.get(), id).forEachOrdered(modifier -> {
                try {
                    modifier.modify(context);
                } catch (Exception e) {
                    LOGGER.error("Failed to apply loot modifier {}: {}", modifier.getId(), e.getMessage());
                }
            });
        });
    }

    @ApiStatus.Internal
    public static void initClient() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, CLIENT_RELOADER.get());

        ModelEvents.LOAD_BLOCK_MODEL.register((location, model) -> getResourceModifiersFor(MODEL_OVERRIDE.get(), location).forEachOrdered(modifier -> {
            try {
                modifier.modify(model);
            } catch (Exception e) {
                LOGGER.error("Failed to apply model override modifier {}: {}", modifier.getId(), e.getMessage());
            }
        }));
    }

    @ApiStatus.Internal
    public static PreparableReloadListener createServerReloader(ReloadableServerResources serverResources) {
        return serverReloader = new SidedReloader("data", DATA_MODIFIERS, type -> (n, json, inject, priority) -> {
            if (!(type.getSerializer() instanceof DataModifierSerializer))
                throw new JsonSyntaxException(REGISTRY.getKey(type) + " is not a data modifier");
            return ((DataModifierSerializer) type.getSerializer()).deserialize(n, serverResources, json, inject, priority);
        });
    }

    @ApiStatus.Internal
    public static CompletableFuture<Void> getServerCompleteFuture() {
        if (serverReloader == null)
            throw new NullPointerException("Expected to wait for resource modifiers, but serverReloader was null");
        if (serverReloader.getCompleteFuture() == null)
            throw new NullPointerException("Expected to wait for resource modifiers, but serverReloader#getCompleteFuture() returned null");
        return serverReloader.getCompleteFuture();
    }

    @ApiStatus.Internal
    public static CompletableFuture<Void> getClientCompleteFuture() {
        return CLIENT_RELOADER.get().getCompleteFuture();
    }

    /**
     * Retrieves a modifier with the specified id.
     *
     * @param id The id of the modifier to retrieve
     * @return The modifier with that id or <code>null</code> if there is no modifier with that id
     */
    @Nullable
    public static ResourceModifier<?> getDataModifier(ResourceLocation id) {
        return DATA_MODIFIERS.get(id);
    }

    /**
     * Retrieves a modifier with the specified id.
     *
     * @param id The id of the modifier to retrieve
     * @return The modifier with that id or <code>null</code> if there is no modifier with that id
     */
    @Nullable
    public static ResourceModifier<?> getResourceModifier(ResourceLocation id) {
        return RESOURCE_MODIFIERS.get(id);
    }

    /**
     * Creates a stream of all modifiers that modify the resource with the specified id.
     *
     * @param type The type of resource to modify
     * @param <T>  The type of object the resource modifies
     * @param id   The id of the resource to modify
     * @return The modifiers for that resource
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<ResourceModifier<T>> getDataModifiersFor(ResourceModifierType type, ResourceLocation id) {
        return DATA_MODIFIERS.values().stream().filter(modifier -> modifier.getType() == type && ArrayUtils.contains(modifier.getInject(), id)).map(resourceModifier -> (ResourceModifier<T>) resourceModifier).sorted(Comparator.<ResourceModifier<T>>comparingInt(ResourceModifier::getInjectPriority).reversed());
    }

    /**
     * Creates a stream of all modifiers that modify the resource with the specified id.
     *
     * @param type The type of resource to modify
     * @param <T>  The type of object the resource modifies
     * @param id   The id of the resource to modify
     * @return The modifiers for that resource
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<ResourceModifier<T>> getResourceModifiersFor(ResourceModifierType type, ResourceLocation id) {
        return RESOURCE_MODIFIERS.values().stream().filter(modifier -> modifier.getType() == type && ArrayUtils.contains(modifier.getInject(), id)).map(resourceModifier -> (ResourceModifier<T>) resourceModifier).sorted(Comparator.<ResourceModifier<T>>comparingInt(ResourceModifier::getInjectPriority).reversed());
    }

    private static ResourceModifier<?> deserialize(ResourceLocation name, JsonElement element, Function<ResourceModifierType, ResourceModifierSerializer> function) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "modifier");
        if (!json.has("inject"))
            throw new JsonSyntaxException("Missing inject, expected to find a String or JsonArray");
        ResourceLocation typeName = new ResourceLocation(GsonHelper.getAsString(json, "type"));
        ResourceModifierType type = REGISTRY.getOptional(typeName).orElseThrow(() -> new JsonSyntaxException("Unknown resource type: " + typeName));

        JsonElement injectElement = json.get("inject");
        if (!(injectElement.isJsonPrimitive() && injectElement.getAsJsonPrimitive().isString()) && !injectElement.isJsonArray())
            throw new JsonSyntaxException("Expected inject to be a String or JsonArray, was " + GsonHelper.getType(injectElement));
        ResourceLocation[] inject = injectElement.isJsonPrimitive() && injectElement.getAsJsonPrimitive().isString() ? new ResourceLocation[]{new ResourceLocation(GsonHelper.convertToString(injectElement, "inject"))} : JSONTupleParser.getArray(json, "inject", new ResourceLocation[0], 1, ResourceLocation::new);
        int priority = GsonHelper.getAsInt(json, "injectPriority", 1000);

        return function.apply(type).deserialize(name, json, inject, priority).build(name);
    }

    private static class SidedReloader implements ReloadStartListener, PollinatedPreparableReloadListener {

        private static final Gson GSON = new GsonBuilder().create();

        private final String type;
        private final PreparableReloadListener listener;
        private CompletableFuture<Void> completeFuture;

        private SidedReloader(String type, Map<ResourceLocation, ResourceModifier<?>> resourceModifiers, Function<ResourceModifierType, ResourceModifierSerializer> function) {
            this.type = type;
            this.listener = new SimpleJsonResourceReloadListener(GSON, "resource_modifiers") {
                @Override
                protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                    Map<ResourceLocation, ResourceModifier<?>> modifiers = new HashMap<>();
                    map.forEach((name, element) -> {
                        try {
                            modifiers.put(name, deserialize(name, element, function));
                        } catch (Exception e) {
                            LOGGER.error("Parsing error loading custom {} modifier {}: {}", type, name, e.getMessage());
                        }
                    });
                    resourceModifiers.clear();
                    resourceModifiers.putAll(modifiers);

                    LOGGER.info("Loaded {} {} modifiers", modifiers.size(), type);
                }
            };
            this.completeFuture = CompletableFuture.completedFuture(null);
        }

        @Override
        public void onReloadStart(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
            this.completeFuture = this.listener.reload(CompletableFuture::completedFuture, resourceManager, InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Runnable::run, gameExecutor);
        }

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return this.completeFuture.thenCompose(stage::wait);
        }

        @Override
        public ResourceLocation getPollenId() {
            return new ResourceLocation(Pollen.MOD_ID, this.type + "_modifiers");
        }

        public CompletableFuture<Void> getCompleteFuture() {
            return completeFuture;
        }
    }
}
