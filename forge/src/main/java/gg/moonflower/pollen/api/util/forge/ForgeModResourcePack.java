package gg.moonflower.pollen.api.util.forge;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

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
        this.separator = basePath.getFileSystem().getSeparator();
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
        InputStream stream;

        Path path = getPath(filename);

        if (path != null && Files.isRegularFile(path)) {
            return Files.newInputStream(path);
        }

        stream = PollinatedModContainer.openDefault(this.container, filename);

        if (stream != null) {
            return stream;
        }

        throw new FileNotFoundException("\"" + filename + "\" in " + this.container.getBrand() + " \"" + this.container.getId() + "\"");
    }

    @Override
    protected boolean hasResource(String filename) {
        if (PollinatedModContainer.containsDefault(this.container, filename)) {
            return true;
        }

        Path path = getPath(filename);
        return path != null && Files.isRegularFile(path);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int depth, Predicate<String> predicate) {
        List<ResourceLocation> ids = new ArrayList<>();
        String nioPath = path.replace("/", this.separator);
        Path namespacePath = this.getPath(type.getDirectory() + "/" + namespace);
        if (namespacePath != null) {
            Path searchPath = namespacePath.resolve(nioPath).toAbsolutePath().normalize();
            if (Files.exists(searchPath)) {
                try {
                    Files.walk(searchPath, depth, new FileVisitOption[0]).filter(Files::isRegularFile).filter((p) -> {
                        String filename = p.getFileName().toString();
                        return !filename.endsWith(".mcmeta") && predicate.test(filename);
                    }).map(namespacePath::relativize).map((p) -> p.toString().replace(this.separator, "/")).forEach((s) -> {
                        try {
                            ids.add(new ResourceLocation(namespace, s));
                        } catch (ResourceLocationException e) {
                            LOGGER.error(e.getMessage());
                        }
                    });
                } catch (IOException var11) {
                    LOGGER.warn("getResources at " + path + " in namespace " + namespace + ", mod " + this.container.getId() + " failed!", var11);
                }
            }
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
        return PollinatedModContainer.getDisplayName(this.container);
    }

    public boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }
}
