package gg.moonflower.pollen.core.mixin.fabric;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PollenFabricMixinPlugin implements IMixinConfigPlugin {

    private boolean sodiumLoaded;
    private boolean irisLoaded;
    private boolean optifineLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        FabricLoader loader = FabricLoader.getInstance();
        this.sodiumLoaded = loader.isModLoaded("sodium");
        this.irisLoaded = loader.isModLoaded("iris");
        this.optifineLoaded = loader.isModLoaded("optifabric");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (this.optifineLoaded && mixinClassName.equals("gg.moonflower.pollen.core.mixin.fabric.client.ShaderInstanceMixin"))
            return false;
        if (!this.sodiumLoaded && mixinClassName.startsWith("gg.moonflower.pollen.core.mixin.fabric.sodium"))
            return false;
        if (!this.irisLoaded && mixinClassName.startsWith("gg.moonflower.pollen.core.mixin.fabric.iris"))
            return false;
        return !this.sodiumLoaded || !"gg.moonflower.pollen.core.mixin.fabric.client.LevelRendererVanillaMixin".equals(mixinClassName);
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
