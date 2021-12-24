package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.advancement.AdvancementModifierManager;
import gg.moonflower.pollen.api.client.shader.ShaderConst;
import gg.moonflower.pollen.api.client.shader.ShaderLoader;
import gg.moonflower.pollen.api.command.PollenSuggestionProviders;
import gg.moonflower.pollen.api.command.argument.ColorArgumentType;
import gg.moonflower.pollen.api.command.argument.EnumArgument;
import gg.moonflower.pollen.api.command.argument.TimeArgumentType;
import gg.moonflower.pollen.api.crafting.brewing.PollenBrewingRecipe;
import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@ApiStatus.Internal
public class Pollen {

    public static final String MOD_ID = "pollen";
    public static final RecipeType<PollenBrewingRecipe> BREWING = RecipeType.register(MOD_ID + ":brewing");
    private static final PollinatedRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = PollinatedRegistry.create(Registry.RECIPE_SERIALIZER, MOD_ID);
    public static final Supplier<RecipeSerializer<PollenBrewingRecipe>> BREWING_SERIALIZER = RECIPE_SERIALIZERS.register("brewing", PollenBrewingRecipe::createSerializer);
    private static MinecraftServer server;    public static final Platform PLATFORM = Platform.builder(Pollen.MOD_ID)
            .commonInit(Pollen::onCommon)
            .clientInit(Pollen::onClient)
            .commonPostInit(Pollen::onCommonPost)
            .clientPostInit(Pollen::onClientPost)
            .build();

    public static void init() {
        PollenSuggestionProviders.init();
    }

    private static void onClient() {
        SyncedDataManager.initClient();
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
        AdvancementModifierManager.init();
        ShaderLoader.init();
        InitRendererEvent.EVENT.register(ShaderConst::init);
    }

    private static void onCommon() {
        SyncedDataManager.init();
        RECIPE_SERIALIZERS.register(PLATFORM);
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

    @Nullable
    public static MinecraftServer getRunningServer() {
        return server;
    }




}
