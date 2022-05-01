package gg.moonflower.pollen.api.registry.content.forge;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SignRegistryImpl {

    public static synchronized WoodType register(ResourceLocation id) {
        WoodType type = WoodType.register(WoodType.create(id.toString()));
        if (Platform.isClient()) {
            Sheets.addWoodType(type);
        }
        return type;
    }
}
