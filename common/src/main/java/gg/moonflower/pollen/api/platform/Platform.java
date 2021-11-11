package gg.moonflower.pollen.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
}
