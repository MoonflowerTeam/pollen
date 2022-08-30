package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.SurfaceRuleRegistry;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import terrablender.api.SurfaceRuleManager;

@ApiStatus.Internal
public class SurfaceRuleRegistryImpl {

    public static void register(SurfaceRuleRegistry.RuleCategory category, String namespace, SurfaceRules.RuleSource rules) {
        SurfaceRuleManager.addSurfaceRules(wrapCategory(category), namespace, rules);
    }

    public static void registerAtStage(SurfaceRuleRegistry.RuleCategory category, SurfaceRuleRegistry.RuleStage stage, int priority, SurfaceRules.RuleSource rules) {
        SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(wrapCategory(category), wrapStage(stage), priority, rules);
    }

    public static void registerDefault(SurfaceRuleRegistry.RuleCategory category, SurfaceRules.RuleSource rules) {
        SurfaceRuleManager.setDefaultSurfaceRules(wrapCategory(category), rules);
    }

    private static SurfaceRuleManager.RuleStage wrapStage(SurfaceRuleRegistry.RuleStage stage) {
        return switch (stage) {
            case BEFORE_BEDROCK -> SurfaceRuleManager.RuleStage.BEFORE_BEDROCK;
            case AFTER_BEDROCK -> SurfaceRuleManager.RuleStage.AFTER_BEDROCK;
        };
    }

    private static SurfaceRuleManager.RuleCategory wrapCategory(SurfaceRuleRegistry.RuleCategory category) {
        return switch (category) {
            case OVERWORLD -> SurfaceRuleManager.RuleCategory.OVERWORLD;
            case NETHER -> SurfaceRuleManager.RuleCategory.NETHER;
        };
    }
}
