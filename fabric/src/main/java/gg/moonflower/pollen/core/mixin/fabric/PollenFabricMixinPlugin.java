package gg.moonflower.pollen.core.mixin.fabric;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PollenFabricMixinPlugin implements IMixinConfigPlugin {

    private boolean sodiumLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        this.sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return this.sodiumLoaded ? !"gg.moonflower.pollen.core.mixin.fabric.client.LevelRendererVanillaMixin".equals(mixinClassName) : !mixinClassName.startsWith("gg.moonflower.pollen.core.mixin.sodium");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
