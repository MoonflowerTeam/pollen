package gg.moonflower.pollen.impl.registry.wrapper;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.api.registry.wrapper.v1.PollinatedBlockRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedBlockRegistryImpl extends PollinatedRegistryImpl<Block> implements PollinatedBlockRegistry {

    private final DeferredRegister<Item> itemRegistry;

    public PollinatedBlockRegistryImpl(DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry) {
        super(blockRegistry);
        this.itemRegistry = itemRegistry;
    }

    public <R extends Block> RegistrySupplier<R> registerWithItem(String id, Supplier<R> block, Item.Properties properties) {
        return this.registerWithItem(id, block, object -> new BlockItem(object, properties));
    }

    public <R extends Block> RegistrySupplier<R> registerWithItem(String id, Supplier<R> block, Function<R, Item> itemFactory) {
        RegistrySupplier<R> register = this.register(id, block);
        this.itemRegistry.register(id, () -> itemFactory.apply(register.get()));
        return register;
    }

    // TODO: sign api
//
//    /**
//     * Registers a standing and wall sign block and sign item with a material and material color.
//     *
//     * @param id       The name of the sign
//     * @param material The material of the sign blocks
//     * @param color    The material color of the sign blocks
//     * @return A pair of a wall and standing sign block supplier
//     */
//    public Pair<RegistrySupplier<PollinatedStandingSignBlock>, RegistrySupplier<PollinatedWallSignBlock>> registerSign(String id, Material material, MaterialColor color) {
//        return this.registerSign(id, BlockBehaviour.Properties.of(material, color).noCollission().strength(1.0F).sound(SoundType.WOOD), new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS));
//    }
//
//    /**
//     * Registers a standing and wall sign block and sign item with custom properties.
//     *
//     * @param id              The name of the sign
//     * @param blockProperties The properties of the sign blocks
//     * @param itemProperties  The properties of the sign item
//     * @return A pair of a wall and standing sign block supplier
//     */
//    public Pair<RegistrySupplier<PollinatedStandingSignBlock>, RegistrySupplier<PollinatedWallSignBlock>> registerSign(String id, BlockBehaviour.Properties blockProperties, Item.Properties itemProperties) {
//        WoodType type = SignRegistry.register(new ResourceLocation(this.modId, id));
//
//        RegistryValue<PollinatedStandingSignBlock> standing = this.register(id + "_sign", () -> new PollinatedStandingSignBlock(blockProperties, type));
//        RegistryValue<PollinatedWallSignBlock> wall = this.register(id + "_wall_sign", () -> new PollinatedWallSignBlock(blockProperties.dropsLike(standing.get()), type));
//
//        this.itemRegistry.register(id + "_sign", () -> new PollinatedSignItem(itemProperties, standing.get(), wall.get()));
//
//        return Pair.of(standing, wall);
//    }
}
