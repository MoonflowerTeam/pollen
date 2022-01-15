package gg.moonflower.pollen.api.crafting.fabric;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@ApiStatus.Internal
public class PollenRecipeTypesImpl {

    public static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(BiFunction<ResourceLocation, JsonObject, T> fromJson, BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork, BiConsumer<FriendlyByteBuf, T> toNetwork) {
        return new FabricRecipeSerializer<>(fromJson, fromNetwork, toNetwork);
    }

    private static class FabricRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

        private final BiFunction<ResourceLocation, JsonObject, T> fromJson;
        private final BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork;
        private final BiConsumer<FriendlyByteBuf, T> toNetwork;

        private FabricRecipeSerializer(BiFunction<ResourceLocation, JsonObject, T> fromJson, BiFunction<ResourceLocation, FriendlyByteBuf, T> fromNetwork, BiConsumer<FriendlyByteBuf, T> toNetwork) {
            this.fromJson = fromJson;
            this.fromNetwork = fromNetwork;
            this.toNetwork = toNetwork;
        }

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
