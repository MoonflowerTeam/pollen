package gg.moonflower.pollen.core.mixin.fabric.data;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiFunction;

@Mixin(RecipeProvider.class)
public interface RecipeProviderAccessor {

    @Accessor
    static BiFunction<ItemLike, ItemLike, RecipeBuilder> getShapeBuilders() {
        return null;
    }
}
