package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Supplier;

public final class ModifyTradesEvents {

    public static final PollinatedEvent<ModifyVillager> VILLAGER = EventRegistry.createLoop(ModifyVillager.class);
    public static final PollinatedEvent<ModifyWanderer> WANDERER = EventRegistry.createLoop(ModifyWanderer.class);

    private ModifyTradesEvents() {
    }

    /**
     * Registers new trades into all villager types trader.
     *
     * @author Jackson, Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ModifyVillager {

        /**
         * Modifies the trades for all normal villagers.
         *
         * @param context The context for the event
         */
        void modifyTrades(Context context);

        /**
         * Context for registering villager trades.
         *
         * @since 1.0.0
         */
        interface Context {

            /**
             * @return The profession of the villager to add trades to
             */
            VillagerProfession getProfession();

            /**
             * Retrieves the registry of trades for the specified tier.
             *
             * @param tier A number between {@link #getMinTier()} and {@link #getMaxTier()} to retrieve the tier of trades
             * @return The registry for that tier
             */
            TradeRegistry getTrades(int tier);

            /**
             * @return The minimum tier for trades. Vanilla is 1
             */
            int getMinTier();

            /**
             * @return The maximum tier for trades. Vanilla is 5
             */
            int getMaxTier();
        }
    }

    /**
     * Registers new trades into the wandering trader.
     *
     * @author Jackson, Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ModifyWanderer {

        /**
         * Modifies the trades for the wandering trader.
         *
         * @param context The context for the event
         */
        void modifyTrades(Context context);

        /**
         * Context for registering wanderer trades.
         *
         * @since 1.0.0
         */
        interface Context {

            /**
             * @return The common trades registry
             */
            TradeRegistry getGeneric();

            /**
             * @return The rarer trades registry
             */
            TradeRegistry getRare();
        }
    }

    /**
     * Registers trades into a villager trade list.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class TradeRegistry implements List<VillagerTrades.ItemListing> {

        private final List<VillagerTrades.ItemListing> trades;

        public TradeRegistry() {
            this.trades = NonNullList.create();
        }

        @Override
        public int size() {
            return this.trades.size();
        }

        @Override
        public boolean isEmpty() {
            return this.trades.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.trades.contains(o);
        }

        @NotNull
        @Override
        public Iterator<VillagerTrades.ItemListing> iterator() {
            return this.trades.iterator();
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return this.trades.toArray();
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return this.trades.toArray(a);
        }

        @Override
        public boolean add(VillagerTrades.ItemListing listing) {
            return this.trades.add(listing);
        }

        @Override
        public boolean remove(Object o) {
            return this.trades.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return this.trades.containsAll(c);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends VillagerTrades.ItemListing> c) {
            return this.trades.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends VillagerTrades.ItemListing> c) {
            return this.trades.addAll(index, c);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return this.trades.removeAll(c);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return this.trades.retainAll(c);
        }

        @Override
        public void clear() {
            this.trades.clear();
        }

        @Override
        public VillagerTrades.ItemListing get(int index) {
            return this.trades.get(index);
        }

        @Override
        public VillagerTrades.ItemListing set(int index, VillagerTrades.ItemListing element) {
            return this.trades.set(index, element);
        }

        @Override
        public void add(int index, VillagerTrades.ItemListing element) {
            this.trades.add(index, element);
        }

        @Override
        public VillagerTrades.ItemListing remove(int index) {
            return this.trades.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.trades.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.trades.lastIndexOf(o);
        }

        @NotNull
        @Override
        public ListIterator<VillagerTrades.ItemListing> listIterator() {
            return this.trades.listIterator();
        }

        @NotNull
        @Override
        public ListIterator<VillagerTrades.ItemListing> listIterator(int index) {
            return this.trades.listIterator(index);
        }

        @NotNull
        @Override
        public List<VillagerTrades.ItemListing> subList(int fromIndex, int toIndex) {
            return this.trades.subList(fromIndex, toIndex);
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item           The item to trade for
         * @param emeralds       The amount of emeralds to trade
         * @param itemCount      The amount of the item to trade
         * @param maxUses        The maximum amount of times this trade can be used before needing to reset
         * @param xpGain         The amount of experience gained by this exchange
         * @param sellToVillager Whether the villager is buying or selling the item for emeralds
         */
        public void add(ItemLike item, int emeralds, int itemCount, int maxUses, int xpGain, boolean sellToVillager) {
            this.add(new ItemTrade(() -> item, emeralds, itemCount, maxUses, xpGain, 0.05F, sellToVillager));
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item            The item to trade for
         * @param emeralds        The amount of emeralds to trade
         * @param itemCount       The amount of the item to trade
         * @param maxUses         The maximum amount of times this trade can be used before needing to reset
         * @param xpGain          The amount of experience gained by this exchange
         * @param priceMultiplier The multiplier for how much the price deviates
         * @param sellToVillager  Whether the villager is buying or selling the item for emeralds
         */
        public void add(ItemLike item, int emeralds, int itemCount, int maxUses, int xpGain, float priceMultiplier, boolean sellToVillager) {
            this.add(new ItemTrade(() -> item, emeralds, itemCount, maxUses, xpGain, priceMultiplier, sellToVillager));
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item           The item to trade for as a supplier
         * @param emeralds       The amount of emeralds to trade
         * @param itemCount      The amount of the item to trade
         * @param maxUses        The maximum amount of times this trade can be used before needing to reset
         * @param xpGain         The amount of experience gained by this exchange
         * @param sellToVillager Whether the villager is buying or selling the item for emeralds
         */
        public void add(Supplier<? extends ItemLike> item, int emeralds, int itemCount, int maxUses, int xpGain, boolean sellToVillager) {
            this.add(new ItemTrade(item, emeralds, itemCount, maxUses, xpGain, 0.05F, sellToVillager));
        }

        /**
         * Adds a simple trade for items or emeralds.
         *
         * @param item            The item to trade for as a supplier
         * @param emeralds        The amount of emeralds to trade
         * @param itemCount       The amount of the item to trade
         * @param maxUses         The maximum amount of times this trade can be used before needing to reset
         * @param xpGain          The amount of experience gained by this exchange
         * @param priceMultiplier The multiplier for how much the price deviates
         * @param sellToVillager  Whether the villager is buying or selling the item for emeralds
         */
        public void add(Supplier<? extends ItemLike> item, int emeralds, int itemCount, int maxUses, int xpGain, float priceMultiplier, boolean sellToVillager) {
            this.add(new ItemTrade(item, emeralds, itemCount, maxUses, xpGain, priceMultiplier, sellToVillager));
        }
    }

    private static class ItemTrade implements VillagerTrades.ItemListing {

        private final Supplier<? extends ItemLike> item;
        private final int emeralds;
        private final int itemCount;
        private final int maxUses;
        private final int xpGain;
        private final float priceMultiplier;
        private final boolean sellToVillager;

        private ItemTrade(Supplier<? extends ItemLike> Item, int emeralds, int itemCount, int maxUses, int xpGain, float priceMultiplier, boolean sellToVillager) {
            this.item = Item;
            this.emeralds = emeralds;
            this.itemCount = itemCount;
            this.maxUses = maxUses;
            this.xpGain = xpGain;
            this.priceMultiplier = priceMultiplier;
            this.sellToVillager = sellToVillager;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack emeralds = new ItemStack(Items.EMERALD, this.emeralds);
            ItemStack item = new ItemStack(this.item.get(), this.itemCount);

            return new MerchantOffer(this.sellToVillager ? item : emeralds, this.sellToVillager ? emeralds : item, this.maxUses, this.xpGain, this.priceMultiplier);
        }
    }
}
