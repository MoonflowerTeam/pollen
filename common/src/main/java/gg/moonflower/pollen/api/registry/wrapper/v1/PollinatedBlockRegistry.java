package gg.moonflower.pollen.api.registry.wrapper.v1;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.impl.registry.wrapper.PollinatedBlockRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A specialized registry for blocks to allow easier item block registry.
 *
 * @author Ocelot, Jackson
 * @since 2.0.0
 */
public interface PollinatedBlockRegistry extends PollinatedRegistry<Block> {

    static PollinatedBlockRegistry create(DeferredRegister<Item> itemRegistry) {
        return new PollinatedBlockRegistryImpl(DeferredRegister.create(itemRegistry.getRegistries().getModId(), Registry.BLOCK_REGISTRY), itemRegistry);
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
    <R extends Block> RegistrySupplier<R> registerWithItem(String id, Supplier<R> block, Item.Properties properties);

    /**
     * Registers a block with an item.
     *
     * @param id          The id of the block
     * @param block       The block to register
     * @param itemFactory The factory to create a new item from the registered block
     * @param <R>         The type of block being registered
     * @return The registered block
     */
    <R extends Block> RegistrySupplier<R> registerWithItem(String id, Supplier<R> block, Function<R, Item> itemFactory);
}
