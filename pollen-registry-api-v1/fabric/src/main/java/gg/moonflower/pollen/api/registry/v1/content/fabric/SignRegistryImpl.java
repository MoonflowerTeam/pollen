package gg.moonflower.pollen.api.registry.v1.content.fabric;

import gg.moonflower.pollen.mixin.WoodTypeAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SignRegistryImpl {

    public static WoodType register(ResourceLocation id) {
        WoodType type = WoodTypeAccessor.invokeRegister(new WoodTypeImpl(id));
        if (Platform.isClient())
            Sheets.SIGN_MATERIALS.put(type, SheetsAccessor.invokeCreateSignMaterial(type));
        return type;
    }

    public static class WoodTypeImpl extends WoodType {

        private final ResourceLocation id;

        private WoodTypeImpl(ResourceLocation id) {
            super(id.toString());
            this.id = id;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}
