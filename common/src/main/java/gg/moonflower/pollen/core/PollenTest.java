package gg.moonflower.pollen.core;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.block.PollinatedLiquidBlock;
import gg.moonflower.pollen.api.block.PollinatedStandingSignBlock;
import gg.moonflower.pollen.api.block.PollinatedWallSignBlock;
import gg.moonflower.pollen.api.config.ConfigManager;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.datagen.provider.loot_table.PollinatedLootTableProvider;
import gg.moonflower.pollen.api.entity.PollinatedBoatType;
import gg.moonflower.pollen.api.item.BucketItemBase;
import gg.moonflower.pollen.api.item.PollinatedBoatItem;
import gg.moonflower.pollen.api.item.SpawnEggItemBase;
import gg.moonflower.pollen.api.levelgen.biome.BiomePlacementContext;
import gg.moonflower.pollen.api.levelgen.biome.PollinatedRegion;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.*;
import gg.moonflower.pollen.api.registry.content.*;
import gg.moonflower.pollen.api.registry.resource.TagRegistry;
import gg.moonflower.pollen.core.client.render.DebugPollenFlowerPotRenderer;
import gg.moonflower.pollen.core.datagen.TestBlockLootGenerator;
import gg.moonflower.pollen.core.test.TestFluid;
import gg.moonflower.pollen.core.test.TestPollenFluidBehavior;
import gg.moonflower.pollen.core.test.TestServerConfig;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollenTest {

    private static final PollinatedRegistry<Item> ITEMS = create(() -> PollinatedRegistry.create(Registry.ITEM, Pollen.MOD_ID));
    private static final PollinatedBlockRegistry BLOCKS = create(() -> PollinatedRegistry.createBlock(ITEMS));
    private static final PollinatedFluidRegistry FLUIDS = create(() -> PollinatedRegistry.createFluid(Pollen.MOD_ID));
    private static final PollinatedRegistry<PollinatedBoatType> BOATS = create(() -> PollinatedRegistry.create(PollenRegistries.BOAT_TYPE_REGISTRY, Pollen.MOD_ID));
    private static final PollinatedRegistry<Biome> BIOMES = create(() -> PollinatedRegistry.create(BuiltinRegistries.BIOME, Pollen.MOD_ID));

    public static final TagKey<Fluid> TEST_TAG = create(() -> TagRegistry.bindFluid(new ResourceLocation(Pollen.MOD_ID, "test")));
    public static final TestServerConfig SERVER_CONFIG = create(() -> ConfigManager.register(Pollen.MOD_ID, PollinatedConfigType.SERVER, TestServerConfig::new));

    public static final Supplier<PollinatedBoatType> TEST_BOAT = create(() -> Objects.requireNonNull(BOATS).register("test_boat", () -> new PollinatedBoatType(new ResourceLocation("textures/entity/ghast/ghast.png"))));
    public static final Supplier<FlowingFluid> TEST_FLUID = create(() -> Objects.requireNonNull(FLUIDS).register("test", TestFluid.Source::new));
    public static final Supplier<FlowingFluid> FLOWING_TEST_FLUID = create(() -> Objects.requireNonNull(FLUIDS).register("flowing_test", TestFluid.Flowing::new));
    public static final Supplier<Block> TEST = create(() -> Objects.requireNonNull(BLOCKS).register("test", () -> new PollinatedLiquidBlock(TEST_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops())));
    public static final Supplier<Item> TEST_BUCKET = create(() -> Objects.requireNonNull(ITEMS).register("test", () -> new BucketItemBase(TEST_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC))));
    public static final Supplier<Item> TEST_SPAWN_EGG = create(() -> Objects.requireNonNull(ITEMS).register("test_spawn_egg", () -> new SpawnEggItemBase<>(() -> EntityType.IRON_GOLEM, 0, 0, new Item.Properties().tab(CreativeModeTab.TAB_MISC))));

    public static final Supplier<Item> TEST_BOAT_ITEM = create(() -> Objects.requireNonNull(ITEMS).register("test_boat", () -> new PollinatedBoatItem(Objects.requireNonNull(TEST_BOAT), new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION))));
    public static final ResourceKey<Biome> TEST_BIOME = makeBiome("test_biome",() -> (new Biome.BiomeBuilder()).precipitation(Biome.Precipitation.RAIN).biomeCategory(Biome.BiomeCategory.NONE).temperature(0.5f).downfall(0.5f).specialEffects((new BiomeSpecialEffects.Builder()).fogColor(12638463).waterColor(12345).waterFogColor(56789).skyColor(12345).build()).mobSpawnSettings(new MobSpawnSettings.Builder().build()).generationSettings(new BiomeGenerationSettings.Builder().build()).build());

    public static final Pair<Supplier<PollinatedStandingSignBlock>, Supplier<PollinatedWallSignBlock>> TEST_SIGN = create(() -> Objects.requireNonNull(BLOCKS).registerSign("test", Material.WOOD, MaterialColor.COLOR_BLUE));

    static void onClient() {
        BlockRendererRegistry.register(Blocks.FLOWER_POT, new DebugPollenFlowerPotRenderer());
    }

    static void onCommon() {
        Objects.requireNonNull(ITEMS).register(Pollen.PLATFORM);
        Objects.requireNonNull(BLOCKS).register(Pollen.PLATFORM);
        Objects.requireNonNull(FLUIDS).register(Pollen.PLATFORM);
        Objects.requireNonNull(BOATS).register(Pollen.PLATFORM);
        Objects.requireNonNull(BIOMES).register(Pollen.PLATFORM);

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

        FlatteningRegistry.register(Blocks.EMERALD_BLOCK, Blocks.DIAMOND_BLOCK.defaultBlockState());
        FlatteningRegistry.register(Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK.defaultBlockState());
    }

    static void onClientPost(Platform.ModSetupContext context) {
    }

    static void onCommonPost(Platform.ModSetupContext context) {
        FlammabilityRegistry.register(Blocks.DIAMOND_BLOCK, 200, 50);
        CompostablesRegistry.register(Blocks.SAND, 1);
        FurnaceFuelRegistry.register(Items.BUCKET, 100);
        context.enqueueWork(() -> {
            RegionRegistry.register(Pollen.MOD_ID, new TestRegion());
            SurfaceRuleRegistry.register(SurfaceRuleRegistry.RuleCategory.OVERWORLD, Pollen.MOD_ID, SurfaceRules.ifTrue(SurfaceRules.isBiome(TEST_BIOME), SurfaceRules.state(Blocks.DIAMOND_BLOCK.defaultBlockState())));
        });
    }

    static void onData(Platform.DataSetupContext context) {
        DataGenerator generator = context.getGenerator();
        generator.addProvider(new PollinatedLootTableProvider(generator).add(
                LootContextParamSets.CHEST, new TestBlockLootGenerator()
        ));
    }

    private static <T> T create(Supplier<T> factory) {
        return !Pollen.TESTS_ENABLED ? null : factory.get();
    }

    private static ResourceKey<Biome> makeBiome(String name, Supplier<Biome> factory) {
        if (Pollen.TESTS_ENABLED) {
            ResourceLocation id = new ResourceLocation(Pollen.MOD_ID, name);
            Objects.requireNonNull(BIOMES).register(name, factory);
            return ResourceKey.create(Registry.BIOME_REGISTRY, id);
        } else {
            return null;
        }
    }

    public static class TestRegion implements PollinatedRegion {

        @Override
        public Type getType() {
            return Type.OVERWORLD;
        }

        @Override
        public int getWeight() {
            return 5;
        }

        @Override
        public void addBiomes(BiomePlacementContext context) {
            context.addModifiedOverworldBiomes(builder -> builder.replaceBiome(Biomes.PLAINS, TEST_BIOME));
        }
    }
}
