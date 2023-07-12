package gg.moonflower.pollen.api.pinwheelbridge.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.texture.TextureLocation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pollen.impl.render.geometry.PoseStackWrapper;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Base32;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public interface PinwheelBridge {

    static MatrixStack wrap(PoseStack poseStack) {
        return new PoseStackWrapper(poseStack);
    }

    static ResourceLocation toLocation(TextureLocation location) {
        if (location.isOnline()) {
            return new ResourceLocation(location.namespace(), "base32" + new Base32().encodeToString(location.path().getBytes(StandardCharsets.UTF_8)).toLowerCase(Locale.ROOT).replaceAll("=", "_"));
        }
        return new ResourceLocation(location.namespace(), location.path());
    }
}
