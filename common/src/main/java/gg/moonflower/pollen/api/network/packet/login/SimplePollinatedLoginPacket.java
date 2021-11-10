package gg.moonflower.pollen.api.network.packet.login;

/**
 * An implementation of {@link PollinatedLoginPacket} that handles login specific data.
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class SimplePollinatedLoginPacket<T> implements PollinatedLoginPacket<T> {

    private int loginIndex;

    @Override
    public int getAsInt() {
        return loginIndex;
    }

    @Override
    public void setLoginIndex(int index) {
        this.loginIndex = index;
    }
}
