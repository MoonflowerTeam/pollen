package gg.moonflower.pollen.api.crafting.v1;

import com.google.gson.JsonObject;
import dev.architectury.registry.registries.DeferredRegister;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.crafting.PollenBrewingRecipeImpl;
import gg.moonflower.pollen.core.crafting.PollenShapelessGrindstoneRecipeImpl;
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
 * @since 2.0.0
 */
public final class PollenRecipeTypes {

    @ApiStatus.Internal
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Pollen.MOD_ID, Registry.RECIPE_SERIALIZER_REGISTRY);
    @ApiStatus.Internal
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(Pollen.MOD_ID, Registry.RECIPE_TYPE_REGISTRY);

    public static final Supplier<RecipeType<PollenBrewingRecipe>> BREWING_TYPE = register("brewing");
    public static final Supplier<RecipeSerializer<PollenBrewingRecipe>> BREWING = RECIPE_SERIALIZERS.register("brewing", () -> createSerializer(PollenBrewingRecipeImpl::fromJson, PollenBrewingRecipeImpl::fromNetwork, PollenBrewingRecipeImpl::toNetwork));

    public static final Supplier<RecipeType<PollenGrindstoneRecipe>> GRINDSTONE_TYPE = register("grindstone");
    public static final Supplier<RecipeSerializer<PollenShapelessGrindstoneRecipeImpl>> GRINDSTONE = RECIPE_SERIALIZERS.register("grindstone", () -> createSerializer(PollenShapelessGrindstoneRecipeImpl::fromJson, PollenShapelessGrindstoneRecipeImpl::fromNetwork, PollenShapelessGrindstoneRecipeImpl::toNetwork));

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
    public static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(BiFunction<ResourceLocation, JsonObject, T> fromJson, BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork, BiConsumer<FriendlyByteBuf, T> toNetwork) {
        return new PollenRecipeSerializer<>(fromJson, fromNetwork, toNetwork);
    }

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String name) {
        return RECIPES.register(name, () -> new RecipeType<T>() {
            @Override
            public String toString() {
                return Pollen.MOD_ID + ":" + name;
            }
        });
    }

    private record PollenRecipeSerializer<T extends Recipe<?>>(BiFunction<ResourceLocation, JsonObject, T> fromJson,
                                                               BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork,
                                                               BiConsumer<FriendlyByteBuf, T> toNetwork) implements RecipeSerializer<T> {

        @Override
        public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return this.fromJson.apply(resourceLocation, jsonObject);
        }

        @Override
        public T fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return this.fromNetwork.apply(id, buf);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            this.toNetwork.accept(buf, recipe);
        }
    }
}
