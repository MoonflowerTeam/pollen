package gg.moonflower.pollen.api.levelgen.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.levelgen.biome.parameters.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

import java.util.*;

/**
 * A utility class for management of climate parameters.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
 * @since 1.5.0
 */
public final class ParameterPoints {

    private static final List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> VANILLA_POINTS;
    private static final Map<ResourceKey<Biome>, List<Climate.ParameterPoint>> PARAMETER_POINT_CACHE = new HashMap<>();

    private ParameterPoints() {
    }

    /**
     * @return A builder to create a list of {@link Climate.ParameterPoint}s
     */
    public static ListBuilder listBuilder() {
        return new ListBuilder();
    }

    /**
     * Gets {@link Climate.ParameterPoint}s for the given vanilla biome.
     *
     * @param biome The biome to get parameter points for
     * @return A list of parameter points corresponding to the given biome
     */
    public static List<Climate.ParameterPoint> getVanilla(ResourceKey<Biome> biome) {
        if (PARAMETER_POINT_CACHE.containsKey(biome))
            return PARAMETER_POINT_CACHE.get(biome);
        List<Climate.ParameterPoint> points = VANILLA_POINTS.stream().filter(pair -> pair.getSecond() == biome).map(Pair::getFirst).collect(ImmutableList.toImmutableList());
        PARAMETER_POINT_CACHE.put(biome, points);
        return points;
    }

    /**
     * Builds a list of {@link Climate.ParameterPoint}s with the given parameters.
     *
     * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
     * @since 1.5.0
     */
    public static class ListBuilder {

        private final List<Climate.Parameter> temperatureValues = new ArrayList<>();
        private final List<Climate.Parameter> humidityValues = new ArrayList<>();
        private final List<Climate.Parameter> continentalnessValues = new ArrayList<>();
        private final List<Climate.Parameter> erosionValues = new ArrayList<>();
        private final List<Climate.Parameter> depthValues = new ArrayList<>();
        private final List<Climate.Parameter> weirdnessValues = new ArrayList<>();
        private final List<Long> offsetValues = new ArrayList<>();

        private ListBuilder() {
        }

        /**
         * Adds temperature parameters to the list.
         * @param values The temperature values to be added
         */
        public ListBuilder temperature(Climate.Parameter... values) {
            this.temperatureValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Adds temperature values to the list.
         * @param values The temperature values to be added
         */
        public ListBuilder temperature(Temperature... values) {
            this.temperatureValues.addAll(Arrays.stream(values).map(Temperature::parameter).toList());
            return this;
        }

        /**
         * Adds humidity parameters to the list.
         * @param values The humidity values to be added
         */
        public ListBuilder humidity(Climate.Parameter... values) {
            this.humidityValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Adds humidity values to the list.
         * @param values The humidity values to be added
         */
        public ListBuilder humidity(Humidity... values) {
            this.humidityValues.addAll(Arrays.stream(values).map(Humidity::parameter).toList());
            return this;
        }

        /**
         * Adds continentalness parameters to the list.
         * @param values The continentalness values to be added
         */
        public ListBuilder continentalness(Climate.Parameter... values) {
            this.continentalnessValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Adds continentalness values to the list.
         * @param values The continentalness values to be added
         */
        public ListBuilder continentalness(Continentalness... values) {
            this.continentalnessValues.addAll(Arrays.stream(values).map(Continentalness::parameter).toList());
            return this;
        }

        /**
         * Adds erosion parameters to the list.
         * @param values The erosion values to be added
         */
        public ListBuilder erosion(Climate.Parameter... values) {
            this.erosionValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Adds erosion values to the list.
         * @param values The erosion values to be added
         */
        public ListBuilder erosion(Erosion... values) {
            this.erosionValues.addAll(Arrays.stream(values).map(Erosion::parameter).toList());
            return this;
        }

        /**
         * Adds depth parameters to the list.
         * @param values The depth values to be added
         */
        public ListBuilder depth(Climate.Parameter... values) {
            this.depthValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Adds depth values to the list.
         * @param values The depth values to be added
         */
        public ListBuilder depth(Depth... values) {
            this.depthValues.addAll(Arrays.stream(values).map(Depth::parameter).toList());
            return this;
        }

        /**
         * Adds weirdness parameters to the list.
         * @param values The weirdness values to be added
         */
        public ListBuilder weirdness(Climate.Parameter... values) {
            this.weirdnessValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Adds weirdness values to the list.
         * @param values The weirdness values to be added
         */
        public ListBuilder weirdness(Weirdness... values) {
            this.weirdnessValues.addAll(Arrays.stream(values).map(Weirdness::parameter).toList());
            return this;
        }

        /**
         * Adds offset values to the list.
         * @param values The offset values to be added
         */
        public ListBuilder offset(Float... values) {
            this.offsetValues.addAll(Arrays.stream(values).map(Climate::quantizeCoord).toList());
            return this;
        }

        /**
         * Adds offset values to the list.
         * @param values The offest values to be added
         */
        public ListBuilder offset(Long... values) {
            this.offsetValues.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Builds a list of {@link Climate.ParameterPoint}s based on the given parameters.
         * @return The built list
         */
        public List<Climate.ParameterPoint> build() {
            this.populateIfEmpty();
            ImmutableList.Builder<Climate.ParameterPoint> builder = new ImmutableList.Builder<>();
            this.temperatureValues.forEach(temperature -> this.humidityValues.forEach(humidity -> this.continentalnessValues.forEach(continentalness -> this.erosionValues.forEach(erosion -> this.depthValues.forEach(depth -> this.weirdnessValues.forEach(weirdness -> this.offsetValues.forEach(offset -> builder.add(new Climate.ParameterPoint(temperature, humidity, continentalness, erosion, depth, weirdness, offset)))))))));
            return builder.build();
        }

        private void populateIfEmpty() {
            if (this.temperatureValues.isEmpty()) this.temperatureValues.add(Temperature.FULL_RANGE.parameter());
            if (this.humidityValues.isEmpty()) this.humidityValues.add(Humidity.FULL_RANGE.parameter());
            if (this.continentalnessValues.isEmpty()) this.continentalnessValues.add(Continentalness.FULL_RANGE.parameter());
            if (this.erosionValues.isEmpty()) this.erosionValues.add(Erosion.FULL_RANGE.parameter());
            if (this.depthValues.isEmpty()) this.depthValues.add(Depth.FULL_RANGE.parameter());
            if (this.weirdnessValues.isEmpty()) this.weirdnessValues.add(Weirdness.FULL_RANGE.parameter());
            if (this.offsetValues.isEmpty()) this.offsetValues.add(Climate.quantizeCoord(0.0F));
        }
    }

    static {
        ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = new ImmutableList.Builder<>();
        (new OverworldBiomeBuilder()).addBiomes(builder::add);
        VANILLA_POINTS = builder.build();
    }
}
