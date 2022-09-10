package gg.moonflower.pollen.api.registry.v1.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A fish bucket that allows all entity types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedFishBucketItem extends PollinatedBucketItem {

    private final Supplier<? extends EntityType<?>> entityType;

    public PollinatedFishBucketItem(Supplier<? extends EntityType<?>> entityType, Supplier<? extends Fluid> fluid, Properties builder) {
        super(fluid, builder);
        this.entityType = entityType;
    }

    public PollinatedFishBucketItem(EntityType<?> entityType, Fluid fluid, Properties builder) {
        super(fluid, builder);
        this.entityType = () -> entityType;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group))
            TabFiller.insert(new ItemStack(this), false, items, stack -> stack.getItem() instanceof BucketItem && "minecraft".equals(Registry.ITEM.getKey(stack.getItem()).getNamespace()));
    }

    @Override
    public void checkExtraContent(@Nullable Player player, Level world, ItemStack stack, BlockPos pos) {
        if (!world.isClientSide())
            this.spawn(player, (ServerLevel) world, stack, pos);
    }

    @Override
    protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    protected void spawn(@Nullable Player player, ServerLevel world, ItemStack stack, BlockPos pos) {
        Entity entity = this.getEntityType().spawn(world, stack, player, pos, MobSpawnType.BUCKET, true, false);
        if (entity instanceof AbstractFish)
            ((AbstractFish) entity).setFromBucket(true);
    }

    public EntityType<?> getEntityType() {
        return this.entityType.get();
    }
}
