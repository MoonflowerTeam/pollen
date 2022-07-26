package gg.moonflower.pollen.api.util.forge;

import com.google.common.base.Charsets;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@ApiStatus.Internal
public class ForgeModResourcePack extends AbstractPackResources {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_]+");
    private final PollinatedModContainer container;
    private final Path basePath;
    private final AutoCloseable closer;
    private final String separator;
    private final boolean enabledByDefault;

    public ForgeModResourcePack(PollinatedModContainer container, Path path, AutoCloseable closer, boolean enabledByDefault) {
        super(null);
        this.container = container;
        this.basePath = path.toAbsolutePath().normalize();
        this.closer = closer;
        this.separator = this.basePath.getFileSystem().getSeparator();
        this.enabledByDefault = enabledByDefault;
    }

    private Path getPath(String filename) {
        Path childPath = this.basePath.resolve(filename.replace("/", this.separator)).toAbsolutePath().normalize();

        if (childPath.startsWith(this.basePath) && Files.exists(childPath)) {
            return childPath;
        } else {
            return null;
        }
    }

    @Override
    protected InputStream getResource(String filename) throws IOException {
        Path path = getPath(filename);
        if (path != null && Files.isRegularFile(path))
            return Files.newInputStream(path);

        if ("pack.mcmeta".equals(filename)) {
            String description = this.getName();

            if (description == null) {
                description = "";
            } else {
                description = description.replaceAll("\"", "\\\"");
            }

            String pack = String.format("{\"pack\":{\"pack_format\":" + SharedConstants.getCurrentVersion().getPackVersion(com.mojang.bridge.game.PackType.RESOURCE) + ",\"description\":\"%s\"}}", description);
            return IOUtils.toInputStream(pack, Charsets.UTF_8);
        }
        throw new FileNotFoundException("\"" + filename + "\" in " + this.container.getBrand() + " Mod \"" + this.container.getId() + "\"");
    }

    @Override
    protected boolean hasResource(String filename) {
        if ("pack.mcmeta".equals(filename))
            return true;

        Path path = this.getPath(filename);
        return path != null && Files.isRegularFile(path);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, Predicate<ResourceLocation> predicate) {
        List<ResourceLocation> ids = new ArrayList<>();
        Path namespacePath = this.getPath(type.getDirectory() + "/" + namespace);
        if (namespacePath == null)
            return Collections.emptySet();

        Path searchPath = namespacePath.resolve(path.replace("/", this.separator)).normalize();
        if (!Files.exists(searchPath))
            return Collections.emptySet();

        try(Stream<Path> stream = Files.walk(searchPath)) {
            stream.filter(Files::isRegularFile).filter(p -> {
                String fileName = p.getFileName().toString();
                return !fileName.endsWith(".mcmeta");
            }).map(namespacePath::relativize).map(p -> p.toString().replace(this.separator, "/")).forEach(s -> {
                try {
                    ResourceLocation id = new ResourceLocation(namespace, s);
                    if (predicate.test(id)) ids.add(id);
                } catch (ResourceLocationException e) {
                    LOGGER.error(e.getMessage());
                }
            });
        } catch (IOException e) {
            LOGGER.warn("getResources at " + path + " in namespace " + namespace + ", mod " + this.container.getId() + " failed!", e);
        }

        return ids;
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        try {
            Path typePath = getPath(type.getDirectory());

            if (typePath == null || !(Files.isDirectory(typePath))) {
                return Collections.emptySet();
            }

            Set<String> namespaces = new HashSet<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(typePath, Files::isDirectory)) {
                for (Path path : stream) {
                    String s = path.getFileName().toString();
                    // s may contain trailing slashes, remove them
                    s = s.replace(this.separator, "");

                    if (RESOURCE_PACK_PATH.matcher(s).matches()) {
                        namespaces.add(s);
                    } else {
                        LOGGER.warn(this.getClass().getSimpleName() + ": ignored invalid namespace: {} in mod ID {}", s, this.container.getId());
                    }
                }
            }

            return namespaces;
        } catch (IOException e) {
            LOGGER.warn("getNamespaces in mod " + this.container.getId() + " failed!", e);
            return Collections.emptySet();
        }
    }

    @Override
    public void close() {
        if (closer != null) {
            try {
                closer.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getName() {
        return this.container.getDisplayName();
    }

    public boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }
}
