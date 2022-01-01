package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.PollinatedModContainerImpl;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
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
    private final Consumer<Platform.DataSetupContext> dataInit;

    ForgePlatform(String modId, IEventBus eventBus, Runnable commonInit, Runnable clientInit, Consumer<ModSetupContext> commonPostInit, Consumer<Platform.ModSetupContext> clientPostInit, Consumer<Platform.DataSetupContext> dataInit) {
        super(modId);
        this.eventBus = eventBus;
        this.commonInit = commonInit;
        this.clientInit = clientInit;
        this.commonPostInit = commonPostInit;
        this.clientPostInit = clientPostInit;
        this.dataInit = dataInit;
    }

    @Override
    public void setup() {
        this.eventBus.<FMLCommonSetupEvent>addListener(e -> this.commonPostInit.accept(new ModSetupContextImpl(e)));
        this.eventBus.<FMLClientSetupEvent>addListener(e -> this.clientPostInit.accept(new ModSetupContextImpl(e)));
        this.eventBus.<GatherDataEvent>addListener(e -> this.dataInit.accept(new DataSetupContextImpl(e.getGenerator(), new PollinatedModContainerImpl(e.getModContainer()))));

        this.commonInit.run();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> this.clientInit::run);
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
