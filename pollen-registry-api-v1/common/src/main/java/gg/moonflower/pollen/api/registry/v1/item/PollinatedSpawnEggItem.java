package gg.moonflower.pollen.api.registry.v1.item;

import com.mojang.logging.LogUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import gg.moonflower.pollen.api.base.NbtConstants;
import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.api.registry.v1.content.DispenseItemBehaviorRegistry;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * A spawn egg that allows for deferred entity types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedSpawnEggItem<T extends EntityType<? extends Mob>> extends SpawnEggItem {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final DispenseItemBehavior DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());

            try {
                type.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            } catch (Exception e) {
                LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), e);
                return ItemStack.EMPTY;
            }

            stack.shrink(1);
            return stack;
        }
    };

    private final Supplier<T> type;

    public PollinatedSpawnEggItem(Supplier<T> type, int backgroundColor, int spotColor, Properties builder) {
        super(Platform.isForge() ? null : type.get(), backgroundColor, spotColor, builder);
        this.type = type;
        if (Platform.isForge())
            registerSpawnEgg(this, type);

        DispenseItemBehaviorRegistry.register(this, DISPENSE_BEHAVIOR);
    }

    @SuppressWarnings("UnimplementedExpectPlatform")
    @ExpectPlatform
    @PlatformOnly(PlatformOnly.FORGE)
    public static void registerSpawnEgg(SpawnEggItem item, Supplier<? extends EntityType<?>> type) {
        Platform.error();
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) || group == CreativeModeTab.TAB_MISC)
            TabFiller.insertNamed(new ItemStack(this), false, items, stack -> stack.getItem() instanceof SpawnEggItem);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag nbt) {
        if (!Platform.isForge())
            return super.getType(nbt);

        if (nbt != null && nbt.contains("EntityTag", NbtConstants.COMPOUND)) {
            CompoundTag compoundnbt = nbt.getCompound("EntityTag");
            if (compoundnbt.contains("id", NbtConstants.STRING)) {
                return EntityType.byString(compoundnbt.getString("id")).orElseGet(this.type);
            }
        }

        return this.type.get();
    }
}
