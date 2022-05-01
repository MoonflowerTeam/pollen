package gg.moonflower.pollen.api.crafting;

import com.google.gson.JsonObject;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.crafting.grindstone.PollenGrindstoneRecipe;
import gg.moonflower.pollen.api.crafting.grindstone.PollenShapelessGrindstoneRecipe;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Built-in Pollen recipes types and implementations.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class PollenRecipeTypes {

    @ApiStatus.Internal
    public static final PollinatedRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = PollinatedRegistry.create(Registry.RECIPE_SERIALIZER, Pollen.MOD_ID);
    @ApiStatus.Internal
    public static final PollinatedRegistry<RecipeType<?>> RECIPES = PollinatedRegistry.create(Registry.RECIPE_TYPE, Pollen.MOD_ID);

    public static final Supplier<RecipeType<PollenBrewingRecipe>> BREWING_TYPE = register("brewing");
    public static final Supplier<RecipeSerializer<PollenBrewingRecipe>> BREWING = RECIPE_SERIALIZERS.register("brewing", () -> createSerializer(PollenBrewingRecipe::fromJson, PollenBrewingRecipe::fromNetwork, PollenBrewingRecipe::toNetwork));

    public static final Supplier<RecipeType<PollenGrindstoneRecipe>> GRINDSTONE_TYPE = register("grindstone");
    public static final Supplier<RecipeSerializer<PollenShapelessGrindstoneRecipe>> GRINDSTONE = RECIPE_SERIALIZERS.register("grindstone", () -> createSerializer(PollenShapelessGrindstoneRecipe::fromJson, PollenShapelessGrindstoneRecipe::fromNetwork, PollenShapelessGrindstoneRecipe::toNetwork));

    private PollenRecipeTypes() {
    }

    /**
     * Creates a multiplatform recipe serializer.
     *
     * @param fromJson    The function to create a recipe from JSON
     * @param fromNetwork The function to create a recipe from network
     * @param toNetwork   The function to write a recipe into network
     * @param <T>         The type of recipe to make a serializer for
     * @return A new serializer for recipes
     */
    @ExpectPlatform
    public static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(BiFunction<ResourceLocation, JsonObject, T> fromJson, BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork, BiConsumer<FriendlyByteBuf, T> toNetwork) {
        return Platform.error();
    }

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String name) {
        return RECIPES.register(name, () -> new RecipeType<T>() {
            @Override
            public String toString() {
                return Pollen.MOD_ID + ":" + name;
            }
        });
    }
}
