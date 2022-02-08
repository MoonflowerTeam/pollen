package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.command.PollenSuggestionProviders;
import gg.moonflower.pollen.api.command.argument.ColorArgumentType;
import gg.moonflower.pollen.api.command.argument.EnumArgument;
import gg.moonflower.pollen.api.command.argument.TimeArgumentType;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.event.events.registry.client.RegisterAtlasSpriteEvent;
import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.loader.CosmeticModelLoader;
import gg.moonflower.pollen.core.client.loader.CosmeticTextureLoader;
import gg.moonflower.pollen.core.datagen.PollenLanguageProvider;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.shader.ShaderConst;
import gg.moonflower.pollen.pinwheel.api.client.shader.ShaderLoader;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class Pollen {

    public static final String MOD_ID = "pollen";
    public static final Platform PLATFORM = Platform.builder(Pollen.MOD_ID)
            .commonInit(Pollen::onCommon)
            .clientInit(Pollen::onClient)
            .commonPostInit(Pollen::onCommonPost)
            .clientPostInit(Pollen::onClientPost)
            .dataInit(Pollen::onDataInit)
            .build();

    private static MinecraftServer server;

    public static void init() {
        PollenSuggestionProviders.init();
        if (!Platform.isProduction())
            PollenTest.init();
    }

    private static void onClient() {
        SyncedDataManager.initClient();
        ResourceModifierManager.initClient();
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
        GeometryModelManager.addLoader(new CosmeticModelLoader());
        GeometryTextureManager.addProvider(new CosmeticTextureLoader());
        ShaderLoader.init();
        DebugInputs.init();
        EntitlementManager.init();
        InitRendererEvent.EVENT.register(ShaderConst::init);
        RegisterAtlasSpriteEvent.event(InventoryMenu.BLOCK_ATLAS).register((atlas, registry) -> {
            for (Fluid fluid : Registry.FLUID) {
                if (!(fluid instanceof PollinatedFluid))
                    return;
                PollinatedFluid pollinatedFluid = (PollinatedFluid) fluid;
                registry.accept(pollinatedFluid.getStillTextureName());
                registry.accept(pollinatedFluid.getFlowingTextureName());
            }
        });
        if (!Platform.isProduction())
            PollenTest.onClient();
    }

    private static void onCommon() {
        SyncedDataManager.init();
        ResourceModifierManager.init();
        PollenRecipeTypes.RECIPE_SERIALIZERS.register(PLATFORM);
        PollenRecipeTypes.RECIPES.register(PLATFORM);
        if (!Platform.isProduction())
            PollenTest.onCommon();
    }

    private static void onClientPost(Platform.ModSetupContext context) {
        if (!Platform.isProduction())
            PollenTest.onClientPost(context);
    }

    private static void onCommonPost(Platform.ModSetupContext context) {
        ArgumentTypes.register(MOD_ID + ":color", ColorArgumentType.class, new EmptyArgumentSerializer<>(ColorArgumentType::new));
        ArgumentTypes.register(MOD_ID + ":time", TimeArgumentType.class, new TimeArgumentType.Serializer());
        ArgumentTypes.register(MOD_ID + ":enum", EnumArgument.class, new EnumArgument.Serializer());
        ServerLifecycleEvents.PRE_STARTING.register(server -> {
            Pollen.server = server;
            return true;
        });
        ServerLifecycleEvents.STOPPED.register(server -> Pollen.server = null);
        PollenMessages.init();
        if (!Platform.isProduction())
            PollenTest.onCommonPost(context);
    }

    private static void onDataInit(Platform.DataSetupContext context) {
        context.getGenerator().addProvider(new PollenLanguageProvider(context.getGenerator(), context.getMod()));
    }

    @Nullable
    public static MinecraftServer getRunningServer() {
        return server;
    }
}
