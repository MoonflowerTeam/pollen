package gg.moonflower.pollen.api.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * A fish bucket that allows all entity types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class FishBucketItemBase extends BucketItemBase {

    private final Supplier<? extends EntityType<?>> entityType;

    public FishBucketItemBase(Supplier<? extends EntityType<?>> entityType, Supplier<? extends Fluid> fluid, boolean addToMisc, Properties builder) {
        super(fluid, addToMisc, builder);
        this.entityType = entityType;
    }

    public FishBucketItemBase(EntityType<?> entityType, Fluid fluid, boolean addToMisc, Properties builder) {
        super(fluid, addToMisc, builder);
        this.entityType = () -> entityType;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (this.getEntityType() == EntityType.TROPICAL_FISH) {
            CompoundTag compoundTag = itemStack.getTag();
            if (compoundTag != null && compoundTag.contains("BucketVariantTag", 3)) {
                int i = compoundTag.getInt("BucketVariantTag");
                ChatFormatting[] chatFormattings = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
                String string = "color.minecraft." + TropicalFish.getBaseColor(i);
                String string2 = "color.minecraft." + TropicalFish.getPatternColor(i);

                for (int j = 0; j < TropicalFish.COMMON_VARIANTS.length; ++j) {
                    if (i == TropicalFish.COMMON_VARIANTS[j]) {
                        list.add((new TranslatableComponent(TropicalFish.getPredefinedName(j))).withStyle(chatFormattings));
                        return;
                    }
                }

                list.add((new TranslatableComponent(TropicalFish.getFishTypeName(i))).withStyle(chatFormattings));
                MutableComponent mutableComponent = new TranslatableComponent(string);
                if (!string.equals(string2)) {
                    mutableComponent.append(", ").append(new TranslatableComponent(string2));
                }

                mutableComponent.withStyle(chatFormattings);
                list.add(mutableComponent);
            }
        }
    }

    @Override
    public void checkExtraContent(Level world, ItemStack stack, BlockPos pos) {
        if (!world.isClientSide()) {
            this.spawn(null, (ServerLevel) world, stack, pos);
        }
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
