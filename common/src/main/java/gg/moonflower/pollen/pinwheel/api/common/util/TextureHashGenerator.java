package gg.moonflower.pollen.pinwheel.api.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>Generates hash files automagically.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class TextureHashGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            throw new IllegalArgumentException("First argument is expected to be url base.");

        String urlBase = args[0];
        Path source = Paths.get("src/generated/input");
        Path result = Paths.get("src/generated/hashes.json");

        if (!Files.exists(source)) {
            Files.createDirectories(source);
            return;
        }

        JsonObject hashes = new JsonObject();
        Files.walk(source).forEach(child ->
        {
            if (!Files.isRegularFile(child))
                return;
            try (InputStream stream = new FileInputStream(child.toFile())) {
                hashes.addProperty(urlBase + child.toString().replaceAll("\\\\", "/").substring("src/generated/input/".length()), DigestUtils.md5Hex(IOUtils.toByteArray(stream)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (!Files.exists(result))
            Files.createFile(result);

        try (FileOutputStream os = new FileOutputStream(result.toFile())) {
            IOUtils.write(GSON.toJson(hashes), os, StandardCharsets.UTF_8);
        }
    }
}
