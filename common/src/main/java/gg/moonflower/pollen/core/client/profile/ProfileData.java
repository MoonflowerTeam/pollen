package gg.moonflower.pollen.core.client.profile;

import net.minecraft.Util;

import java.util.UUID;

/**
 * Information on a single profile.
 *
 * @author Ocelot
 */
public class ProfileData {

    public static final ProfileData EMPTY = new ProfileData(Util.NIL_UUID, 0);

    private final UUID uuid;
    private final int patreon;

    public ProfileData(UUID uuid, int patreon) {
        this.uuid = uuid;
        this.patreon = patreon;
    }

    /**
     * @return The id of the profile
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return The Patreon account id
     */
    public int getPatreon() {
        return patreon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileData that = (ProfileData) o;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public String toString() {
        return "ProfileData{" +
                "uuid=" + uuid +
                ", patreon=" + patreon +
                '}';
    }
}
