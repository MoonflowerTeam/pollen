package gg.moonflower.pollen.api.modifier;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.modifier.serializer.DataModifierSerializer;
import gg.moonflower.pollen.api.modifier.serializer.ModifierSerializer;
import gg.moonflower.pollen.api.modifier.serializer.ResourceModifierSerializer;
import gg.moonflower.pollen.api.platform.Platform;

public interface ResourceModifierType {

    @ExpectPlatform
    static ResourceModifierType create(DataModifierSerializer serializer) {
        return Platform.error();
    }

    @ExpectPlatform
    static ResourceModifierType create(ResourceModifierSerializer serializer) {
        return Platform.error();
    }

    ModifierSerializer getSerializer();
}
