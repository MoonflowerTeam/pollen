package gg.moonflower.pollen.api.event.events;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.EventRegistry;
import gg.moonflower.pollen.core.mixin.loot.LootPoolAccessor;
import gg.moonflower.pollen.core.mixin.loot.LootPoolBuilderAccessor;
import gg.moonflower.pollen.core.mixin.loot.LootTableAccessor;
import gg.moonflower.pollen.core.mixin.loot.LootTableBuilderAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Fired for each new loot table deserialized from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface LootTableConstructingEvent {

    PollinatedEvent<LootTableConstructingEvent> EVENT = EventRegistry.create(LootTableConstructingEvent.class, events -> context -> {
        for (LootTableConstructingEvent event : events)
            event.modifyLootTable(context);
    });

    /**
     * @param context The context for deserialization
     */
    void modifyLootTable(Context context);

    /**
     * Context for deserializing a loot table.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    class Context {

        private final LootTable source;
        private final ResourceLocation loadingId;

        private final LootTable.Builder builder;
        private final Map<Integer, LootPoolModifier> modifyPools;
        private final Set<Integer> removePools;
        private final Set<Integer> removeFunctions;
        private boolean changed;

        public Context(ResourceLocation loadingId, LootTable source) {
            this.loadingId = loadingId;
            this.source = source;

            this.builder = LootTable.lootTable().setParamSet(source.getParamSet());
            this.modifyPools = new HashMap<>();
            this.removePools = new HashSet<>();
            this.removeFunctions = new HashSet<>();
            ((LootTableBuilderAccessor) this.builder).getPools().addAll(getPools(source));
            ((LootTableBuilderAccessor) this.builder).getFunctions().addAll(Arrays.asList(((LootTableAccessor) source).getFunctions()));
        }

        private void updatePool(int index, Consumer<LootPool.Builder> action) {
            List<LootPool> pools = ((LootTableBuilderAccessor) this.builder).getPools();
            if (index < 0 || index >= pools.size())
                return;

            LootPoolAccessor sourceAccessor = (LootPoolAccessor) pools.get(index);

            LootPool.Builder builder = LootPool.lootPool().setRolls(sourceAccessor.getRolls());
            LootPoolBuilderAccessor accessor = (LootPoolBuilderAccessor) builder;
            accessor.getEntries().addAll(Arrays.asList(sourceAccessor.getEntries()));
            accessor.getConditions().addAll(Arrays.asList(sourceAccessor.getConditions()));
            accessor.getFunctions().addAll(Arrays.asList(sourceAccessor.getFunctions()));
            accessor.setBonusRolls(sourceAccessor.getBonusRolls());

            action.accept(builder);
            pools.set(index, builder.build());
            this.changed = true;
        }

        private void removeFromPool(int index, Consumer<LootPoolModifier> action) {
            List<LootPool> pools = ((LootTableBuilderAccessor) this.builder).getPools();
            if (index < 0 || index >= pools.size())
                return;

            action.accept(this.modifyPools.computeIfAbsent(index, __ -> new LootPoolModifier()));
            this.changed = true;
        }

        @ApiStatus.Internal
        public LootTable apply() {
            if (!this.changed)
                return this.source;

            this.modifyPools.keySet().removeIf(this.removePools::contains); // Remove modifications to removed pools

            List<LootPool> pools = ((LootTableBuilderAccessor) this.builder).getPools();
            List<LootItemFunction> functions = ((LootTableBuilderAccessor) this.builder).getFunctions();
            for (Map.Entry<Integer, LootPoolModifier> entry : this.modifyPools.entrySet()) {
                int index = entry.getKey();
                if (index < 0 || index >= pools.size())
                    continue;

                LootPool pool = pools.get(index);
                LootPool.Builder poolBuilder = LootPool.lootPool();
                LootPoolAccessor poolAccessor = (LootPoolAccessor) pool;
                LootPoolBuilderAccessor builderAccessor = (LootPoolBuilderAccessor) poolBuilder;
                LootPoolModifier modifier = entry.getValue();

                builderAccessor.getEntries().addAll(Arrays.asList(poolAccessor.getEntries())); // Add existing entries
                builderAccessor.getConditions().addAll(Arrays.asList(poolAccessor.getConditions())); // Add existing functions
                builderAccessor.getFunctions().addAll(Arrays.asList(poolAccessor.getFunctions())); // Add existing functions

                int[] removeEntriesArray = modifier.removeEntries.stream().mapToInt(i -> i).sorted().toArray();
                int[] removeConditionsArray = modifier.removeConditions.stream().mapToInt(i -> i).sorted().toArray();
                int[] removeFunctionsArray = modifier.removeFunctions.stream().mapToInt(i -> i).sorted().toArray();

                for (int j = 0; j < removeEntriesArray.length; j++) // Remove entries
                    builderAccessor.getEntries().remove(removeEntriesArray[removeEntriesArray.length - j - 1]);
                for (int j = 0; j < removeConditionsArray.length; j++) // Remove conditions
                    builderAccessor.getConditions().remove(removeConditionsArray[removeConditionsArray.length - j - 1]);
                for (int j = 0; j < removeFunctionsArray.length; j++) // Remove functions
                    builderAccessor.getFunctions().remove(removeFunctionsArray[removeFunctionsArray.length - j - 1]);

                pools.set(index, poolBuilder.build()); // Update pool
            }

            int[] removePoolsArray = this.removePools.stream().mapToInt(i -> i).sorted().toArray();
            int[] removeFunctionsArray = this.removeFunctions.stream().mapToInt(i -> i).sorted().toArray();

            for (int j = 0; j < removePoolsArray.length; j++) // Remove loot table pools
                pools.remove(removePoolsArray[removePoolsArray.length - j - 1]);
            for (int j = 0; j < removeFunctionsArray.length; j++) // Remove loot table functions
                functions.remove(removeFunctionsArray[removeFunctionsArray.length - j - 1]);

            this.modifyPools.clear();
            this.removePools.clear();
            this.removeFunctions.clear();

            return this.builder.build();
        }

        /**
         * Adds a new loot pool to the table.
         *
         * @param lootPool The pool to add
         */
        public void addPool(LootPool lootPool) {
            ((LootTableBuilderAccessor) this.builder).getPools().add(lootPool);
            this.changed = true;
        }

        /**
         * Adds a new loot pool to the table.
         *
         * @param lootPool The pool to add
         */
        public void addPool(LootPool.Builder lootPool) {
            this.builder.withPool(lootPool);
            this.changed = true;
        }

        /**
         * Adds a new item function to the table.
         *
         * @param function The function to add
         */
        public void addFunction(LootItemFunction function) {
            ((LootTableBuilderAccessor) this.builder).getFunctions().add(function);
            this.changed = true;
        }

        /**
         * Adds a new item function to the table.
         *
         * @param function The function to add
         */
        public void addFunction(LootItemFunction.Builder function) {
            this.builder.apply(function);
            this.changed = true;
        }

        /**
         * Sets the parameter set to the specified value.
         *
         * @param parameterSet The new parameter set
         */
        public void setParamSet(LootContextParamSet parameterSet) {
            this.builder.setParamSet(parameterSet);
            this.changed = true;
        }

        /**
         * Inserts new loot pool data into the specified pool.
         *
         * @param poolIndex The pool to insert into
         * @param pools     The pools to add
         */
        public void insertPool(int poolIndex, LootPool... pools) {
            this.updatePool(poolIndex, pool -> {
                LootPoolBuilderAccessor poolAccessor = (LootPoolBuilderAccessor) pool;
                for (LootPool injectPool : pools) {
                    LootPoolAccessor injectPoolAccessor = (LootPoolAccessor) injectPool;
                    poolAccessor.getEntries().addAll(Arrays.asList(injectPoolAccessor.getEntries()));
                    poolAccessor.getConditions().addAll(Arrays.asList(injectPoolAccessor.getConditions()));
                    poolAccessor.getFunctions().addAll(Arrays.asList(injectPoolAccessor.getFunctions()));
                }
            });
        }

        /**
         * Inserts new loot pool data into the specified pool.
         *
         * @param poolIndex The pool to insert into
         * @param pools     The pools to add
         */
        public void insertPool(int poolIndex, LootPool.Builder... pools) {
            this.updatePool(poolIndex, pool -> {
                LootPoolBuilderAccessor poolAccessor = (LootPoolBuilderAccessor) pool;
                for (LootPool.Builder injectPool : pools) {
                    LootPoolBuilderAccessor injectPoolAccessor = (LootPoolBuilderAccessor) injectPool;
                    poolAccessor.getEntries().addAll(injectPoolAccessor.getEntries());
                    poolAccessor.getConditions().addAll(injectPoolAccessor.getConditions());
                    poolAccessor.getFunctions().addAll(injectPoolAccessor.getFunctions());
                }
            });
        }

        /**
         * Inserts new loot pool entries into the specified pool.
         *
         * @param poolIndex The pool to insert into
         * @param entries   The entries to add
         */
        public void insertEntry(int poolIndex, LootPoolEntryContainer... entries) {
            this.updatePool(poolIndex, pool -> ((LootPoolBuilderAccessor) pool).getEntries().addAll(Arrays.asList(entries)));
        }

        /**
         * Inserts new loot pool entries into the specified pool.
         *
         * @param poolIndex The pool to insert into
         * @param entries   The entries to add
         */
        public void insertEntry(int poolIndex, LootPoolEntryContainer.Builder<?>... entries) {
            this.updatePool(poolIndex, pool -> {
                for (LootPoolEntryContainer.Builder<?> entry : entries)
                    pool.add(entry);
            });
        }

        /**
         * Inserts new loot pool conditions into the specified pool.
         *
         * @param poolIndex  The pool to insert into
         * @param conditions The conditions to add
         */
        public void insertCondition(int poolIndex, LootItemCondition... conditions) {
            this.updatePool(poolIndex, pool -> ((LootPoolBuilderAccessor) pool).getConditions().addAll(Arrays.asList(conditions)));
        }

        /**
         * Inserts new loot pool conditions into the specified pool.
         *
         * @param poolIndex  The pool to insert into
         * @param conditions The conditions to add
         */
        public void insertCondition(int poolIndex, LootItemCondition.Builder... conditions) {
            this.updatePool(poolIndex, pool -> {
                for (LootItemCondition.Builder condition : conditions)
                    pool.when(condition);
            });
        }

        /**
         * Inserts new loot pool functions into the specified pool.
         *
         * @param poolIndex The pool to insert into
         * @param functions The functions to add
         */
        public void insertFunction(int poolIndex, LootItemFunction... functions) {
            this.updatePool(poolIndex, pool -> ((LootPoolBuilderAccessor) pool).getFunctions().addAll(Arrays.asList(functions)));
        }

        /**
         * Inserts new loot pool functions into the specified pool.
         *
         * @param poolIndex The pool to insert into
         * @param functions The functions to add
         */
        public void insertFunction(int poolIndex, LootItemFunction.Builder... functions) {
            this.updatePool(poolIndex, pool -> {
                for (LootItemFunction.Builder function : functions)
                    pool.apply(function);
            });
        }

        /**
         * Removes an entry from the specified loot pool.
         *
         * @param poolIndex The index of the pool to remove from
         * @param index     The index of the entry to remove
         */
        public void removeEntry(int poolIndex, int index) {
            this.removeFromPool(poolIndex, modifier -> modifier.removeEntries.add(index));
        }

        /**
         * Removes a condition from the specified loot pool.
         *
         * @param poolIndex The index of the pool to remove from
         * @param index     The index of the condition to remove
         */
        public void removeCondition(int poolIndex, int index) {
            this.removeFromPool(poolIndex, modifier -> modifier.removeConditions.add(index));
        }

        /**
         * Removes a function from the specified loot pool.
         *
         * @param poolIndex The index of the pool to remove from
         * @param index     The index of the function to remove
         */
        public void removeFunction(int poolIndex, int index) {
            this.removeFromPool(poolIndex, modifier -> modifier.removeFunctions.add(index));
        }

        /**
         * Removes the specified loot pool from the table.
         *
         * @param index The index of the pool to remove
         */
        public void removePool(int index) {
            this.removePools.add(index);
            this.changed = true;
        }

        /**
         * Removes the specified function from the table.
         *
         * @param index The index of the function to remove
         */
        public void removeFunction(int index) {
            this.removeFunctions.add(index);
            this.changed = true;
        }

        /**
         * @return The id of the loot table currently being modified or <code>null</code> if unknown
         */
        public ResourceLocation getId() {
            return loadingId;
        }

        /**
         * @return The loot table being modified
         */
        public LootTable getLootTable() {
            return source;
        }

        private static class LootPoolModifier {

            private final Set<Integer> removeEntries;
            private final Set<Integer> removeConditions;
            private final Set<Integer> removeFunctions;

            private LootPoolModifier() {
                this.removeEntries = new HashSet<>();
                this.removeConditions = new HashSet<>();
                this.removeFunctions = new HashSet<>();
            }
        }
    }

    @ApiStatus.Internal
    @ExpectPlatform
    static List<LootPool> getPools(LootTable lootTable) {
        return Platform.error();
    }
}
