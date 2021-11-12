package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ForgePlatform extends Platform {

    private final IEventBus eventBus;
    private final Runnable commonInit;
    private final Runnable clientInit;
    private final Consumer<Platform.ModSetupContext> commonPostInit;
    private final Consumer<Platform.ModSetupContext> clientPostInit;

    ForgePlatform(String modId, IEventBus eventBus, Runnable commonInit, Runnable clientInit, Consumer<ModSetupContext> commonPostInit, Consumer<Platform.ModSetupContext> clientPostInit) {
        super(modId);
        this.eventBus = eventBus;
        this.commonInit = commonInit;
        this.clientInit = clientInit;
        this.commonPostInit = commonPostInit;
        this.clientPostInit = clientPostInit;
    }

    @Override
    public void setup() {
        this.eventBus.<FMLCommonSetupEvent>addListener(e -> this.commonPostInit.accept(new SetupContext(e)));
        this.eventBus.<FMLClientSetupEvent>addListener(e -> this.clientPostInit.accept(new SetupContext(e)));

        this.commonInit.run();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> this.clientInit::run);
    }

    public IEventBus getEventBus() {
        return eventBus;
    }

    private static class SetupContext implements ModSetupContext {

        private final ParallelDispatchEvent event;

        private SetupContext(ParallelDispatchEvent event) {
            this.event = event;
        }

        @Override
        public CompletableFuture<Void> enqueueWork(Runnable work) {
            return this.event.enqueueWork(work);
        }

        @Override
        public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
            return this.event.enqueueWork(work);
        }
    }
}
