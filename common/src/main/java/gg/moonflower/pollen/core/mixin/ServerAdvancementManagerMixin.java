package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.Collections;

@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin implements PollinatedPreparableReloadListener {

    @Unique
    private static final ResourceLocation POLLEN_ID = new ResourceLocation("server_advancement_manager");
    @Unique
    private static final Collection<ResourceLocation> POLLEN_DEPENDENCIES = Collections.singleton(new ResourceLocation(Pollen.MOD_ID, "advancement_modifiers"));

    @Override
    public ResourceLocation getPollenId() {
        return POLLEN_ID;
    }

    @Override
    public Collection<ResourceLocation> getPollenDependencies() {
        return POLLEN_DEPENDENCIES;
    }
}
