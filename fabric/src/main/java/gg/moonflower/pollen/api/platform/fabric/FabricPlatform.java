package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class FabricPlatform extends Platform {

    private final Runnable commonInit;
    private final Runnable clientInit;
    private final Consumer<Platform.ModSetupContext> commonPostInit;
    private final Consumer<Platform.ModSetupContext> clientPostInit;

    FabricPlatform(String modId, Runnable commonInit, Runnable clientInit, Consumer<Platform.ModSetupContext> commonPostInit, Consumer<ModSetupContext> clientPostInit) {
        super(modId);
        this.commonInit = commonInit;
        this.clientInit = clientInit;
        this.commonPostInit = commonPostInit;
        this.clientPostInit = clientPostInit;
    }

    @Override
    public void setup() {
        ModSetupContext context = new SetupContext();

        this.commonInit.run();
        this.commonPostInit.accept(context);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.clientInit.run();
            this.clientPostInit.accept(context);
        }
    }

    private static class SetupContext implements ModSetupContext {

        @Override
        public CompletableFuture<Void> enqueueWork(Runnable work) {
            work.run();
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
            return CompletableFuture.completedFuture(work.get());
        }
    }
}
