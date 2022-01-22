package gg.moonflower.pollen.core.client.profile;

import java.util.UUID;

/**
 * Information on a single profile.
 *
 * @author Ocelot
 */
public class ProfileData {

    private final UUID uuid;
    private final int patreon;
    private final int tier;

    public ProfileData(UUID uuid, int patreon, int tier) {
        this.uuid = uuid;
        this.patreon = patreon;
        this.tier = tier;
    }

    /**
     * @return The id of the profile
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return The Patreon level of the profile
     */
    public int getPatreon() {
        return patreon;
    }

    /**
     * @return The Patreon tier of the profile
     */
    public int getTier() {
        return tier;
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
                ", tier=" + tier +
                '}';
    }
}
