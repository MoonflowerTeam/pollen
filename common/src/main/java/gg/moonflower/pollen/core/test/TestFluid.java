package gg.moonflower.pollen.core.test;

import com.mojang.blaze3d.systems.RenderSystem;
import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.PollenTest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public abstract class TestFluid extends FlowingFluid implements PollinatedFluid {

    private static final ResourceLocation STILL = new ResourceLocation(Pollen.MOD_ID, "block/void");
    private static final ResourceLocation FLOW = new ResourceLocation(Pollen.MOD_ID, "block/void_flow");

    @Override
    public ResourceLocation getStillTextureName() {
        return STILL;
    }

    @Override
    public ResourceLocation getFlowingTextureName() {
        return FLOW;
    }

    @Override
    public int getFogColor(Camera camera, ClientLevel level, Holder<Biome> biome, float partialTicks) {
        return 0x0E0E10;
    }

    @Override
    public void applyFog(GameRenderer renderer, Camera camera, float distance, float partialTicks) {
        RenderSystem.setShaderFogEnd(1.0F);
        RenderSystem.setShaderFogStart(0.2F);
    }

    @Override
    public Fluid getFlowing() {
        return PollenTest.FLOWING_TEST_FLUID.get();
    }

    @Override
    public Fluid getSource() {
        return PollenTest.TEST_FLUID.get();
    }

    @Override
    public Item getBucket() {
        return PollenTest.TEST_BUCKET.get();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
//        if (!state.isSource() && !state.getValue(FALLING)) {
//            if (random.nextInt(64) == 0) {
//                level.playLocalSound((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
//            }
//        } else if (random.nextInt(10) == 0) {
//            level.addParticle(ParticleTypes.UNDERWATER, (double) pos.getX() + random.nextDouble(), (double) pos.getY() + random.nextDouble(), (double) pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
//        }
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
    }

    @Override
    public Optional<SoundEvent> getEmptySound() {
        return Optional.of(SoundEvents.BUCKET_EMPTY_LAVA);
    }

    @Override
    protected boolean canConvertToSource() {
        return true;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return PollenTest.TEST.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == this.getSource() || fluid == this.getFlowing();
    }

    @Override
    public int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 40;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(PollenTest.TEST_TAG);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Nullable
    @Override
    public BlockState getInteractionState(Level level, FluidState fluidState, BlockPos pos, BlockPos neighborPos) {
        return level.getBlockState(neighborPos).is(Blocks.DIAMOND_BLOCK) ? Blocks.EMERALD_BLOCK.defaultBlockState() : null;
    }

    public static class Flowing extends TestFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends TestFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
