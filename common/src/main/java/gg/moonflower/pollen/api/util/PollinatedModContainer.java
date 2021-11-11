package gg.moonflower.pollen.api.util;

import com.google.common.base.Charsets;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.SharedConstants;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public interface PollinatedModContainer {

    @ExpectPlatform
    static Optional<PollinatedModContainer> get(String modId) {
        return Platform.error();
    }

    static boolean containsDefault(PollinatedModContainer container, String filename) {
        return "pack.mcmeta".equals(filename);
    }

    static InputStream openDefault(PollinatedModContainer container, String filename) {
        if ("pack.mcmeta".equals(filename)) {
            String description = container.getName();

            if (description == null) {
                description = "";
            } else {
                description = description.replaceAll("\"", "\\\"");
            }

            String pack = String.format("{\"pack\":{\"pack_format\":" + SharedConstants.getCurrentVersion().getPackVersion() + ",\"description\":\"%s\"}}", description);
            return IOUtils.toInputStream(pack, Charsets.UTF_8);
        }
        return null;
    }

    static String getDisplayName(PollinatedModContainer container) {
        if (container.getName() != null) {
            return container.getName();
        } else {
            return container.getBrand() + " \"" + container.getId() + "\"";
        }
    }

    String getBrand();

    Path getRootPath();

    String getId();

    @Nullable
    String getName();
}
