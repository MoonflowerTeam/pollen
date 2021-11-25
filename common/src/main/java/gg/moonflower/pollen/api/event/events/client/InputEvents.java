package gg.moonflower.pollen.api.event.events.client;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;

/**
 * Basic events for passing GLFW inputs to the game.
 */
public final class InputEvents {

    public static final PollinatedEvent<MouseInputEvent> MOUSE_INPUT_EVENT = EventRegistry.create(MouseInputEvent.class, events -> (mouseHandler, button, action, modifiers) -> {
        for (MouseInputEvent event : events)
            if (event.mouseInput(mouseHandler, button, action, modifiers))
                return true;
        return false;
    });
    public static final PollinatedEvent<MouseScrolledEvent> MOUSE_SCROLL_EVENT = EventRegistry.create(MouseScrolledEvent.class, events -> (mouseHelper, xOffset, yOffset) -> {
        for (MouseScrolledEvent event : events)
            if (event.mouseScrolled(mouseHelper, xOffset, yOffset))
                return true;
        return false;
    });
    public static final PollinatedEvent<MouseScrolledEvent> GUI_MOUSE_SCROLL_EVENT_PRE = EventRegistry.create(MouseScrolledEvent.class, events -> (mouseHelper, xOffset, yOffset) -> {
        for (MouseScrolledEvent event : events)
            if (event.mouseScrolled(mouseHelper, xOffset, yOffset))
                return true;
        return false;
    });
    public static final PollinatedEvent<MouseScrolledEvent> GUI_MOUSE_SCROLL_EVENT_POST = EventRegistry.create(MouseScrolledEvent.class, events -> (mouseHelper, xOffset, yOffset) -> {
        for (MouseScrolledEvent event : events)
            if (event.mouseScrolled(mouseHelper, xOffset, yOffset))
                return true;
        return false;
    });
    public static final PollinatedEvent<KeyInputEvent> KEY_INPUT_EVENT = EventRegistry.create(KeyInputEvent.class, events -> (key, scanCode, action, modifiers) -> {
        for (KeyInputEvent event : events)
            if (event.keyInput(key, scanCode, action, modifiers))
                return true;
        return false;
    });

    private InputEvents() {
    }

    /**
     * Called when a mouse input is detected.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface MouseInputEvent {

        /**
         * @param button    The <a href=https://www.glfw.org/docs/latest/group__keys.html>GLFW</a> button code
         * @param action    The action taken. One of {@link GLFW#GLFW_PRESS} or {@link GLFW#GLFW_RELEASE}
         * @param modifiers The bit-field containing all modifers to the pressed key. One of {@link GLFW#GLFW_MOD_SHIFT}, {@link GLFW#GLFW_MOD_CONTROL}, {@link GLFW#GLFW_MOD_ALT}, or {@link GLFW#GLFW_MOD_SUPER}
         * @return Whether the event was consumed
         */
        boolean mouseInput(MouseHandler mouseHandler, int button, int action, int modifiers);
    }

    /**
     * Called each time the mouse is scrolled.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface MouseScrolledEvent {

        /**
         * Handles the mouse being scrolled.
         *
         * @param mouseHandler The handler for mouse input
         * @param xOffset      The delta X movement in the wheel
         * @param yOffset      The delta Y movement in the wheel
         * @return Whether the event was consumed
         */
        boolean mouseScrolled(MouseHandler mouseHandler, double xOffset, double yOffset);
    }

    /**
     * Called when a keyboard input is detected.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface KeyInputEvent {

        /**
         * @param key       The <a href=https://www.glfw.org/docs/latest/group__keys.html>GLFW</a> key code
         * @param scanCode  The platform-specific scan code
         * @param action    The action taken. One of {@link GLFW#GLFW_PRESS}, {@link GLFW#GLFW_RELEASE}, or {@link GLFW#GLFW_REPEAT}
         * @param modifiers The bit-field containing all modifers to the pressed key. One of {@link GLFW#GLFW_MOD_SHIFT}, {@link GLFW#GLFW_MOD_CONTROL}, {@link GLFW#GLFW_MOD_ALT}, or {@link GLFW#GLFW_MOD_SUPER}
         * @return Whether the event was consumed
         */
        boolean keyInput(int key, int scanCode, int action, int modifiers);
    }
}
