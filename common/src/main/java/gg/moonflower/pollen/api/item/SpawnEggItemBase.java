package gg.moonflower.pollen.api.item;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * <p>A spawn egg that allows for deferred entity types.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class SpawnEggItemBase<T extends EntityType<?>> extends SpawnEggItem {

    private final boolean addToMisc;
    private final Supplier<T> type;

    @SuppressWarnings("UnsafePlatformOnlyCall")
    public SpawnEggItemBase(Supplier<T> type, int backgroundColor, int spotColor, boolean addToMisc, Properties builder) {
        super(Platform.isForge() ? null : type.get(), backgroundColor, spotColor, builder);
        this.type = type;
        this.addToMisc = addToMisc;
        if (Platform.isForge())
            registerSpawnEgg(this, type);
    }

    @SuppressWarnings("UnimplementedExpectPlatform")
    @ExpectPlatform
    @PlatformOnly(PlatformOnly.FORGE)
    public static void registerSpawnEgg(SpawnEggItem item, Supplier<? extends EntityType<?>> type) {
        Platform.error();
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) || (this.addToMisc && group == CreativeModeTab.TAB_MISC)) {
            if (items.stream().anyMatch(stack -> stack.getItem() instanceof SpawnEggItem)) {
                String itemName = Registry.ITEM.getKey(this).getPath();
                Optional<ItemStack> optional = items.stream().filter(stack -> stack.getItem() instanceof SpawnEggItem).max((a, b) ->
                {
                    int valA = itemName.compareToIgnoreCase(Registry.ITEM.getKey(a.getItem()).getPath());
                    int valB = Registry.ITEM.getKey(b.getItem()).getPath().compareToIgnoreCase(itemName);
                    return valB - valA;
                });
                if (optional.isPresent()) {
                    items.add(items.indexOf(optional.get()) + 1, new ItemStack(this));
                    return;
                }
            }
            items.add(new ItemStack(this));
        }
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag nbt) {
        if (nbt != null && nbt.contains("EntityTag", 10)) {
            CompoundTag compoundnbt = nbt.getCompound("EntityTag");
            if (compoundnbt.contains("id", 8)) {
                return EntityType.byString(compoundnbt.getString("id")).orElseGet(this.type);
            }
        }

        return this.type.get();
    }
}
