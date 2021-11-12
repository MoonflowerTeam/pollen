package gg.moonflower.pollen.core.brewing.fabric;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.core.brewing.PollenBrewingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenBrewingRecipeImpl {

    public static RecipeSerializer<PollenBrewingRecipe> createSerializer() {
        return new RecipeSerializer<PollenBrewingRecipe>() {
            @Override
            public PollenBrewingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
                return PollenBrewingRecipe.fromJson(resourceLocation, jsonObject);
            }

            @Override
            public PollenBrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                return PollenBrewingRecipe.fromNetwork(id, buf);
            }

            @Override
            public void toNetwork(FriendlyByteBuf buf, PollenBrewingRecipe recipe) {
                PollenBrewingRecipe.toNetwork(buf, recipe);
            }
        };
    }
}
