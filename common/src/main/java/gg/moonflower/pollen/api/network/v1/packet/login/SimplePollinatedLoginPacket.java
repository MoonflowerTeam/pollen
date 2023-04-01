package gg.moonflower.pollen.api.network.v1.packet.login;

/**
 * An implementation of {@link PollinatedLoginPacket} that handles login specific data.
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 2.0.0
 */
public abstract class SimplePollinatedLoginPacket<T> implements PollinatedLoginPacket<T> {

    private int loginIndex;

    @Override
    public int getAsInt() {
        return this.loginIndex;
    }

    @Override
    public void setLoginIndex(int index) {
        this.loginIndex = index;
    }
}
