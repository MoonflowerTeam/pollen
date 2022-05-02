package gg.moonflower.pollen.api.registry.content.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.mixin.fabric.WoodTypeAccessor;
import gg.moonflower.pollen.core.mixin.fabric.client.SheetsAccessor;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SignRegistryImpl {

    public static WoodType register(ResourceLocation id) {
        WoodType type = WoodTypeAccessor.invokeRegister(new WoodTypeImpl(id));
        if (Platform.isClient()) {
            Sheets.SIGN_MATERIALS.put(type, SheetsAccessor.invokeCreateSignMaterial(type));
        }
        return type;
    }

    public static class WoodTypeImpl extends WoodType {

        private final ResourceLocation id;

        private WoodTypeImpl(ResourceLocation id) {
            super(id.getPath()); // Guarantees no ResourceLocationException(s), though less parity with Forge. If this is an issue, it can be changed.
            this.id = id;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}
