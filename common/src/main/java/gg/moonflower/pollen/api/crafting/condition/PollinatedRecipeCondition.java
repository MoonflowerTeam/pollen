package gg.moonflower.pollen.api.crafting.condition;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * A common condition for checking if a recipe should be loaded.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedRecipeCondition {

    /**
     * Tests to see if the provided JSON should allow the recipe to load.
     *
     * @param json The JSON to check
     * @return Whether the recipe passes this condition
     * @throws JsonParseException If any errors occurs when reading from the JSON
     */
    boolean test(JsonObject json) throws JsonParseException;

    /**
     * An <code>and</code> operator condition for use with data generators.
     *
     * @param values The conditions to check
     * @return A condition checking if all the specified conditions are true
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider and(PollinatedRecipeConditionProvider... values) {
        return Platform.error();
    }

    /**
     * A static <code>false</code> condition for use with data generators.
     *
     * @return A condition always returning false
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider FALSE() {
        return Platform.error();
    }

    /**
     * A static <code>true</code> condition for use with data generators.
     *
     * @return A condition always returning true
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider TRUE() {
        return Platform.error();
    }

    /**
     * Inverts the specified condition for use with data generators.
     *
     * @param value The conditions to invert
     * @return A condition checking if all the specified condition is false
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider not(PollinatedRecipeConditionProvider value) {
        return Platform.error();
    }

    /**
     * An <code>or</code> operator condition for use with data generators.
     *
     * @param values The conditions to check
     * @return A condition checking if any of the specified conditions are true
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider or(PollinatedRecipeConditionProvider... values) {
        return Platform.error();
    }

    /**
     * Checks to see if a block is registered with the specified name.
     *
     * @param name The name of the block
     * @return A condition checking if the block is registered
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider blockExists(ResourceLocation name) {
        return Platform.error();
    }

    /**
     * Checks to see if an item is registered with the specified name.
     *
     * @param name The name of the item
     * @return A condition checking if the item is registered
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider itemExists(ResourceLocation name) {
        return Platform.error();
    }

    /**
     * Checks to see if a fluid is registered with the specified name.
     *
     * @param name The name of the fluid
     * @return A condition checking if the fluid is registered
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider fluidExists(ResourceLocation name) {
        return Platform.error();
    }

    /**
     * Checks to see if the item tag exists and has at least one registered item in it.
     *
     * @param tag The item tag to check
     * @return A condition checking if the item is registered
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider itemTagPopulated(Tag.Named<Item> tag) {
        return Platform.error();
    }

    /**
     * Checks to see if the block tag exists and has at least one registered block in it.
     *
     * @param tag The block tag to check
     * @return A condition checking if the block is registered
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider blockTagPopulated(Tag.Named<Block> tag) {
        return Platform.error();
    }

    /**
     * Checks to see if the fluid tag exists and has at least one registered fluid in it.
     *
     * @param tag The fluid tag to check
     * @return A condition checking if the fluid is registered
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider fluidTagPopulated(Tag.Named<Fluid> tag) {
        return Platform.error();
    }

    /**
     * Checks to see if all the specified mods are loaded.
     *
     * @param modIds The IDs of the mods to check
     * @return A condition checking if all mod are loaded
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider allModsLoaded(String... modIds) {
        return Platform.error();
    }

    /**
     * Checks to see if any the specified mods are loaded.
     *
     * @param modIds The IDs of the mods to check
     * @return A condition checking if any mod is loaded
     */
    @ExpectPlatform
    static PollinatedRecipeConditionProvider anyModsLoaded(String... modIds) {
        return Platform.error();
    }
}
