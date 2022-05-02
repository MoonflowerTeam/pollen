package gg.moonflower.pollen.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A mod instance for initializing mods on the common side.
 *
 * @author Jackson
 * @since 1.0.0
 */
public abstract class Platform {

    private static final boolean FORGE = ArchitecturyTarget.getCurrentTarget().equals(PlatformOnly.FORGE);
    private static final Supplier<RegistryAccess> CLIENT_REGISTRY_ACCESS = () -> {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null ? listener.registryAccess() : null;
    };
    private static final Supplier<RecipeManager> CLIENT_RECIPE_MANAGER = () -> {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null ? listener.getRecipeManager() : null;
    };

    private final String modId;

    protected Platform(String modId) {
        this.modId = modId;
    }

    public static Builder builder(String modId) {
        return new Builder(modId);
    }

    public static <T> T error() {
        throw new AssertionError();
    }

    /**
     * @return Whether this mod is running in a production environment
     */
    @ExpectPlatform
    public static boolean isProduction() {
        return Platform.error();
    }

    /**
     * @return Whether the current environment is a client
     */
    @ExpectPlatform
    public static boolean isClient() {
        return Platform.error();
    }

    /**
     * @return Whether Optifine is breaking the game
     */
    @ExpectPlatform
    public static boolean isOptifineLoaded() {
        return Platform.error();
    }

    /**
     * @return The main game executor. This is the Minecraft Client or Server instance
     */
    @ExpectPlatform
    public static BlockableEventLoop<?> getGameExecutor() {
        return Platform.error();
    }

    /**
     * Checks to see if the specified mod is loaded.
     *
     * @param modId The id of the mod to check
     * @return Whether that mod is loaded
     */
    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        return Platform.error();
    }

    /**
     * @return A stream of all loaded mods
     */
    @ExpectPlatform
    public static Stream<PollinatedModContainer> getMods() {
        return Platform.error();
    }

    /**
     * @return The currently running Minecraft Server instance. This will not be present in a remote client level
     */
    public static Optional<MinecraftServer> getRunningServer() {
        return Optional.ofNullable(Pollen.getRunningServer());
    }

    /**
     * @return The access to registries for the running server or client
     */
    public static Optional<RegistryAccess> getRegistryAccess() {
        MinecraftServer server = Pollen.getRunningServer();
        if (server != null)
            return Optional.of(server.registryAccess());
        return isClient() ? Optional.ofNullable(CLIENT_REGISTRY_ACCESS.get()) : Optional.empty();
    }

    /**
     * @return The recipe manager for the running server or client
     */
    public static Optional<RecipeManager> getRecipeManager() {
        MinecraftServer server = Pollen.getRunningServer();
        if (server != null)
            return Optional.of(server.getRecipeManager());
        return isClient() ? Optional.ofNullable(CLIENT_RECIPE_MANAGER.get()) : Optional.empty();
    }

    /**
     * @return Whether the currently running platform is Forge
     */
    public static boolean isForge() {
        return FORGE;
    }

    /**
     * @return The mod id for the platform.
     */
    public String getModId() {
        return modId;
    }

    /**
     * Loads the {@link Platform}.
     *
     * <p>Fabric users should not run this on their client initializer. Running this on the common initializer will handle client initialization too.
     */
    public abstract void setup();

    /**
     * Loads the {@link Platform} data generator.
     *
     * <p>Fabric users need to run this on the data initializer in order for them to work
     */
    @PlatformOnly(PlatformOnly.FABRIC)
    public void dataSetup(DataGenerator dataGenerator) {
    }

    /**
     * Used as context for initializing mods during loading lifecycle.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public interface ModSetupContext {

        /**
         * Queues work to happen later when it is safe to do so.
         * <p><i>NOTE: The returned future may execute on the current thread so it is not safe to call {@link CompletableFuture#join()} or {@link CompletableFuture#get()}</i>
         *
         * @param work The work to do
         * @return A future for when the work is done
         */
        CompletableFuture<Void> enqueueWork(Runnable work);

        /**
         * Queues work to happen later when it is safe to do so.
         * <p><i>NOTE: The returned future may execute on the current thread so it is not safe to call {@link CompletableFuture#join()} or {@link CompletableFuture#get()}</i>
         *
         * @param work The work to do
         * @return A future for when the work is done
         */
        <T> CompletableFuture<T> enqueueWork(Supplier<T> work);
    }

    /**
     * Used as context for initializing data generators.
     *
     * @author Ocelot
     * @see DataProvider
     * @since 1.0.0
     */
    public interface DataSetupContext {

        /**
         * @return The actual data generator to add providers to
         */
        DataGenerator getGenerator();

        /**
         * @return The mod the generator is running for
         */
        PollinatedModContainer getMod();
    }

    public static class Builder {

        private final String modId;

        private Runnable commonInit = () -> {
        };
        private Supplier<Runnable> clientInit = () -> () -> {
        };
        private Supplier<Runnable> serverInit = () -> () -> {
        };
        private Consumer<ModSetupContext> commonPostInit = __ -> {
        };
        private Supplier<Consumer<ModSetupContext>> clientPostInit = () -> __ -> {
        };
        private Supplier<Consumer<ModSetupContext>> serverPostInit = () -> __ -> {
        };
        private Consumer<DataSetupContext> dataInit = __ -> {
        };

        private Builder(String modId) {
            this.modId = modId;
        }

        @ApiStatus.Internal
        @ExpectPlatform
        public static Platform buildImpl(String modId, Runnable commonInit, Supplier<Runnable> clientInit, Supplier<Runnable> serverInit, Consumer<Platform.ModSetupContext> commonPostInit, Supplier<Consumer<Platform.ModSetupContext>> clientPostInit, Supplier<Consumer<Platform.ModSetupContext>> serverPostInit, Consumer<DataSetupContext> dataInit) {
            return Platform.error();
        }

        public Builder commonInit(Runnable onCommonInit) {
            this.commonInit = onCommonInit;
            return this;
        }

        /**
         * @deprecated Use {@link Platform.Builder#clientInit(Supplier)} for safe initialization.
         */
        @Deprecated
        public Builder clientInit(Runnable onClientInit) {
            return this.clientInit(() -> onClientInit);
        }

        public Builder clientInit(Supplier<Runnable> onClientInit) {
            this.clientInit = onClientInit;
            return this;
        }

        public Builder serverInit(Supplier<Runnable> onServerInit) {
            this.serverInit = onServerInit;
            return this;
        }

        public Builder commonPostInit(Consumer<ModSetupContext> onCommonPostInit) {
            this.commonPostInit = onCommonPostInit;
            return this;
        }

        /**
         * @deprecated Use {@link Platform.Builder#clientPostInit(Supplier)} for safe initialization. TODO remove in 2.0.0
         */
        @Deprecated
        public Builder clientPostInit(Consumer<ModSetupContext> onClientPostInit) {
            return this.clientPostInit(() -> onClientPostInit);
        }

        public Builder clientPostInit(Supplier<Consumer<ModSetupContext>> onClientPostInit) {
            this.clientPostInit = onClientPostInit;
            return this;
        }

        public Builder serverPostInit(Supplier<Consumer<ModSetupContext>> onServerPostInit) {
            this.serverPostInit = onServerPostInit;
            return this;
        }

        public Builder dataInit(Consumer<DataSetupContext> dataInit) {
            this.dataInit = dataInit;
            return this;
        }

        public Platform build() {
            return buildImpl(this.modId, this.commonInit, this.clientInit, this.serverInit, this.commonPostInit, this.clientPostInit, this.serverPostInit, this.dataInit);
        }
    }
}
