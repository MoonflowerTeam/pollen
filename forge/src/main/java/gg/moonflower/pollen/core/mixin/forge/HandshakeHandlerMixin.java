package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.core.extension.forge.FMLHandshakeHandlerExtensions;
import net.minecraftforge.network.HandshakeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(HandshakeHandler.class)
public class HandshakeHandlerMixin implements FMLHandshakeHandlerExtensions {

    @Unique
    private final Collection<Future<?>> waits = new ConcurrentLinkedQueue<>();

    @Inject(method = "tickServer", at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;attr(Lio/netty/util/AttributeKey;)Lio/netty/util/Attribute;", shift = At.Shift.BEFORE), cancellable = true, remap = false)
    public void tickServer(CallbackInfoReturnable<Boolean> cir) {

        AtomicReference<Throwable> error = new AtomicReference<>();
        this.waits.removeIf(future -> {
            if (!future.isDone())
                return false;

            try {
                future.get();
            } catch (ExecutionException e) {
                Throwable caught = e.getCause();
                error.getAndUpdate(oldEx -> {
                    if (oldEx == null)
                        return caught;

                    oldEx.addSuppressed(caught);
                    return oldEx;
                });
            } catch (InterruptedException | CancellationException ignored) {
            }

            return true;
        });

        if (!this.waits.isEmpty())
            cir.cancel();
    }

    @Override
    public void pollen_addWait(Future<?> wait) {
        this.waits.add(wait);
    }
}
