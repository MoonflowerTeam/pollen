package gg.moonflower.pollen.api.config;

/**
 * Wrapper for Forge config types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public enum PollinatedConfigType {

    /**
     * Common mod config for configuration that needs to be loaded on both environments.
     * <p>Loaded on both servers and clients.
     * <p>Stored in the global config directory.
     * <p>Not synced.
     * <p>Suffix is "-common".
     */
    COMMON,
    /**
     * Client config is for configuration affecting the ONLY client state such as graphical options.
     * <p>Only loaded on the client side.
     * <p>Stored in the global config directory.
     * <p>Not synced.
     * <p>Suffix is "-client".
     */
    CLIENT,
    /**
     * Server type config is configuration that is associated with a server instance.
     * <p>Only loaded during server startup.
     * <p>Stored in a server/save specific "serverconfig" directory.
     * <p>Synced to clients during connection.
     * <p>Suffix is "-server".
     */
    SERVER
}
