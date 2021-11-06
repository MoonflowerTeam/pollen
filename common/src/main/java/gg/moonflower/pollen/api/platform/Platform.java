package gg.moonflower.pollen.api.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.ApiStatus;

/**
 * A mod instance for initializing mods on the common side.
 *
 * @author Jackson
 * @since 1.0.0
 */
public abstract class Platform {

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
    public void setup() {
    }

    public static class Builder {

        private static final Runnable EMPTY_RUNNABLE = () -> {
        };

        private final String modId;

        private Runnable commonInit = Builder.EMPTY_RUNNABLE;
        private Runnable clientInit = Builder.EMPTY_RUNNABLE;
        private Runnable commonPostInit = Builder.EMPTY_RUNNABLE;
        private Runnable clientPostInit = Builder.EMPTY_RUNNABLE;
        private Runnable commonNetworkInit = Builder.EMPTY_RUNNABLE;
        private Runnable clientNetworkInit = Builder.EMPTY_RUNNABLE;

        private Builder(String modId) {
            this.modId = modId;
        }

        @ApiStatus.Internal
        @ExpectPlatform
        public static Platform buildImpl(String modId, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit, Runnable commonNetworkInit, Runnable clientNetworkInit) {
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

        public Builder commonPostInit(Runnable onCommonPostInit) {
            this.commonPostInit = onCommonPostInit;
            return this;
        }

        public Builder clientPostInit(Runnable onClientPostInit) {
            this.clientPostInit = onClientPostInit;
            return this;
        }

        public Builder commonNetworkInit(Runnable onCommonNetworkInit) {
            this.commonNetworkInit = onCommonNetworkInit;
            return this;
        }

        public Builder clientNetworkInit(Runnable onClientNetworkInit) {
            this.clientNetworkInit = onClientNetworkInit;
            return this;
        }

        public Platform build() {
            return buildImpl(this.modId, this.commonInit, this.clientInit, this.commonPostInit, this.clientPostInit, this.commonNetworkInit, this.clientNetworkInit);
        }
    }
}
