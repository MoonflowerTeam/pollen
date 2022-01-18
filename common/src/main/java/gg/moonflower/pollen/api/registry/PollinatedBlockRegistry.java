package gg.moonflower.pollen.api.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
}
