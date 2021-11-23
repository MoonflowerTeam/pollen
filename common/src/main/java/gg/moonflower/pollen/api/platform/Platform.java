package gg.moonflower.pollen.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
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

    public static class Builder {

        private final String modId;

        private Runnable commonInit = () -> {
        };
        private Runnable clientInit = () -> {
        };
        private Consumer<ModSetupContext> commonPostInit = __ -> {
        };
        private Consumer<ModSetupContext> clientPostInit = __ -> {
        };

        private Builder(String modId) {
            this.modId = modId;
        }

        @ApiStatus.Internal
        @ExpectPlatform
        public static Platform buildImpl(String modId, Runnable commonInit, Runnable clientInit, Consumer<ModSetupContext> commonPostInit, Consumer<ModSetupContext> clientPostInit) {
            return Platform.error();
        }

        public Builder commonInit(Runnable onCommonInit) {
            this.commonInit = onCommonInit;
            return this;
        }

        public Builder clientInit(Runnable onClientInit) {
            this.clientInit = onClientInit;
            return this;
        }

        public Builder commonPostInit(Consumer<ModSetupContext> onCommonPostInit) {
            this.commonPostInit = onCommonPostInit;
            return this;
        }

        public Builder clientPostInit(Consumer<ModSetupContext> onClientPostInit) {
            this.clientPostInit = onClientPostInit;
            return this;
        }

        public Platform build() {
            return buildImpl(this.modId, this.commonInit, this.clientInit, this.commonPostInit, this.clientPostInit);
        }
    }
}
