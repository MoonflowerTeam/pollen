package gg.moonflower.pollen.core.client.profile;

import java.util.UUID;

public class ProfileData {

    private final UUID uuid;
    private final int patreon;
    private final int tier;

    public ProfileData(UUID uuid, int patreon, int tier) {
        this.uuid = uuid;
        this.patreon = patreon;
        this.tier = tier;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPatreon() {
        return patreon;
    }

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
