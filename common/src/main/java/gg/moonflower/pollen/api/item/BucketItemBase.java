package gg.moonflower.pollen.api.item;

import gg.moonflower.pollen.core.mixin.BucketItemAccessor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A bucket that uses a supplied fluid.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class BucketItemBase extends BucketItem {

    protected final Supplier<? extends Fluid> fluid;

    public BucketItemBase(Supplier<? extends Fluid> fluid, Properties builder) {
        super(null, builder);
        this.fluid = fluid;
    }

    public BucketItemBase(Fluid fluid, Properties builder) {
        this(() -> fluid, builder);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Fluid content = this.getContent();
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (hitResult.getType() == HitResult.Type.MISS)
            return InteractionResultHolder.pass(itemStack);
        if (hitResult.getType() != HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(itemStack);

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos offsetPos = pos.relative(direction);
        if (level.mayInteract(player, pos) && player.mayUseItemAt(offsetPos, direction, itemStack)) {
            BlockState blockState = level.getBlockState(pos);
            if (content == Fluids.EMPTY) {
                if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
                    ItemStack itemStack2 = bucketPickup.pickupBlock(level, pos, blockState);
                    if (!itemStack2.isEmpty()) {
                        player.awardStat(Stats.ITEM_USED.get(this));
                        bucketPickup.getPickupSound().ifPresent((soundEvent) -> player.playSound(soundEvent, 1.0F, 1.0F));
                        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                        ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, player, itemStack2);
                        if (player instanceof ServerPlayer)
                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemStack2);

                        return InteractionResultHolder.sidedSuccess(itemStack3, level.isClientSide());
                    }
                }
            } else {
                BlockPos emptyPos = blockState.getBlock() instanceof LiquidBlockContainer && content == Fluids.WATER ? pos : offsetPos;
                if (this.emptyContents(player, level, emptyPos, hitResult)) {
                    this.checkExtraContent(player, level, itemStack, emptyPos);
                    if (player instanceof ServerPlayer)
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, emptyPos, itemStack);

                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(itemStack, player), level.isClientSide());
                }
            }
        }
        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) || group == CreativeModeTab.TAB_MISC) {
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof BucketItem)) {
                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof BucketItem && "minecraft".equals(Registry.ITEM.getKey(stack.getItem()).getNamespace()) && (stack.getItem() == Items.WATER_BUCKET || ((BucketItemAccessor) stack.getItem()).getContent() != Fluids.WATER)).reduce((a, b) -> b);
                if (optional.isPresent() && items.contains(optional.get())) {
                    items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
                    return;
                }
            }
            items.add(new ItemStack(this));
        }
    }

    @Override
    public boolean emptyContents(@Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        Fluid content = this.getContent();
        if (!(content instanceof FlowingFluid))
            return false;

        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        boolean bl = blockState.canBeReplaced(content);
        boolean bl2 = blockState.isAir() || bl || block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(level, blockPos, blockState, content);
        if (!bl2) {
            return blockHitResult != null && this.emptyContents(player, level, blockHitResult.getBlockPos().relative(blockHitResult.getDirection()), null);
        } else if (level.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
            int i = blockPos.getX();
            int j = blockPos.getY();
            int k = blockPos.getZ();
            level.playSound(player, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
        } else if (block instanceof LiquidBlockContainer && content == Fluids.WATER) {
            ((LiquidBlockContainer) block).placeLiquid(level, blockPos, blockState, ((FlowingFluid) content).getSource(false));
            this.playEmptySound(player, level, blockPos);
            return true;
        } else {
            if (!level.isClientSide() && bl && !material.isLiquid()) {
                level.destroyBlock(blockPos, true);
            }

            if (!level.setBlock(blockPos, content.defaultFluidState().createLegacyBlock(), 11) && !blockState.getFluidState().isSource()) {
                return false;
            } else {
                this.playEmptySound(player, level, blockPos);
                return true;
            }
        }
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos) {
        level.playSound(player, pos, this.getContent().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    public Fluid getContent() {
        return this.fluid.get();
    }
}
