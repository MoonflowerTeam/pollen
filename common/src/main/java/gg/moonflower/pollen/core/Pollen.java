package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.advancement.AdvancementModifierManager;
import gg.moonflower.pollen.api.client.model.ItemOverrideModifierManager;
import gg.moonflower.pollen.api.command.PollenSuggestionProviders;
import gg.moonflower.pollen.api.command.argument.ColorArgumentType;
import gg.moonflower.pollen.api.command.argument.EnumArgument;
import gg.moonflower.pollen.api.command.argument.TimeArgumentType;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import gg.moonflower.pollen.api.event.events.client.render.AddRenderLayersEvent;
import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.loader.CosmeticModelLoader;
import gg.moonflower.pollen.core.client.loader.CosmeticTextureLoader;
import gg.moonflower.pollen.core.client.render.DebugPollenFlowerPotRenderer;
import gg.moonflower.pollen.core.client.render.layer.PollenCosmeticLayer;
import gg.moonflower.pollen.core.datagen.PollenLanguageProvider;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.shader.ShaderConst;
import gg.moonflower.pollen.pinwheel.api.client.shader.ShaderLoader;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
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
    }

    private static void onClient() {
        SyncedDataManager.initClient();
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
        GeometryModelManager.addLoader(new CosmeticModelLoader());
        GeometryTextureManager.addProvider(new CosmeticTextureLoader());
        ItemOverrideModifierManager.init();
        ShaderLoader.init();
        DebugInputs.init();
        EntitlementManager.init();
        InitRendererEvent.EVENT.register(ShaderConst::init);
        AddRenderLayersEvent.EVENT.register(context -> {
            for (String skin : context.getSkins())
                context.getSkin(skin).addLayer(new PollenCosmeticLayer<>(context.getSkin(skin)));
        });
        if (!Platform.isProduction())
            BlockRendererRegistry.register(Blocks.FLOWER_POT, new DebugPollenFlowerPotRenderer());
    }

    private static void onCommon() {
        SyncedDataManager.init();
        AdvancementModifierManager.init();
        PollenRecipeTypes.RECIPE_SERIALIZERS.register(PLATFORM);
        PollenRecipeTypes.RECIPES.register(PLATFORM);
    }

    private static void onClientPost(Platform.ModSetupContext context) {
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
    }

    private static void onDataInit(Platform.DataSetupContext context) {
        context.getGenerator().addProvider(new PollenLanguageProvider(context.getGenerator(), context.getMod()));
    }

    @Nullable
    public static MinecraftServer getRunningServer() {
        return server;
    }
}
