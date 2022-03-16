package gg.moonflower.pollen.pinwheel.core.client.util;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class DynamicReloader {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<PreparableReloadListener> reloadListeners;
    private ReloadInstance asyncReloader;

    public DynamicReloader() {
        this.reloadListeners = new ArrayList<>();
    }

    public void addListener(PreparableReloadListener listener) {
        this.reloadListeners.add(listener);
    }

    public CompletableFuture<?> reload(boolean showLoadingScreen) {
        if (asyncReloader != null)
            return asyncReloader.done();
        asyncReloader = SimpleReloadInstance.of(Minecraft.getInstance().getResourceManager(), this.reloadListeners, Util.backgroundExecutor(), Minecraft.getInstance(), CompletableFuture.completedFuture(Unit.INSTANCE));
        if (showLoadingScreen)
            Minecraft.getInstance().setOverlay(new LoadingOverlay(Minecraft.getInstance(), asyncReloader, error ->
            {
                asyncReloader = null;
                error.ifPresent(LOGGER::error);
            }, true));
        return this.asyncReloader.done().handle((unit, e) ->
        {
            if (e != null)
                LOGGER.error("Error reloading", e);
            this.asyncReloader = null;
            return unit;
        });
    }

    public boolean isReloading() {
        return asyncReloader != null;
    }
}
