package gg.moonflower.pollen.core.client.profile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Maintains the internal connection to the Moonflower server.
 *
 * @author Ocelot
 */
public class ProfileConnection {

    // FIXME People are repeatedly connecting to the api, seems like something is trying to reconnect over and over in seconds? Verify fixed
    private static final String USER_AGENT = "Pollen/" + PollinatedModContainer.get(Pollen.MOD_ID).orElseThrow(() -> new IllegalStateException("No Pollen? wtf")).getVersion() + "/" + SharedConstants.getCurrentVersion().getName();
    private static final int MAX_AUTH_ATTEMPTS = 2;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new SecureRandom();
    private static final Gson GSON = new Gson();

    private final String apiUrl;
    private final String linkUrl;
    private final CompletableFuture<?> serverDown;
    private String token;

    public ProfileConnection(String apiUrl, String linkUrl) {
        this.apiUrl = apiUrl;
        this.linkUrl = linkUrl;
        this.serverDown = Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.get() ? CompletableFuture.completedFuture(false) : CompletableFuture.runAsync(() -> {
            try {
                HttpHead head = new HttpHead(apiUrl);
                try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
                    try (CloseableHttpResponse response = client.execute(head)) {
                        // Check for a connection without error
                    }
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, HttpUtil.DOWNLOAD_EXECUTOR);
    }

    @Nullable
    private static JsonElement getJsonResponse(HttpEntity entity) {
        if (entity.getContentType() != null && ContentType.APPLICATION_JSON.toString().equals(entity.getContentType().getValue())) {
            try (InputStreamReader reader = new InputStreamReader(entity.getContent())) {
                return JsonParser.parseReader(reader);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private static Entitlement parseEntitlement(JsonObject json) throws JsonSyntaxException {
        String id = GsonHelper.getAsString(json, "_id");
        JsonObject entitlementJson = GsonHelper.getAsJsonObject(json, "entitlement");
        String displayName = GsonHelper.getAsString(entitlementJson, "displayName", id);
        Entitlement.Type type = Entitlement.Type.byName(GsonHelper.getAsString(entitlementJson, "type"));
        if (type == null)
            return null;

        DataResult<? extends Entitlement> result = type.codec().parse(JsonOps.INSTANCE, entitlementJson);
        if (result.error().isPresent())
            throw new JsonSyntaxException("Failed to parse '" + id + "'. " + result.error().get().message());

        Entitlement entitlement = result.result().orElseThrow(() -> new IllegalStateException("Failed to retrieve entitlement result"));
        entitlement.setRegistryName(id);
        entitlement.setDisplayName(Component.literal(displayName));
        return entitlement;
    }

    private static JsonElement checkError(String url, HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        JsonElement json = getJsonResponse(response.getEntity());
        if (statusLine.getStatusCode() != 200 || json == null) {
            if (json != null)
                throw new IOException("Failed to connect to '" + url + "'. " + json.getAsJsonObject().get("status").getAsString() + " " + json.getAsJsonObject().get("error").getAsString());
            throw new IOException("Failed to connect to '" + url + "'. " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        }
        return json;
    }

    private static JsonElement getProfileJson(String url) throws IOException, ProfileNotFoundException {
        HttpGet get = new HttpGet(url);
        try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
            try (CloseableHttpResponse response = client.execute(get)) {
                if (response.getStatusLine().getStatusCode() == 404)
                    throw new ProfileNotFoundException();
                return checkError(url, response);
            }
        } catch (IOException | ProfileNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }

    private static JsonElement getJson(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
            try (CloseableHttpResponse response = client.execute(get)) {
                return checkError(url, response);
            }
        }
    }

    private String getBearerToken() throws IOException {
        if (this.token == null) {
            String url = this.apiUrl + "/auth/minecraft";
            byte[] data = new byte[20];
            RANDOM.nextBytes(data);
            String secret = DigestUtils.sha1Hex(this.apiUrl + new String(data));
            User user = Minecraft.getInstance().getUser();

            try {
                Minecraft.getInstance().getMinecraftSessionService().joinServer(user.getGameProfile(), user.getAccessToken(), secret);

                HttpPost post = new HttpPost(url);
                post.setEntity(EntityBuilder.create().setText("{\"uuid\":\"" + user.getGameProfile().getId() + "\",\"username\":\"" + user.getGameProfile().getName() + "\",\"secret\":\"" + secret + "\"}").setContentType(ContentType.APPLICATION_JSON).build());
                try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
                    try (CloseableHttpResponse response = client.execute(post)) {
                        return this.token = GsonHelper.getAsString(checkError(url, response).getAsJsonObject(), "token");
                    }
                }
            } catch (IOException e) {
                this.token = null;
                throw e;
            } catch (Exception e) {
                this.token = null;
                throw new IOException(e);
            }
        }
        return this.token;
    }

    private <T> T runAuthenticated(AuthRequest<T> request) throws IOException {
        AtomicReference<T> result = new AtomicReference<>();
        AuthRequestContext<T> context = new AuthRequestContext<>(result::set);
        while (result.get() == null && context.attempt < MAX_AUTH_ATTEMPTS) {
            request.run(context);
        }
        return result.get();
    }

    private void checkConnection() throws IOException {
        if (Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.get())
            throw new IllegalStateException("Moonflower profiles disabled");
        try {
            this.serverDown.join(); // Will throw a CompletionException if completed exceptionally
        } catch (Exception e) {
            if (e instanceof CompletionException) {
                Throwable cause = e.getCause();
                if (cause instanceof IOException)
                    throw (IOException) cause;
            }
            throw new IOException("Failed to connect to server", e);
        }
    }

    public CompletableFuture<Boolean> isServerDown() {
        if (this.serverDown.isCompletedExceptionally())
            return CompletableFuture.completedFuture(true);
        return this.serverDown.handle((__, e) -> e != null);
    }

    /**
     * Retrieves the data for a profile.
     *
     * @param profileId The id of the profile to retrieve
     * @return The profile for that player
     * @throws IOException If any error occurs when loading data
     */
    public ProfileData getProfileData(UUID profileId) throws IOException, ProfileNotFoundException {
        this.checkConnection();
        return GSON.fromJson(getProfileJson(this.apiUrl + "/user/minecraft/" + profileId).getAsJsonObject(), ProfileData.class);
    }

    /**
     * Retrieves all registered entitlements.
     *
     * @return The map of keys to entitlements
     * @throws IOException If any error occurs when loading data
     */
    public Map<String, Entitlement> getEntitlements() throws IOException, ProfileNotFoundException {
        this.checkConnection();
        try {
            JsonArray array = getProfileJson(this.apiUrl + "/entitlement").getAsJsonArray();
            Map<String, Entitlement> entitlementMap = new HashMap<>();
            for (JsonElement element : array) {
                try {
                    Entitlement entitlement = parseEntitlement(element.getAsJsonObject());
                    if (entitlement != null)
                        entitlementMap.put(entitlement.getRegistryName().getPath(), entitlement);
                } catch (JsonParseException e) {
                    LOGGER.error("Failed to parse entitlement: " + element, e);
                }
            }
            return entitlementMap;
        } catch (JsonParseException e) {
            throw new IOException("Failed to parse entitlements", e);
        }
    }

    /**
     * Retrieves a single registered entitlement.
     *
     * @param entitlementId The id of the entitlement to retrieve
     * @return The single entitlement or <code>null</code> if it is not a supported type
     * @throws IOException If any error occurs when loading data
     */
    @Nullable
    public Entitlement getEntitlement(String entitlementId) throws IOException {
        this.checkConnection();
        try {
            return parseEntitlement(getJson(this.apiUrl + "/entitlement/" + entitlementId).getAsJsonObject());
        } catch (JsonParseException e) {
            throw new IOException("Failed to parse entitlement", e);
        }
    }

    /**
     * Retrieves all entitlements for a profile.
     *
     * @param profileId The id of the profile to retrieve from
     * @return The map of keys to entitlements
     * @throws IOException If any error occurs when loading data
     */
    public Map<String, JsonObject> getEntitlementSettings(UUID profileId) throws IOException, ProfileNotFoundException {
        this.checkConnection();
        try {
            JsonArray array = getProfileJson(this.apiUrl + "/user/minecraft/" + profileId + "/entitlements").getAsJsonArray();
            Map<String, JsonObject> entitlementMap = new HashMap<>();
            for (JsonElement element : array) {
                try {
                    JsonObject settingsJson = element.getAsJsonObject();
                    String id = GsonHelper.getAsString(settingsJson, "id");
                    settingsJson.remove("type");
                    settingsJson.remove("id");
                    entitlementMap.put(id, settingsJson);
                } catch (JsonParseException e) {
                    LOGGER.error("Failed to parse entitlement: " + element, e);
                }
            }
            return entitlementMap;
        } catch (JsonParseException e) {
            throw new IOException("Failed to parse entitlements", e);
        }
    }

    /**
     * Retrieves settings for a single entitlement for a profile.
     *
     * @param profileId     The id of the profile to retrieve from
     * @param entitlementId The id of the entitlement to get settings for
     * @return The settings for that entitlement
     * @throws IOException If any error occurs when loading data
     */
    public JsonObject getSettings(UUID profileId, String entitlementId) throws IOException, ProfileNotFoundException {
        this.checkConnection();
        return getProfileJson(this.apiUrl + "/user/minecraft/" + profileId + "/entitlements/" + entitlementId).getAsJsonObject();
    }

    /**
     * Updates settings for a single entitlement for a profile.
     *
     * @param profileId     The id of the profile to set for
     * @param entitlementId The id of the entitlement to set settings for
     * @param newSettings   The updated settings for the entitlement. This should only contain changes
     * @return The entire settings for that entitlement as it now is server-side
     * @throws IOException If any error occurs when loading data
     */
    public JsonObject updateSettings(UUID profileId, String entitlementId, JsonObject newSettings) throws IOException, ProfileNotFoundException {
        this.checkConnection();
        return this.runAuthenticated(context -> {
            String url = this.apiUrl + "/user/minecraft/" + profileId + "/entitlements/" + entitlementId;
            HttpPatch patch = new HttpPatch(url);
            patch.setHeader("Authorization", "Bearer " + this.getBearerToken());
            patch.setEntity(EntityBuilder.create().setText(GSON.toJson(newSettings)).setContentType(ContentType.APPLICATION_JSON).build());

            try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
                try (CloseableHttpResponse response = client.execute(patch)) {
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == 404)
                        throw new ProfileNotFoundException();
                    if (statusLine.getStatusCode() == 401) {
                        context.retry();
                        return;
                    }

                    context.complete(checkError(url, response).getAsJsonObject());
                }
            }
        });
    }

    /**
     * Links the local minecraft account to a Patreon account in a browser.
     *
     * @return The status of the link
     * @throws IOException If any errors occurs when retrieving the URL
     */
    public LinkStatus linkPatreon() throws IOException {
        this.checkConnection();
        this.token = null;
        return this.runAuthenticated(context -> {
            String url = this.linkUrl + "/minecraft?token=" + this.getBearerToken() + "&ref=minecraft";
            CompletableFuture<?> connectFuture = new CompletableFuture<>();
            CompletableFuture<?> responseFuture = new CompletableFuture<>();
            AtomicReference<ServerSocket> server = new AtomicReference<>();
            CompletableFuture.runAsync(() -> {
                try (ServerSocket serverSocket = new ServerSocket(8001)) {
                    server.set(serverSocket);
                    connectFuture.complete(null);
                    try (Socket clientSocket = serverSocket.accept(); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                        out.println("HTTP/1.1 200 OK");
                        responseFuture.complete(null);
                    }
                } catch (Exception e) {
                    if (!connectFuture.isDone())
                        connectFuture.completeExceptionally(e);
                    responseFuture.completeExceptionally(e);
                } finally {
                    server.set(null);
                }

                if (!responseFuture.isDone())
                    responseFuture.completeExceptionally(new IOException("Unknown Cause"));
            }, HttpUtil.DOWNLOAD_EXECUTOR);
            context.complete(new LinkStatus(url, connectFuture, responseFuture, server));
        });
    }

    public static class LinkStatus {

        private final String url;
        private final CompletableFuture<?> connectFuture;
        private final CompletableFuture<?> responseFuture;
        private final AtomicReference<ServerSocket> server;

        public LinkStatus(String url, CompletableFuture<?> connectFuture, CompletableFuture<?> responseFuture, AtomicReference<ServerSocket> server) {
            this.url = url;
            this.connectFuture = connectFuture;
            this.responseFuture = responseFuture;
            this.server = server;
        }

        public synchronized void cancel() {
            ServerSocket serverSocket = this.server.get();
            if (serverSocket == null)
                return;

            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("Failed to cancel Patreon link", e);
                this.responseFuture.completeExceptionally(e);
            }
        }

        public String getUrl() {
            return url;
        }

        public CompletableFuture<?> getConnectFuture() {
            return connectFuture;
        }

        public CompletableFuture<?> getResponseFuture() {
            return responseFuture;
        }
    }

    private class AuthRequestContext<T> {

        private final Consumer<T> completeCallback;
        private int attempt;

        private AuthRequestContext(Consumer<T> completeCallback) {
            this.completeCallback = completeCallback;
            this.attempt = 0;
        }

        public void retry() {
            ProfileConnection.this.token = null;
            this.attempt++;
        }

        public void complete(T value) {
            this.completeCallback.accept(value);
        }
    }

    private interface AuthRequest<T> {

        void run(AuthRequestContext<T> context) throws IOException;
    }
}
