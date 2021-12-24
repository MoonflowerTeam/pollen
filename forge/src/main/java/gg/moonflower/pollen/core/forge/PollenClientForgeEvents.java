package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.client.InputEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import gg.moonflower.pollen.api.event.events.registry.client.ParticleFactoryRegistryEvent;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.extensions.MouseHandlerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT)
public class PollenClientForgeEvents {

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvents.CLIENT_PRE.invoker().tick();
                break;
            case END:
                TickEvents.CLIENT_POST.invoker().tick();
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientNetworkEvents.LOGIN.invoker().login(event.getController(), event.getPlayer(), event.getNetworkManager());
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientNetworkEvents.LOGOUT.invoker().logout(event.getController(), event.getPlayer(), event.getNetworkManager());
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.RespawnEvent event) {
        ClientNetworkEvents.RESPAWN.invoker().respawn(event.getController(), event.getOldPlayer(), event.getPlayer(), event.getNetworkManager());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.client.event.GuiScreenEvent.MouseScrollEvent.Pre event) {
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        if (InputEvents.GUI_MOUSE_SCROLL_EVENT_PRE.invoker().mouseScrolled(mouseHandler, ((MouseHandlerExtension) mouseHandler).getXOffset(), event.getScrollDelta()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.client.event.GuiScreenEvent.MouseScrollEvent.Post event) {
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        InputEvents.GUI_MOUSE_SCROLL_EVENT_POST.invoker().mouseScrolled(mouseHandler, ((MouseHandlerExtension) mouseHandler).getXOffset(), event.getScrollDelta());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.client.event.InputEvent.MouseScrollEvent event) {
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        if (InputEvents.MOUSE_SCROLL_EVENT.invoker().mouseScrolled(mouseHandler, ((MouseHandlerExtension) mouseHandler).getXOffset(), event.getScrollDelta()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(InputEvent.MouseInputEvent event) {
        InputEvents.MOUSE_INPUT_EVENT.invoker().mouseInput(Minecraft.getInstance().mouseHandler, event.getButton(), event.getAction(), event.getMods());
    }

    @SubscribeEvent
    public static void onEvent(InputEvent.KeyInputEvent event) {
        InputEvents.KEY_INPUT_EVENT.invoker().keyInput(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onEvent(ParticleFactoryRegisterEvent event) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        ParticleFactoryRegistryEvent.EVENT.invoker().registerParticles(new ParticleFactoryRegistryEvent.Registry() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
                particleEngine.register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleFactoryRegistryEvent.Factory<T> factory) {
                particleEngine.register(type, factory::create);
            }
        });
    }
}
