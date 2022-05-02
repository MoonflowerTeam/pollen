package gg.moonflower.pollen.api.resource.modifier.type;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifier;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierType;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.core.mixin.data.AdvancementBuilderAccessor;
import gg.moonflower.pollen.core.mixin.data.AdvancementRewardsAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Modifies existing advancements to add extra criteria, requirements, and rewards.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AdvancementModifier extends ResourceModifier<Advancement.Builder> {

    private final Map<String, Criterion> addCriteria;
    private final String[][] injectRequirements;
    private final String[][] addRequirements;
    private final AdvancementRewards addRewards;
    private final String[] removeRequirements;
    private final ResourceLocation[] removeLoot;
    private final ResourceLocation[] removeRecipes;

    public AdvancementModifier(ResourceLocation id, ResourceLocation[] inject, int priority, Map<String, Criterion> addCriteria, String[][] injectRequirements, String[][] addRequirements, AdvancementRewards addRewards, String[] removeRequirements, ResourceLocation[] removeLoot, ResourceLocation[] removeRecipes) {
        super(id, inject, priority);
        this.addCriteria = ImmutableMap.copyOf(addCriteria);
        this.injectRequirements = injectRequirements;
        this.addRequirements = addRequirements;
        this.addRewards = addRewards;
        this.removeRequirements = removeRequirements;
        this.removeLoot = removeLoot;
        this.removeRecipes = removeRecipes;
    }

    private String getCriteriaName(String criteria) {
        if (criteria.contains(":"))
            return criteria;
        if (!this.addCriteria.containsKey(criteria) && this.addCriteria.containsKey(this.id.getNamespace() + ":" + criteria))
            return this.id.getNamespace() + ":" + criteria;
        return criteria;
    }

    /**
     * @return A new advancement modifier builder
     */
    public static AdvancementModifier.Builder advancementModifier() {
        return new AdvancementModifier.Builder();
    }

    /**
     * @return A builder for this modifier
     */
    public AdvancementModifier.Builder deconstruct() {
        return new AdvancementModifier.Builder(this.inject, this.priority, this.addCriteria, this.injectRequirements, this.addRequirements, this.addRewards, this.removeRequirements, this.removeLoot, this.removeRecipes);
    }

    @Override
    public void modify(Advancement.Builder resource) throws JsonParseException {
        for (String[] requirement : this.injectRequirements)
            for (String criteria : requirement) {
                criteria = this.getCriteriaName(criteria);
                if (!this.addCriteria.containsKey(criteria) && !resource.getCriteria().containsKey(criteria))
                    throw new JsonSyntaxException("Unknown required criterion '" + criteria + "'");
            }

        AdvancementBuilderAccessor accessor = (AdvancementBuilderAccessor) resource;
        if (!this.addCriteria.isEmpty())
            resource.getCriteria().putAll(this.addCriteria);

        String[][] requirements = accessor.getRequirements();
        AdvancementRewards rewards = ((AdvancementBuilderAccessor) resource).getRewards();
        ResourceLocation[] loot = ((AdvancementRewardsAccessor) rewards).getLoot();
        ResourceLocation[] recipes = ((AdvancementRewardsAccessor) rewards).getRecipes();
        int experience = ((AdvancementRewardsAccessor) rewards).getExperience();

        if (this.removeRequirements.length > 0) { // Remove first to clear out any unwanted requirements
            for (String remove : this.removeRequirements) { // Loop through all requirements to remove
                for (int i = 0; i < requirements.length; i++) { // Loop through all requirements
                    String[] requirement = requirements[i] = JSONTupleParser.remove(requirements[i], remove);
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
                requirements[i] = JSONTupleParser.insert(requirements[i], requirement);
            }
        }
        if (this.addRequirements.length > 0) // Add to the end of the requirements
            requirements = JSONTupleParser.insert(requirements, this.addRequirements);

        for (String[] requirement : requirements) // Rename requirements with namespaces
            for (int i = 0; i < requirement.length; i++)
                requirement[i] = this.getCriteriaName(requirement[i]);

        // Remove unused criteria
        String[][] finalRequirements = requirements;
        resource.getCriteria().keySet().removeIf(criteria -> {
            for (String[] requirement : finalRequirements)
                if (ArrayUtils.contains(requirement, criteria))
                    return false;
            return true;
        });

        accessor.setRequirements(requirements);

        AdvancementRewardsAccessor addRewards = (AdvancementRewardsAccessor) this.addRewards;
        if (this.removeLoot.length > 0) // Remove loot
            for (ResourceLocation remove : this.removeLoot)
                loot = JSONTupleParser.remove(loot, remove);
        if (this.removeRecipes.length > 0) // Remove recipes
            for (ResourceLocation remove : this.removeRecipes)
                recipes = JSONTupleParser.remove(recipes, remove);

        if (addRewards.getLoot().length > 0) // Add loot
            loot = JSONTupleParser.insert(loot, addRewards.getLoot());
        if (addRewards.getRecipes().length > 0) // Add recipes
            recipes = JSONTupleParser.insert(recipes, addRewards.getRecipes());
        experience += addRewards.getExperience(); // Add Experience

        accessor.setRewards(new AdvancementRewards(experience, loot, recipes, ((AdvancementRewardsAccessor) rewards).getFunction()));
    }

    @Override
    public ResourceModifierType getType() {
        return ResourceModifierManager.ADVANCEMENT.get();
    }

    public static class Builder extends ResourceModifier.Builder<AdvancementModifier, Builder> {

        private final Map<String, Criterion> addCriteria;
        private final Map<String, Integer> injectRequirementsKeys;
        private final List<String> addRequirementsKeys;
        private final List<String> removeRequirements;
        private final List<ResourceLocation> removeLoot;
        private final List<ResourceLocation> removeRecipes;
        private String[][] injectRequirements;
        private String[][] addRequirements;
        private RequirementsStrategy requirementsStrategy;
        private AdvancementRewards addRewards;

        private Builder(ResourceLocation[] inject, int priority, Map<String, Criterion> addCriteria, String[][] injectRequirements, String[][] addRequirements, AdvancementRewards addRewards, String[] removeRequirements, ResourceLocation[] removeLoot, ResourceLocation[] removeRecipes) {
            super(inject, priority);
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
            super();
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

        private static Map<String, Criterion> getCriteriaName(Map<String, Criterion> criteria, DeserializationContext context) {
            Map<String, Criterion> result = new HashMap<>(criteria.size());
            criteria.forEach((name, criterion) -> result.put(name.contains(":") ? name : context.getAdvancementId().getNamespace() + ":" + name, criterion));
            return result;
        }

        /**
         * Deserializes a new modifier from JSON.
         *
         * @param name            The name of the
         * @param serverResources The server resources instance
         * @param json            The JSON to deserialize
         * @param inject          The resources to inject into
         * @param priority        The priority of this injection over others
         * @return The deserialized builder
         */
        public static Builder fromJson(ResourceLocation name, ReloadableServerResources serverResources, JsonObject json, ResourceLocation[] inject, int priority) {
            DeserializationContext context = new DeserializationContext(name, serverResources.getPredicateManager());
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

            String[] removeRequirements = JSONTupleParser.getArray(json, "removeRequirements", new String[0], 0, Function.identity());
            ResourceLocation[] removeLoot;
            ResourceLocation[] removeRecipes;
            if (json.has("removeRewards")) {
                JsonObject removeRewardsJson = GsonHelper.getAsJsonObject(json, "removeRewards");
                removeLoot = JSONTupleParser.getArray(removeRewardsJson, "loot", new ResourceLocation[0], 0, ResourceLocation::new);
                removeRecipes = JSONTupleParser.getArray(removeRewardsJson, "recipes", new ResourceLocation[0], 0, ResourceLocation::new);
            } else {
                removeLoot = new ResourceLocation[0];
                removeRecipes = new ResourceLocation[0];
            }

            return new Builder(inject, priority, addCriteria, injectRequirements, addRequirements, addRewards, removeRequirements, removeLoot, removeRecipes);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        protected ResourceModifierType getType() {
            return ResourceModifierManager.ADVANCEMENT.get();
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

        @Override
        public AdvancementModifier build(ResourceLocation id) {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");
            this.initRequirements();
            return new AdvancementModifier(id, this.inject.toArray(new ResourceLocation[0]), this.priority, this.addCriteria, this.injectRequirements, this.addRequirements, this.addRewards, this.removeRequirements.toArray(new String[0]), this.removeLoot.toArray(new ResourceLocation[0]), this.removeRecipes.toArray(new ResourceLocation[0]));
        }

        @Override
        protected void serializeProperties(JsonObject json) {
            this.initRequirements();

            if (!this.addCriteria.isEmpty()) {
                JsonObject addCriteriaJson = new JsonObject();
                for (Map.Entry<String, Criterion> entry : this.addCriteria.entrySet()) {
                    addCriteriaJson.add(entry.getKey(), entry.getValue().serializeToJson());
                }
                json.add("addCriteria", addCriteriaJson);
            }

            if (this.addRequirements.length > 0) {
                JsonArray addRequirementsJson = new JsonArray();
                for (String[] addRequirement : this.addRequirements) {
                    JsonArray addRequirementJson = new JsonArray();

                    for (String criterion : addRequirement)
                        addRequirementJson.add(criterion);

                    addRequirementsJson.add(addRequirementJson);
                }
                json.add("addRequirements", addRequirementsJson);
            }

            if (this.injectRequirements.length > 0) {
                JsonArray injectRequirementsJson = new JsonArray();
                for (String[] injectRequirement : this.injectRequirements) {
                    JsonArray injectRequirementJson = new JsonArray();

                    for (String criterion : injectRequirement)
                        injectRequirementJson.add(criterion);

                    injectRequirementsJson.add(injectRequirementJson);
                }
                json.add("injectRequirements", injectRequirementsJson);
            }

            json.add("addRewards", this.addRewards.serializeToJson());

            if (!this.removeRequirements.isEmpty()) {
                JsonArray removeRequirementsJson = new JsonArray();
                for (String criterion : this.removeRequirements)
                    removeRequirementsJson.add(criterion);
                json.add("removeRequirements", removeRequirementsJson);
            }

            if (!this.removeLoot.isEmpty()) {
                JsonArray removeLootJson = new JsonArray();
                for (ResourceLocation loot : this.removeLoot)
                    removeLootJson.add(loot.toString());
                json.add("removeLoot", removeLootJson);
            }

            if (!this.removeRecipes.isEmpty()) {
                JsonArray removeRecipesJson = new JsonArray();
                for (ResourceLocation recipe : this.removeRecipes)
                    removeRecipesJson.add(recipe.toString());
                json.add("removeRecipes", removeRecipesJson);
            }
        }
    }
}
