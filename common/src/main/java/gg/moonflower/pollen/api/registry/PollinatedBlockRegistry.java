package gg.moonflower.pollen.api.registry;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.block.PollinatedStandingSignBlock;
import gg.moonflower.pollen.api.block.PollinatedWallSignBlock;
import gg.moonflower.pollen.api.item.PollinatedSignItem;
import gg.moonflower.pollen.api.registry.content.SignRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A specialized registry for blocks to allow easier item block registry.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedBlockRegistry extends WrapperPollinatedRegistry<Block> {

    private final PollinatedRegistry<Item> itemRegistry;

    PollinatedBlockRegistry(PollinatedRegistry<Block> blockRegistry, PollinatedRegistry<Item> itemRegistry) {
        super(blockRegistry);
        this.itemRegistry = itemRegistry;
    }

    /**
     * Registers a block with a simple item.
     *
     * @param id         The id of the block
     * @param block      The block to register
     * @param properties The properties of the item to register
     * @param <R>        The type of block being registered
     * @return The registered block
     */
    public <R extends Block> Supplier<R> registerWithItem(String id, Supplier<R> block, Item.Properties properties) {
        return this.registerWithItem(id, block, object -> new BlockItem(object, properties));
    }

    /**
     * Registers a block with an item.
     *
     * @param id          The id of the block
     * @param block       The block to register
     * @param itemFactory The factory to create a new item from the registered block
     * @param <R>         The type of block being registered
     * @return The registered block
     */
    public <R extends Block> Supplier<R> registerWithItem(String id, Supplier<R> block, Function<R, Item> itemFactory) {
        Supplier<R> register = this.register(id, block);
        this.itemRegistry.register(id, () -> itemFactory.apply(register.get()));
        return register;
    }

    /**
     * Registers a standing and wall sign block and sign item with a material and material color.
     *
     * @param id       The name of the sign
     * @param material The material of the sign blocks
     * @param color    The material color of the sign blocks
     * @return A pair of a wall and standing sign block supplier
     */
    public Pair<Supplier<PollinatedStandingSignBlock>, Supplier<PollinatedWallSignBlock>> registerSign(String id, Material material, MaterialColor color) {
        return this.registerSign(id, BlockBehaviour.Properties.of(material, color).noCollission().strength(1.0F).sound(SoundType.WOOD), new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS));
    }

    /**
     * Registers a standing and wall sign block and sign item with custom properties.
     *
     * @param id              The name of the sign
     * @param blockProperties The properties of the sign blocks
     * @param itemProperties  The properties of the sign item
     * @return A pair of a wall and standing sign block supplier
     */
    public Pair<Supplier<PollinatedStandingSignBlock>, Supplier<PollinatedWallSignBlock>> registerSign(String id, BlockBehaviour.Properties blockProperties, Item.Properties itemProperties) {
        WoodType type = SignRegistry.register(new ResourceLocation(this.modId, id));

        Supplier<PollinatedStandingSignBlock> standing = this.register(id + "_sign", () -> new PollinatedStandingSignBlock(blockProperties, type));
        Supplier<PollinatedWallSignBlock> wall = this.register(id + "_wall_sign", () -> new PollinatedWallSignBlock(blockProperties.dropsLike(standing.get()), type));

        this.itemRegistry.register(id + "_sign", () -> new PollinatedSignItem(itemProperties, standing.get(), wall.get()));

        return Pair.of(standing, wall);
    }
}
