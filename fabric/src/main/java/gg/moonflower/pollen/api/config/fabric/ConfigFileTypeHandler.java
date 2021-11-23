package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import gg.moonflower.pollen.api.event.events.ConfigEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class ConfigFileTypeHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    static ConfigFileTypeHandler TOML = new ConfigFileTypeHandler();

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig) {
        backUpConfig(commentedFileConfig, 5);
    }

    public static void backUpConfig(CommentedFileConfig commentedFileConfig, int maxBackups) {
        Path bakFileLocation = commentedFileConfig.getNioPath().getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFile().getName());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFile().getName()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try {
            for (int i = maxBackups; i > 0; i--) {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if (Files.exists(oldBak)) {
                    if (i >= maxBackups)
                        Files.delete(oldBak);
                    else
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                }
            }
            Files.copy(commentedFileConfig.getNioPath(), bakFile);
        } catch (IOException e) {
            LOGGER.warn("Failed to back up config file {}", commentedFileConfig.getNioPath(), e);
        }
    }

    public Function<PollinatedModConfigImpl, CommentedFileConfig> reader(Path configBasePath) {
        return config -> {
            Path configPath = configBasePath.resolve(config.getFileName());
            CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                    preserveInsertionOrder().
                    autosave().
                    onFileNotFound(this::setupConfigFile).
                    writingMode(WritingMode.REPLACE).
                    build();
            LOGGER.debug("Built TOML config for {}", configPath.toString());
            try {
                configData.load();
            } catch (ParsingException ex) {
                throw new ConfigLoadingException(config, ex);
            }
            LOGGER.debug("Loaded TOML config file {}", configPath.toString());
            try {
                FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(config, configData, Thread.currentThread().getContextClassLoader()));
                LOGGER.debug("Watching TOML config file {} for changes", configPath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Couldn't watch config file", e);
            }
            return configData;
        };
    }

    public void unload(Path configBasePath, PollinatedModConfigImpl config) {
        Path configPath = configBasePath.resolve(config.getFileName());
        try {
            FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(config.getFileName()));
        } catch (RuntimeException e) {
            LOGGER.error("Failed to remove config {} from tracker!", configPath.toString(), e);
        }
    }

    private boolean setupConfigFile(Path file, ConfigFormat<?> conf) throws IOException {
        Files.createFile(file);
        conf.initEmptyFile(file);
        return true;
    }

    private static class ConfigWatcher implements Runnable {

        private final PollinatedModConfigImpl modConfig;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final PollinatedModConfigImpl modConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.modConfig = modConfig;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(this.realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try {
                    this.commentedFileConfig.load();
                    if (!this.modConfig.getSpec().isCorrect(this.commentedFileConfig)) {
                        LOGGER.warn("Configuration file {} is not correct. Correcting", this.commentedFileConfig.getFile().getAbsolutePath());
                        ConfigFileTypeHandler.backUpConfig(this.commentedFileConfig);
                        this.modConfig.getSpec().correct(this.commentedFileConfig);
                        this.commentedFileConfig.save();
                    }
                } catch (ParsingException e) {
                    throw new ConfigLoadingException(this.modConfig, e);
                }
                LOGGER.debug("Config file {} changed, sending notifies", this.modConfig.getFileName());
                this.modConfig.getSpec().afterReload();
                ConfigEvent.RELOADING.invoker().configChanged(this.modConfig);
            }
        }
    }

    private static class ConfigLoadingException extends RuntimeException {

        public ConfigLoadingException(PollinatedModConfigImpl config, Throwable cause) {
            super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
        }
    }
}
