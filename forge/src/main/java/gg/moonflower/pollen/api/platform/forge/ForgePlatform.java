package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.PollinatedModContainerImpl;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ForgePlatform extends Platform {

    private final IEventBus eventBus;
    private final Runnable commonInit;
    private final Supplier<Runnable> clientInit;
    private final Supplier<Runnable> serverInit;
    private final Consumer<Platform.ModSetupContext> commonPostInit;
    private final Supplier<Consumer<Platform.ModSetupContext>> clientPostInit;
    private final Supplier<Consumer<Platform.ModSetupContext>> serverPostInit;
    private final Consumer<DataSetupContext> dataInit;

    ForgePlatform(String modId, IEventBus eventBus, Runnable commonInit, Supplier<Runnable> clientInit, Supplier<Runnable> serverInit, Consumer<Platform.ModSetupContext> commonPostInit, Supplier<Consumer<Platform.ModSetupContext>> clientPostInit, Supplier<Consumer<Platform.ModSetupContext>> serverPostInit, Consumer<DataSetupContext> dataInit) {
        super(modId);
        this.eventBus = eventBus;
        this.commonInit = commonInit;
        this.clientInit = clientInit;
        this.serverInit = serverInit;
        this.commonPostInit = commonPostInit;
        this.clientPostInit = clientPostInit;
        this.serverPostInit = serverPostInit;
        this.dataInit = dataInit;
    }

    @Override
    public void setup() {
        this.eventBus.<FMLCommonSetupEvent>addListener(e -> this.commonPostInit.accept(new ModSetupContextImpl(e)));
        this.eventBus.<FMLClientSetupEvent>addListener(e -> this.clientPostInit.get().accept(new ModSetupContextImpl(e)));
        this.eventBus.<FMLDedicatedServerSetupEvent>addListener(e -> this.serverPostInit.get().accept(new ModSetupContextImpl(e)));
        this.eventBus.<GatherDataEvent>addListener(e -> this.dataInit.accept(new DataSetupContextImpl(e.getGenerator(), new PollinatedModContainerImpl(e.getModContainer()))));

        this.commonInit.run();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.clientInit.get().run());
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> this.serverInit.get().run());
    }

    public IEventBus getEventBus() {
        return eventBus;
    }

    private static class ModSetupContextImpl implements ModSetupContext {

        private final ParallelDispatchEvent event;

        private ModSetupContextImpl(ParallelDispatchEvent event) {
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

    private static class DataSetupContextImpl implements DataSetupContext {

        private final DataGenerator dataGenerator;
        private final PollinatedModContainer modContainer;

        private DataSetupContextImpl(DataGenerator dataGenerator, PollinatedModContainer modContainer) {
            this.dataGenerator = dataGenerator;
            this.modContainer = modContainer;
        }

        @Override
        public DataGenerator getGenerator() {
            return dataGenerator;
        }

        @Override
        public PollinatedModContainer getMod() {
            return modContainer;
        }
    }
}
