package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sheets.class)
public interface SheetsAccessor {

    @Invoker
    static Material invokeCreateSignMaterial(WoodType type) {
        return Platform.error();
    }
}
