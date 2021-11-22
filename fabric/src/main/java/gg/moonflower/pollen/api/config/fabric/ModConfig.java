package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.fabricmc.loader.api.ModContainer;

import java.util.concurrent.Callable;

public class ModConfig {
    private final Type type;
    private final FabricConfigSpec spec;
    private final String fileName;
    private final ModContainer container;
    private final ConfigFileTypeHandler configHandler;
    private CommentedConfig configData;
    private Callable<Void> saveHandler;
}
