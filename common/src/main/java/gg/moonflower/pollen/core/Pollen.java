package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.command.PollenSuggestionProviders;
import gg.moonflower.pollen.api.command.argument.ColorArgumentType;
import gg.moonflower.pollen.api.command.argument.EnumArgument;
import gg.moonflower.pollen.api.command.argument.TimeArgumentType;
import gg.moonflower.pollen.api.config.ConfigManager;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import gg.moonflower.pollen.api.entity.PollenEntityTypes;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.ResourceConditionRegistry;
import gg.moonflower.pollen.api.registry.client.EntityRendererRegistry;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.loader.CosmeticModelLoader;
import gg.moonflower.pollen.core.client.loader.CosmeticTextureLoader;
import gg.moonflower.pollen.core.client.render.PollenShaderTypes;
import gg.moonflower.pollen.core.client.render.entity.PollinatedBoatRenderer;
import gg.moonflower.pollen.core.datagen.PollenLanguageProvider;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.resource.condition.ConfigResourceCondition;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.VanillaModelMapping;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class Pollen {

    public static final String MOD_ID = "pollen";
    public static final boolean TESTS_ENABLED;
    public static final Platform PLATFORM = Platform.builder(Pollen.MOD_ID)
            .commonInit(Pollen::onCommon)
            .clientInit(() -> Pollen::onClient)
            .commonPostInit(Pollen::onCommonPost)
            .clientPostInit(() -> Pollen::onClientPost)
            .dataInit(Pollen::onDataInit)
            .build();
    public static final PollenClientConfig CLIENT_CONFIG = ConfigManager.register(MOD_ID, PollinatedConfigType.CLIENT, PollenClientConfig::new);

    private static MinecraftServer server;

    static {
        TESTS_ENABLED = "true".equalsIgnoreCase(System.getProperty("pollen.enableTests"));
        if (TESTS_ENABLED)
            LogManager.getLogger().info("Pollen tests enabled");
    }

    private static void onClient() {
        VanillaModelMapping.load(); // Loads the class to prevent lag spikes in-game
        SyncedDataManager.initClient();
        ResourceModifierManager.initClient();
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
        PollenShaderTypes.init();
        GeometryModelManager.addLoader(new CosmeticModelLoader());
        GeometryTextureManager.addProvider(new CosmeticTextureLoader());
        DebugInputs.init();
        EntitlementManager.init();
        EntityRendererRegistry.register(PollenEntityTypes.BOAT, context -> new PollinatedBoatRenderer(context, false));
        EntityRendererRegistry.register(PollenEntityTypes.CHEST_BOAT, context -> new PollinatedBoatRenderer(context, true));

        if (TESTS_ENABLED)
            PollenTest.onClient();
    }

    private static void onCommon() {
        PollenSuggestionProviders.init();
        SyncedDataManager.init();
        ResourceModifierManager.init();
        ResourceConditionRegistry.register(ConfigResourceCondition.NAME, new ConfigResourceCondition());
        PollenRecipeTypes.RECIPE_SERIALIZERS.register(PLATFORM);
        PollenRecipeTypes.RECIPES.register(PLATFORM);
        PollenEntityTypes.ENTITY_TYPES.register(PLATFORM);
        if (TESTS_ENABLED)
            PollenTest.onCommon();
    }

    private static void onClientPost(Platform.ModSetupContext context) {
        if (TESTS_ENABLED)
            PollenTest.onClientPost(context);
    }

    private static void onCommonPost(Platform.ModSetupContext context) {
        ArgumentTypeInfos.register(Registry.COMMAND_ARGUMENT_TYPE, MOD_ID + ":color", ColorArgumentType.class, SingletonArgumentInfo.contextFree(ColorArgumentType::color));
        ArgumentTypeInfos.register(Registry.COMMAND_ARGUMENT_TYPE, MOD_ID + ":time", TimeArgumentType.class, new TimeArgumentType.Serializer());
        ArgumentTypeInfos.register(Registry.COMMAND_ARGUMENT_TYPE, MOD_ID + ":enum", EnumArgument.class, new EnumArgument.Serializer());
        ServerLifecycleEvents.PRE_STARTING.register(server -> {
            Pollen.server = server;
            return true;
        });
        ServerLifecycleEvents.STOPPED.register(server -> Pollen.server = null);
        PollenMessages.init();
        if (TESTS_ENABLED)
            PollenTest.onCommonPost(context);
    }

    private static void onDataInit(Platform.DataSetupContext context) {
        if (!TESTS_ENABLED)
            return;
        context.getGenerator().addProvider(true, new PollenLanguageProvider(context.getGenerator(), context.getMod()));
    }

    @Nullable
    public static MinecraftServer getRunningServer() {
        return server;
    }


}
