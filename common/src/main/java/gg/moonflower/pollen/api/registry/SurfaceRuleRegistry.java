package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * @author ebo2022
 * @since 1.5.0
 */
public final class SurfaceRuleRegistry {

    private SurfaceRuleRegistry() {
    }

    /**
     * Registers a surface rule for modded biomes.
     *
     * @param category  The category to register rules for
     * @param namespace The namespace of the mod using the rules
     * @param rules     The surface rules to register
     */
    @ExpectPlatform
    public static void register(RuleCategory category, String namespace, SurfaceRules.RuleSource rules) {
        Platform.error();
    }

    /**
     * Registers surface rules to the default list at the given stage.
     *
     * @param category The category to register rules for
     * @param stage    The stage to register surface rules to
     * @param priority The priority of the rules
     * @param rules    The surface rules to register
     */
    @ExpectPlatform
    public static void registerAtStage(RuleCategory category, RuleStage stage, int priority, SurfaceRules.RuleSource rules) {
        Platform.error();
    }

    /**
     * Sets a surface rule as the default for the given category.
     *
     * @param category The category to register rules for
     * @param rules    The new default surface rules
     */
    @ExpectPlatform
    public static void registerDefault(RuleCategory category, SurfaceRules.RuleSource rules) {
        Platform.error();
    }

    /**
     * Categories of surface rule generation.
     *
     * @since 1.5.0
     */
    public enum RuleCategory {
        OVERWORLD,
        NETHER
    }

    /**
     * Stages of surface rule generation.
     *
     * @since 1.5.0
     */
    public enum RuleStage {
        BEFORE_BEDROCK,
        AFTER_BEDROCK
    }
}
