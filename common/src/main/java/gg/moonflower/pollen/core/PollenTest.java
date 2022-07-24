package gg.moonflower.pollen.core;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.block.PollinatedLiquidBlock;
import gg.moonflower.pollen.api.block.PollinatedStandingSignBlock;
import gg.moonflower.pollen.api.block.PollinatedWallSignBlock;
import gg.moonflower.pollen.api.entity.PollinatedBoatType;
import gg.moonflower.pollen.api.item.BucketItemBase;
import gg.moonflower.pollen.api.item.PollinatedBoatItem;
import gg.moonflower.pollen.api.item.SpawnEggItemBase;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import gg.moonflower.pollen.api.registry.PollinatedBlockRegistry;
import gg.moonflower.pollen.api.registry.PollinatedFluidRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.content.CompostablesRegistry;
import gg.moonflower.pollen.api.registry.content.DispenseItemBehaviorRegistry;
import gg.moonflower.pollen.api.registry.content.FlammabilityRegistry;
import gg.moonflower.pollen.api.registry.content.FurnaceFuelRegistry;
import gg.moonflower.pollen.api.registry.resource.TagRegistry;
import gg.moonflower.pollen.core.client.render.DebugPollenFlowerPotRenderer;
import gg.moonflower.pollen.core.test.TestFluid;
import gg.moonflower.pollen.core.test.TestPollenFluidBehavior;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollenTest {

    private static final PollinatedRegistry<Item> ITEMS = create(() -> PollinatedRegistry.create(Registry.ITEM, Pollen.MOD_ID));
    private static final PollinatedBlockRegistry BLOCKS = create(() -> PollinatedRegistry.createBlock(ITEMS));
    private static final PollinatedFluidRegistry FLUIDS = create(() -> PollinatedRegistry.createFluid(Pollen.MOD_ID));
    private static final PollinatedRegistry<PollinatedBoatType> BOATS = create(() -> PollinatedRegistry.create(PollenRegistries.BOAT_TYPE_REGISTRY, Pollen.MOD_ID));

    public static final TagKey<Fluid> TEST_TAG = create(() -> TagRegistry.bindFluid(new ResourceLocation(Pollen.MOD_ID, "test")));

    public static final Supplier<PollinatedBoatType> TEST_BOAT = create(() -> Objects.requireNonNull(BOATS).register("test_boat", () -> new PollinatedBoatType(new ResourceLocation("textures/entity/ghast/ghast.png"), new ResourceLocation("textures/entity/ghast/ghast_shooting.png"))));
    public static final Supplier<FlowingFluid> TEST_FLUID = create(() -> Objects.requireNonNull(FLUIDS).register("test", TestFluid.Source::new));
    public static final Supplier<FlowingFluid> FLOWING_TEST_FLUID = create(() -> Objects.requireNonNull(FLUIDS).register("flowing_test", TestFluid.Flowing::new));
    public static final Supplier<Block> TEST = create(() -> Objects.requireNonNull(BLOCKS).register("test", () -> new PollinatedLiquidBlock(TEST_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable())));
    public static final Supplier<Item> TEST_BUCKET = create(() -> Objects.requireNonNull(ITEMS).register("test", () -> new BucketItemBase(TEST_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC))));
    public static final Supplier<Item> TEST_SPAWN_EGG = create(() -> Objects.requireNonNull(ITEMS).register("test_spawn_egg", () -> new SpawnEggItemBase<>(() -> EntityType.IRON_GOLEM, 0, 0, new Item.Properties().tab(CreativeModeTab.TAB_MISC))));

    public static final Supplier<Item> TEST_BOAT_ITEM = create(() -> Objects.requireNonNull(ITEMS).register("test_boat", () -> new PollinatedBoatItem(Objects.requireNonNull(TEST_BOAT), false, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION))));
    public static final Supplier<Item> TEST_CHEST_BOAT_ITEM = create(() -> Objects.requireNonNull(ITEMS).register("test_chest_boat", () -> new PollinatedBoatItem(Objects.requireNonNull(TEST_BOAT), true, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION))));

    public static final Pair<Supplier<PollinatedStandingSignBlock>, Supplier<PollinatedWallSignBlock>> TEST_SIGN = create(() -> Objects.requireNonNull(BLOCKS).registerSign("test", Material.WOOD, MaterialColor.COLOR_BLUE));

    static void onClient() {
        BlockRendererRegistry.register(Blocks.FLOWER_POT, new DebugPollenFlowerPotRenderer());
    }

    static void onCommon() {
        Objects.requireNonNull(ITEMS).register(Pollen.PLATFORM);
        Objects.requireNonNull(BLOCKS).register(Pollen.PLATFORM);
        Objects.requireNonNull(FLUIDS).register(Pollen.PLATFORM);
        Objects.requireNonNull(BOATS).register(Pollen.PLATFORM);

        DispenseItemBehaviorRegistry.register(Blocks.DIAMOND_BLOCK, (source, stack) -> source.getLevel().getBlockState(new BlockPos(DispenserBlock.getDispensePosition(source))).getBlock() == Blocks.GOLD_BLOCK, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                source.getLevel().setBlock(new BlockPos(DispenserBlock.getDispensePosition(source)), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
                stack.shrink(1);
                return stack;
            }
        });

        DispenseItemBehaviorRegistry.register(Blocks.DIAMOND_BLOCK, (source, stack) -> source.getLevel().getBlockState(new BlockPos(DispenserBlock.getDispensePosition(source))).getBlock() == Blocks.EMERALD_BLOCK, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                source.getLevel().setBlock(new BlockPos(DispenserBlock.getDispensePosition(source)), Blocks.GOLD_BLOCK.defaultBlockState(), 2);
                stack.shrink(1);
                return stack;
            }
        });

        FluidBehaviorRegistry.register(TEST_TAG, new TestPollenFluidBehavior());
    }

    static void onClientPost(Platform.ModSetupContext context) {
    }

    static void onCommonPost(Platform.ModSetupContext context) {
        FlammabilityRegistry.register(Blocks.DIAMOND_BLOCK, 200, 50);
        CompostablesRegistry.register(Blocks.SAND, 1);
        FurnaceFuelRegistry.register(Items.BUCKET, 100);
    }

    private static <T> T create(Supplier<T> factory) {
        return !Pollen.TESTS_ENABLED ? null : factory.get();
    }
}
