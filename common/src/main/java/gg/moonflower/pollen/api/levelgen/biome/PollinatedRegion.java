package gg.moonflower.pollen.api.levelgen.biome;

/**
 * A source of modded biome parameters and other biome-related data. Serves as a wrapper for TerraBlender's region class.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/Region.java">Region</a>
 * @since 1.5.0
 */
public interface PollinatedRegion {

    /**
     * @return The id of the mod the region is for
     */
    String getModId();

    /**
     * @return The type of the region
     */
    Type getType();

    /**
     * @return The weight of the region, which determines how frequently it will spawn
     */
    int getWeight();

    /**
     * Adds biomes using the provided generator. Modded regions should override this method to apply their changes.
     *
     * @param generator The generator for adding biome generation
     */
    void addBiomes(PollinatedBiomeGenerator generator);

    /**
     * A type of region that determines what dimension a region should spawn in.
     *
     * @since 1.5.0
     */
    enum Type {

        /**
         * Denotes that a region should spawn in the overworld.
         */
        OVERWORLD("overworld"),

        /**
         * Denotes that a region should spawn in the nether.
         */
        NETHER("nether");

        private final String path;

        Type(String path) {
            this.path = path;
        }

        /**
         * @return The string path the parent region will register as
         */
        public String getPath() {
            return this.path;
        }
    }
}
