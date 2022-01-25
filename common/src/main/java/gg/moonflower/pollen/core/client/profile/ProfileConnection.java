package gg.moonflower.pollen.core.client.profile;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Maintains the internal connection to the Moonflower server.
 *
 * @author Ocelot
 */
public class ProfileConnection {

    private static final String USER_AGENT = "Pollen/" + PollinatedModContainer.get(Pollen.MOD_ID).orElseThrow(() -> new IllegalStateException("No Pollen? wtf")).getVersion() + "/" + SharedConstants.getCurrentVersion().getName();
    private static final int MAX_AUTH_ATTEMPTS = 2;
    private static final int MAX_AUTH_DELAY = 60_000; // 60 seconds to successfully authenticate
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    private final String apiUrl;
    private final String linkUrl;
    private String token;

    public ProfileConnection(String apiUrl, String linkUrl) {
        this.apiUrl = apiUrl;
        this.linkUrl = linkUrl;
    }

    @Nullable
    private static JsonElement getJsonResponse(HttpEntity entity) {
        if (ContentType.APPLICATION_JSON.toString().equals(entity.getContentType().getValue())) {
            try (InputStreamReader reader = new InputStreamReader(entity.getContent())) {
                return new JsonParser().parse(reader);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return null;
    }

    private static Entitlement parseEntitlement(JsonObject json) throws JsonSyntaxException {
        String id = GsonHelper.getAsString(json, "_id");
        JsonObject entitlementJson = GsonHelper.getAsJsonObject(json, "entitlement");
        String displayName = GsonHelper.getAsString(entitlementJson, "displayName");
        Entitlement.Type type = Entitlement.Type.byName(GsonHelper.getAsString(entitlementJson, "type"));
        if (type == null)
            throw new JsonSyntaxException("Unknown entitlement type: " + GsonHelper.getAsString(entitlementJson, "type"));

        DataResult<? extends Entitlement> result = type.codec().parse(JsonOps.INSTANCE, entitlementJson);
        if (result.error().isPresent())
            throw new JsonSyntaxException("Failed to parse '" + id + "'. " + result.error().get().message());

        Entitlement entitlement = result.result().orElseThrow(() -> new IllegalStateException("Failed to retrieve entitlement result"));
        entitlement.setRegistryName(id);
        entitlement.setDisplayName(new TextComponent(displayName));
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
            String url = this.apiUrl + "/auth";
            String secret = DigestUtils.sha1Hex(url);
            User user = Minecraft.getInstance().getUser();

            try {
                Minecraft.getInstance().getMinecraftSessionService().joinServer(user.getGameProfile(), user.getAccessToken(), secret);

                HttpPost post = new HttpPost(url);
                post.setEntity(new StringEntity("{\"uuid\":\"" + user.getGameProfile().getId() + "\",\"username\":\"" + user.getGameProfile().getName() + "\",\"secret\":\"" + secret + "\"}"));
                post.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());

                try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
                    try (CloseableHttpResponse response = client.execute(post)) {
                        return this.token = GsonHelper.getAsString(checkError(url, response).getAsJsonObject(), "token");
                    }
                }
            } catch (Exception e) {
                this.token = null;
                throw new IOException(e);
            }
        }
        return this.token;
    }

    private <T> T runAuthenticated(AuthRequest<T> request) throws IOException {
        AtomicReference<T> result = new AtomicReference<>();
        AuthRequestContext<T> context = new AuthRequestContext<T>(result::set);
        while (result.get() == null && context.attempt < MAX_AUTH_ATTEMPTS) {
            request.run(context);
        }
        return result.get();
    }

    /**
     * Retrieves the data for a profile.
     *
     * @param profileId The id of the profile to retrieve
     * @return The profile for that player
     * @throws IOException If any error occurs when loading data
     */
    public ProfileData getProfileData(UUID profileId) throws IOException {
        return GSON.fromJson(getJson(this.apiUrl + "/profiles/" + profileId).getAsJsonObject(), ProfileData.class);
    }

    /**
     * Retrieves all registered entitlements.
     *
     * @return The map of keys to entitlements
     * @throws IOException If any error occurs when loading data
     */
    public Map<String, Entitlement> getEntitlements() throws IOException {
        try {
            JsonArray array = getJson(this.apiUrl + "/entitlements").getAsJsonArray();
            Map<String, Entitlement> entitlementMap = new HashMap<>();
            for (JsonElement element : array) {
                try {
                    Entitlement entitlement = parseEntitlement(element.getAsJsonObject());
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
     * @return The single entitlement
     * @throws IOException If any error occurs when loading data
     */
    public Entitlement getEntitlement(String entitlementId) throws IOException {
        try {
            return parseEntitlement(getJson(this.apiUrl + "/entitlements/" + entitlementId).getAsJsonObject());
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
    public Map<String, JsonObject> getEntitlementSettings(UUID profileId) throws IOException {
        try {
            JsonArray array = getJson(this.apiUrl + "/profiles/" + profileId + "/entitlements").getAsJsonArray();
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
    public JsonObject getSettings(UUID profileId, String entitlementId) throws IOException {
        return getJson(this.apiUrl + "/profiles/" + profileId + "/entitlements/" + entitlementId).getAsJsonObject();
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
    public JsonObject updateSettings(UUID profileId, String entitlementId, JsonObject newSettings) throws IOException {
        return this.runAuthenticated(context -> {
            String url = this.apiUrl + "/profiles/" + profileId + "/entitlements/" + entitlementId;
            HttpPatch patch = new HttpPatch(url);
            patch.setHeader("Authorization", "Bearer " + this.getBearerToken());
            patch.setEntity(new StringEntity(GSON.toJson(newSettings)));

            try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
                try (CloseableHttpResponse response = client.execute(patch)) {
                    StatusLine statusLine = response.getStatusLine();
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
        return this.runAuthenticated(context -> {
            String url = this.linkUrl + "/minecraft?token=" + this.getBearerToken() + "&ref=minecraft";
            CompletableFuture<?> connectFuture = new CompletableFuture<>();
            CompletableFuture<?> responseFuture = new CompletableFuture<>();
            CompletableFuture.runAsync(() -> {
                try (ServerSocket serverSocket = new ServerSocket(8001, 1)) {
                    serverSocket.setSoTimeout(MAX_AUTH_DELAY);
                    connectFuture.complete(null);
                    try (Socket clientSocket = serverSocket.accept(); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        if (in.readLine().startsWith("POST"))
                            responseFuture.complete(null);
                        out.println(responseFuture.isDone() ? "HTTP/1.1 200 OK" : "HTTP/1.1 400 Bad Request");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!connectFuture.isDone())
                        connectFuture.completeExceptionally(e);
                    responseFuture.completeExceptionally(e);
                }

                if (!responseFuture.isDone())
                    responseFuture.completeExceptionally(new IOException("Unknown Cause"));
            }, HttpUtil.DOWNLOAD_EXECUTOR);
            context.complete(new LinkStatus(url, connectFuture, responseFuture));
        });
    }

    public static class LinkStatus {

        private final String url;
        private final CompletableFuture<?> connectFuture;
        private final CompletableFuture<?> responseFuture;

        public LinkStatus(String url, CompletableFuture<?> connectFuture, CompletableFuture<?> responseFuture) {
            this.url = url;
            this.connectFuture = connectFuture;
            this.responseFuture = responseFuture;
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

        public void retry() throws IOException {
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
