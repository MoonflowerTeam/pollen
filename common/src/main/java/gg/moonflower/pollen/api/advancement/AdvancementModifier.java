package gg.moonflower.pollen.api.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import gg.moonflower.pollen.core.mixin.AdvancementBuilderAccessor;
import gg.moonflower.pollen.core.mixin.AdvancementRewardsAccessor;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Modifies existing advancements to add extra criteria, requirements, and rewards.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AdvancementModifier {

    private final ResourceLocation id;
    private final ResourceLocation[] inject;
    private final int priority;
    private final Map<String, Criterion> addCriteria;
    private final String[][] injectRequirements;
    private final String[][] addRequirements;
    private final AdvancementRewards addRewards;
    private final String[] removeRequirements;
    private final ResourceLocation[] removeLoot;
    private final ResourceLocation[] removeRecipes;

    public AdvancementModifier(ResourceLocation id, ResourceLocation[] inject, int priority, Map<String, Criterion> addCriteria, String[][] injectRequirements, String[][] addRequirements, AdvancementRewards addRewards, String[] removeRequirements, ResourceLocation[] removeLoot, ResourceLocation[] removeRecipes) {
        this.id = id;
        this.inject = inject;
        this.priority = priority;
        this.addCriteria = ImmutableMap.copyOf(addCriteria);
        this.injectRequirements = injectRequirements;
        this.addRequirements = addRequirements;
        this.addRewards = addRewards;
        this.removeRequirements = removeRequirements;
        this.removeLoot = removeLoot;
        this.removeRecipes = removeRecipes;
    }

    private static <T> T[] insert(T[] a, T[] b) {
        T[] expanded = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, expanded, a.length, b.length);
        return expanded;
    }

    private static <T> T[] remove(T[] array, T remove) {
        for (int i = 0; i < array.length; i++) {
            if (remove.equals(array[i])) {
                System.arraycopy(array, i + 1, array, i, array.length - i - 1); // Copy the rest of the array down
                array = Arrays.copyOf(array, array.length - 1); // Shrink array by 1
            }
        }
        return array;
    }

    /**
     * @return A new advancement modifier builder
     */
    public static AdvancementModifier.Builder advancementModifier() {
        return new AdvancementModifier.Builder();
    }

    private String getCriteriaName(String criteria) {
        if (criteria.contains(":"))
            return criteria;
        if (!this.addCriteria.containsKey(criteria) && this.addCriteria.containsKey(this.id.getNamespace() + ":" + criteria))
            return this.id.getNamespace() + ":" + criteria;
        return criteria;
    }

    /**
     * @return A {@link Builder} that can be reconstructed into this modifier
     */
    public AdvancementModifier.Builder deconstruct() {
        return new AdvancementModifier.Builder(this.inject, this.priority, this.addCriteria, this.injectRequirements, this.addRequirements, this.addRewards, this.removeRequirements, this.removeLoot, this.removeRecipes);
    }

    /**
     * Modifies the specified advancement with this modifier's modifications.
     *
     * @param advancement The advancement to modify
     * @throws JsonParseException If this modifier is not valid for the specified advancement
     */
    public void modify(Advancement.Builder advancement) throws JsonParseException {
        for (String[] requirement : this.injectRequirements)
            for (String criteria : requirement) {
                criteria = this.getCriteriaName(criteria);
                if (!this.addCriteria.containsKey(criteria) && !advancement.getCriteria().containsKey(criteria))
                    throw new JsonSyntaxException("Unknown required criterion '" + criteria + "'");
            }

        AdvancementBuilderAccessor accessor = (AdvancementBuilderAccessor) advancement;
        if (!this.addCriteria.isEmpty())
            advancement.getCriteria().putAll(this.addCriteria);

        String[][] requirements = accessor.getRequirements();
        AdvancementRewards rewards = ((AdvancementBuilderAccessor) advancement).getRewards();
        ResourceLocation[] loot = ((AdvancementRewardsAccessor) rewards).getLoot();
        ResourceLocation[] recipes = ((AdvancementRewardsAccessor) rewards).getRecipes();
        int experience = ((AdvancementRewardsAccessor) rewards).getExperience();

        if (this.removeRequirements.length > 0) { // Remove first to clear out any unwanted requirements
            for (String remove : this.removeRequirements) { // Loop through all requirements to remove
                for (int i = 0; i < requirements.length; i++) { // Loop through all requirements
                    String[] requirement = requirements[i] = remove(requirements[i], remove);
                    if (requirement.length == 0) { // If empty, it's not needed anymore
                        System.arraycopy(requirements, i + 1, requirements, i, requirements.length - i - 1); // Copy the rest of the array down
                        requirements = Arrays.copyOf(requirements, requirements.length - 1); // Shrink array by 1
                    }
                }
            }
            if (requirements.length == 0)
                throw new JsonSyntaxException("At least 1 requirement must remain after removing requirements");
        }
        if (this.injectRequirements.length > 0) { // Inject into existing arrays if they haven't been removed
            if (this.injectRequirements.length > requirements.length)
                throw new JsonSyntaxException("Requirements can only be injected up to " + requirements.length + ", got " + this.injectRequirements.length);
            for (int i = 0; i < this.injectRequirements.length; i++) {
                String[] requirement = this.injectRequirements[i];
                if (requirement.length == 0)
                    continue;
                requirements[i] = insert(requirements[i], requirement);
            }
        }
        if (this.addRequirements.length > 0) // Add to the end of the requirements
            requirements = insert(requirements, this.addRequirements);

        for (String[] requirement : requirements) // Rename requirements with namespaces
            for (int i = 0; i < requirement.length; i++)
                requirement[i] = this.getCriteriaName(requirement[i]);

        // Remove unused criteria
        String[][] finalRequirements = requirements;
        advancement.getCriteria().keySet().removeIf(criteria -> {
            for (String[] requirement : finalRequirements)
                if (ArrayUtils.contains(requirement, criteria))
                    return false;
            return true;
        });

        accessor.setRequirements(requirements);

        AdvancementRewardsAccessor addRewards = (AdvancementRewardsAccessor) this.addRewards;
        if (this.removeLoot.length > 0) // Remove loot
            for (ResourceLocation remove : this.removeLoot)
                loot = remove(loot, remove);
        if (this.removeRecipes.length > 0) // Remove recipes
            for (ResourceLocation remove : this.removeRecipes)
                recipes = remove(recipes, remove);

        if (addRewards.getLoot().length > 0) // Add loot
            loot = insert(loot, addRewards.getLoot());
        if (addRewards.getRecipes().length > 0) // Add recipes
            recipes = insert(recipes, addRewards.getRecipes());
        experience += addRewards.getExperience(); // Add Experience

        accessor.setRewards(new AdvancementRewards(experience, loot, recipes, ((AdvancementRewardsAccessor) rewards).getFunction()));
    }

    /**
     * @return The id of this modifier
     */
    public ResourceLocation getId() {
        return this.id;
    }

    /**
     * @return The advancements to inject into
     */
    public ResourceLocation[] getInject() {
        return inject;
    }

    /**
     * @return The injection priority of this modifier. Lower injection priorities are applied first
     */
    public int getInjectPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvancementModifier that = (AdvancementModifier) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public static class Builder {

        private final List<ResourceLocation> inject;
        private final Map<String, Criterion> addCriteria;
        private final Map<String, Integer> injectRequirementsKeys;
        private final List<String> addRequirementsKeys;
        private final List<String> removeRequirements;
        private final List<ResourceLocation> removeLoot;
        private final List<ResourceLocation> removeRecipes;
        private int priority;
        private String[][] injectRequirements;
        private String[][] addRequirements;
        private RequirementsStrategy requirementsStrategy;
        private AdvancementRewards addRewards;

        private Builder(ResourceLocation[] inject, int priority, Map<String, Criterion> addCriteria, String[][] injectRequirements, String[][] addRequirements, AdvancementRewards addRewards, String[] removeRequirements, ResourceLocation[] removeLoot, ResourceLocation[] removeRecipes) {
            this.inject = new LinkedList<>(Arrays.asList(inject));
            this.priority = priority;
            this.addCriteria = new LinkedHashMap<>(addCriteria);
            this.injectRequirementsKeys = new LinkedHashMap<>();
            this.addRequirementsKeys = new LinkedList<>();
            this.injectRequirements = injectRequirements;
            this.addRequirements = addRequirements;
            this.addRewards = addRewards;
            this.requirementsStrategy = RequirementsStrategy.AND;
            this.removeRequirements = new LinkedList<>(Arrays.asList(removeRequirements));
            this.removeLoot = new LinkedList<>(Arrays.asList(removeLoot));
            this.removeRecipes = new LinkedList<>(Arrays.asList(removeRecipes));
        }

        private Builder() {
            this.inject = new LinkedList<>();
            this.priority = 1000;
            this.addCriteria = new LinkedHashMap<>();
            this.injectRequirementsKeys = new LinkedHashMap<>();
            this.addRequirementsKeys = new LinkedList<>();
            this.injectRequirements = null;
            this.addRequirements = null;
            this.addRewards = AdvancementRewards.EMPTY;
            this.requirementsStrategy = RequirementsStrategy.AND;
            this.removeRequirements = new LinkedList<>();
            this.removeLoot = new LinkedList<>();
            this.removeRecipes = new LinkedList<>();
        }

        private static <T> T[] getArray(JsonObject json, String name, T[] array, int minSize, Function<String, T> getter) {
            if (!json.has(name))
                return array;

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, name);
            if (jsonArray.size() < minSize)
                throw new JsonSyntaxException("Expected " + name + " to have at least " + minSize + "elements");
            if (array.length != jsonArray.size())
                array = Arrays.copyOf(array, jsonArray.size());

            for (int i = 0; i < jsonArray.size(); ++i)
                array[i] = getter.apply(GsonHelper.convertToString(jsonArray.get(i), name + "[" + i + "]"));

            return array;
        }

        private static Map<String, Criterion> getCriteriaName(Map<String, Criterion> criteria, DeserializationContext context) {
            Map<String, Criterion> result = new HashMap<>(criteria.size());
            criteria.forEach((name, criterion) -> result.put(name.contains(":") ? name : context.getAdvancementId().getNamespace() + ":" + name, criterion));
            return result;
        }

        /**
         * Deserializes a new modifier from JSON.
         *
         * @param json    The JSON to deserialize.
         * @param context The context of deserialization
         * @return The deserialized builder
         */
        public static Builder fromJson(JsonObject json, DeserializationContext context) {
            if (!json.has("inject"))
                throw new JsonSyntaxException("Missing inject, expected to find a String or JsonArray");
            JsonElement injectElement = json.get("inject");
            if (!(injectElement.isJsonPrimitive() && injectElement.getAsJsonPrimitive().isString()) && !injectElement.isJsonArray())
                throw new JsonSyntaxException("Expected inject to be a String or JsonArray, was " + GsonHelper.getType(injectElement));
            ResourceLocation[] inject = injectElement.isJsonPrimitive() && injectElement.getAsJsonPrimitive().isString() ? new ResourceLocation[]{new ResourceLocation(GsonHelper.convertToString(injectElement, "inject"))} : getArray(json, "inject", new ResourceLocation[0], 1, ResourceLocation::new);

            int priority = GsonHelper.getAsInt(json, "injectPriority", 1000);
            AdvancementRewards addRewards = json.has("addRewards") ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject(json, "addRewards")) : AdvancementRewards.EMPTY;

            Map<String, Criterion> addCriteria;
            if (json.has("addCriteria")) {
                addCriteria = getCriteriaName(Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(json, "addCriteria"), context), context);
                if (addCriteria.isEmpty())
                    throw new JsonSyntaxException("'addCriteria' cannot be empty if it's present");
            } else {
                addCriteria = Collections.emptyMap();
            }

            JsonArray injectRequirementsJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "injectRequirements", new JsonArray()));
            String[][] injectRequirements = new String[injectRequirementsJson.size()][];

            for (int i = 0; i < injectRequirementsJson.size(); ++i) {
                JsonArray requirementsJson = GsonHelper.convertToJsonArray(injectRequirementsJson.get(i), "injectRequirements[" + i + "]");
                injectRequirements[i] = new String[requirementsJson.size()];

                for (int j = 0; j < requirementsJson.size(); ++j) {
                    injectRequirements[i][j] = GsonHelper.convertToString(requirementsJson.get(j), "injectRequirements[" + i + "][" + j + "]");
                }
            }

            JsonArray addRequirementsJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "addRequirements", new JsonArray()));
            String[][] addRequirements = new String[addRequirementsJson.size()][];

            for (int i = 0; i < addRequirementsJson.size(); ++i) {
                JsonArray requirementsJson = GsonHelper.convertToJsonArray(addRequirementsJson.get(i), "addRequirements[" + i + "]");
                addRequirements[i] = new String[requirementsJson.size()];

                for (int j = 0; j < requirementsJson.size(); ++j) {
                    addRequirements[i][j] = GsonHelper.convertToString(requirementsJson.get(j), "addRequirements[" + i + "][" + j + "]");
                }
            }

            if (injectRequirements.length == 0 && addRequirements.length == 0) {
                addRequirements = new String[addCriteria.size()][];
                int i = 0;

                for (String string : addCriteria.keySet()) {
                    addRequirements[i++] = new String[]{string};
                }
            }

            for (String[] requirement : addRequirements)
                if (requirement.length == 0)
                    throw new JsonSyntaxException("Requirement entry cannot be empty");

            String[] removeRequirements = getArray(json, "removeRequirements", new String[0], 0, Function.identity());
            ResourceLocation[] removeLoot;
            ResourceLocation[] removeRecipes;
            if (json.has("removeRewards")) {
                JsonObject removeRewardsJson = GsonHelper.getAsJsonObject(json, "removeRewards");
                removeLoot = getArray(removeRewardsJson, "loot", new ResourceLocation[0], 0, ResourceLocation::new);
                removeRecipes = getArray(removeRewardsJson, "recipes", new ResourceLocation[0], 0, ResourceLocation::new);
            } else {
                removeLoot = new ResourceLocation[0];
                removeRecipes = new ResourceLocation[0];
            }

            return new Builder(inject, priority, addCriteria, injectRequirements, addRequirements, addRewards, removeRequirements, removeLoot, removeRecipes);
        }

        /**
         * Adds an advancement to inject into.
         *
         * @param id The id of the advancement
         */
        public AdvancementModifier.Builder injectInto(ResourceLocation id) {
            this.inject.add(id);
            return this;
        }

        /**
         * Sets the priority of this modifier. Lower injection priorities are applied first.
         *
         * @param priority The new priority of this modifier. By default, this is <code>1000</code>.
         */
        public AdvancementModifier.Builder injectPriority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Adds the specified rewards to this modifier.
         *
         * @param builder The builder of rewards to add
         */
        public AdvancementModifier.Builder addRewards(AdvancementRewards.Builder builder) {
            return this.addRewards(builder.build());
        }

        /**
         * Adds the specified rewards to this modifier.
         *
         * @param addRewards The rewards to add
         */
        public AdvancementModifier.Builder addRewards(AdvancementRewards addRewards) {
            this.addRewards = addRewards;
            return this;
        }

        /**
         * Adds a criterion to this modifier.
         *
         * @param name            The name of the criterion
         * @param triggerInstance The instance of the trigger to add
         */
        public AdvancementModifier.Builder addCriterion(String name, CriterionTriggerInstance triggerInstance) {
            return this.addCriterion(name, new Criterion(triggerInstance));
        }

        /**
         * Adds a criterion to this modifier.
         *
         * @param name      The name of the criterion
         * @param criterion The criterion to add
         */
        public AdvancementModifier.Builder addCriterion(String name, Criterion criterion) {
            if (this.addCriteria.put(name, criterion) != null)
                throw new IllegalArgumentException("Duplicate criterion " + name);
            this.addRequirementsKeys.add(name);
            return this;
        }

        /**
         * Injects a criterion into an existing requirement.
         *
         * @param name            The name of the criterion
         * @param index           The index to insert the criterion into
         * @param triggerInstance The instance of the trigger to add
         */
        public AdvancementModifier.Builder injectCriterion(String name, int index, CriterionTriggerInstance triggerInstance) {
            return this.injectCriterion(name, index, new Criterion(triggerInstance));
        }

        /**
         * Injects a criterion into an existing requirement.
         *
         * @param name      The name of the criterion
         * @param index     The index to insert the criterion into
         * @param criterion The criterion to add
         */
        public AdvancementModifier.Builder injectCriterion(String name, int index, Criterion criterion) {
            if (this.addCriteria.put(name, criterion) != null)
                throw new IllegalArgumentException("Duplicate criterion " + name);
            this.injectRequirementsKeys.put(name, index);
            return this;
        }

        /**
         * Removes the specified loot table from the advancement.
         *
         * @param lootTable The id of the loot table to remove
         */
        public AdvancementModifier.Builder removeLoot(ResourceLocation lootTable) {
            this.removeLoot.add(lootTable);
            return this;
        }

        /**
         * Removes the specified recipe from the advancement.
         *
         * @param recipeId The id of the recipe to remove
         */
        public AdvancementModifier.Builder removeRecipe(ResourceLocation recipeId) {
            this.removeRecipes.add(recipeId);
            return this;
        }

        /**
         * Sets the strategy for adding requirements.
         *
         * @param requirementsStrategy The new strategy
         */
        public AdvancementModifier.Builder addRequirements(RequirementsStrategy requirementsStrategy) {
            this.requirementsStrategy = requirementsStrategy;
            return this;
        }

        private void initRequirements() {
            if (this.addRequirements == null)
                this.addRequirements = this.requirementsStrategy.createRequirements(this.addRequirementsKeys);
            if (this.injectRequirements == null) {
                Map.Entry<String, Integer>[] entries = this.injectRequirementsKeys.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).<Map.Entry<String, Integer>>toArray(Map.Entry[]::new);

                this.injectRequirements = new String[this.injectRequirementsKeys.values().stream().mapToInt(i -> i).max().orElse(0)][];
                List<String> list = new LinkedList<>();
                int currentIndex = 0;
                for (Map.Entry<String, Integer> entry : entries) {
                    if (entry.getValue() != currentIndex) {
                        this.injectRequirements[currentIndex] = list.toArray(new String[0]);
                        currentIndex = entry.getValue();
                        list.clear();
                    }
                    list.add(entry.getKey());
                }
                if (!list.isEmpty())
                    this.injectRequirements[currentIndex] = list.toArray(new String[0]);
            }
        }

        /**
         * Constructs a new modifier with the specified id.
         *
         * @param id The id of the modifier to construct
         * @return A new modifier instance
         */
        public AdvancementModifier build(ResourceLocation id) {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");
            this.initRequirements();
            return new AdvancementModifier(id, this.inject.toArray(new ResourceLocation[0]), this.priority, this.addCriteria, this.injectRequirements, this.addRequirements, this.addRewards, this.removeRequirements.toArray(new String[0]), this.removeLoot.toArray(new ResourceLocation[0]), this.removeRecipes.toArray(new ResourceLocation[0]));
        }

        /**
         * Saves this modifier into the specified consumer. Used for datagens.
         *
         * @param consumer The consumer to accept modifiers
         * @param id       The id of the modifier to construct
         * @return A new modifier instance
         */
        public AdvancementModifier save(Consumer<AdvancementModifier> consumer, ResourceLocation id) {
            AdvancementModifier modifier = this.build(id);
            consumer.accept(modifier);
            return modifier;
        }

        /**
         * @return A JSON representing this modifier
         */
        public JsonObject serializeToJson() {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");
            this.initRequirements();

            JsonObject jsonObject = new JsonObject();

            if (this.inject.size() == 1) {
                jsonObject.addProperty("inject", this.inject.get(0).toString());
            } else {
                JsonArray injectJson = new JsonArray();
                for (ResourceLocation inject : this.inject)
                    injectJson.add(inject.toString());
                jsonObject.add("inject", injectJson);
            }

            if (this.priority != 1000)
                jsonObject.addProperty("priority", this.priority);

            if (!this.addCriteria.isEmpty()) {
                JsonObject addCriteriaJson = new JsonObject();
                for (Map.Entry<String, Criterion> entry : this.addCriteria.entrySet()) {
                    addCriteriaJson.add(entry.getKey(), entry.getValue().serializeToJson());
                }
                jsonObject.add("addCriteria", addCriteriaJson);
            }

            if (this.addRequirements.length > 0) {
                JsonArray addRequirementsJson = new JsonArray();
                for (String[] addRequirement : this.addRequirements) {
                    JsonArray addRequirementJson = new JsonArray();

                    for (String criterion : addRequirement)
                        addRequirementJson.add(criterion);

                    addRequirementsJson.add(addRequirementJson);
                }
                jsonObject.add("addRequirements", addRequirementsJson);
            }

            if (this.injectRequirements.length > 0) {
                JsonArray injectRequirementsJson = new JsonArray();
                for (String[] injectRequirement : this.injectRequirements) {
                    JsonArray injectRequirementJson = new JsonArray();

                    for (String criterion : injectRequirement)
                        injectRequirementJson.add(criterion);

                    injectRequirementsJson.add(injectRequirementJson);
                }
                jsonObject.add("injectRequirements", injectRequirementsJson);
            }

            jsonObject.add("addRewards", this.addRewards.serializeToJson());

            if (!this.removeRequirements.isEmpty()) {
                JsonArray removeRequirementsJson = new JsonArray();
                for (String criterion : this.removeRequirements)
                    removeRequirementsJson.add(criterion);
                jsonObject.add("removeRequirements", removeRequirementsJson);
            }

            if (!this.removeLoot.isEmpty()) {
                JsonArray removeLootJson = new JsonArray();
                for (ResourceLocation loot : this.removeLoot)
                    removeLootJson.add(loot.toString());
                jsonObject.add("removeLoot", removeLootJson);
            }

            if (!this.removeRecipes.isEmpty()) {
                JsonArray removeRecipesJson = new JsonArray();
                for (ResourceLocation recipe : this.removeRecipes)
                    removeRecipesJson.add(recipe.toString());
                jsonObject.add("removeRecipes", removeRecipesJson);
            }

            return jsonObject;
        }
    }
}
