package gg.moonflower.pollen.api.item;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.content.DispenseItemBehaviorRegistry;
import gg.moonflower.pollen.api.util.NbtConstants;
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
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// TODO Rename in 2.0.0 to PollinatedSpawnEggItem

/**
 * A spawn egg that allows for deferred entity types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class SpawnEggItemBase<T extends EntityType<? extends Mob>> extends SpawnEggItem {

    private static final DispenseItemBehavior DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());

            try {
                type.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            } catch (Exception var6) {
                LogManager.getLogger().error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), var6);
                return ItemStack.EMPTY;
            }

            stack.shrink(1);
            return stack;
        }
    };

    private final boolean addToMisc;
    private final Supplier<T> type;

    public SpawnEggItemBase(Supplier<T> type, int backgroundColor, int spotColor, Properties builder) {
        this(type, backgroundColor, spotColor, false, builder);
    }

    // TODO remove in 2.0.0

    /**
     * @deprecated Use the other constructor and set creative tab to {@link CreativeModeTab#TAB_MISC} instead
     */
    @Deprecated
    @SuppressWarnings("UnsafePlatformOnlyCall")
    public SpawnEggItemBase(Supplier<T> type, int backgroundColor, int spotColor, boolean addToMisc, Properties builder) {
        super(Platform.isForge() ? null : type.get(), backgroundColor, spotColor, builder);
        this.type = type;
        this.addToMisc = addToMisc;
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
        if (this.allowedIn(group) || (this.addToMisc && group == CreativeModeTab.TAB_MISC)) {
//            if (items.stream().anyMatch(stack -> stack.getItem() instanceof SpawnEggItem)) {
//                String itemName = Registry.ITEM.getKey(this).getPath();
//                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof SpawnEggItem).max((a, b) ->
//                {
//                    int valA = itemName.compareToIgnoreCase(Registry.ITEM.getKey(a.getItem()).getPath());
//                    int valB = Registry.ITEM.getKey(b.getItem()).getPath().compareToIgnoreCase(itemName);
//                    return valB - valA;
//                });
//                if (optional.isPresent()) {
//                    items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
//                    return;
//                }
//            }
//            items.add(new ItemStack(this));
            TabFiller.insertNamed(new ItemStack(this), false, items, stack -> stack.getItem() instanceof SpawnEggItem);
        }
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
