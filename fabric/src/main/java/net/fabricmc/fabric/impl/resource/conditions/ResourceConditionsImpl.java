/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.resource.conditions;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * <p>
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * </p>
 *
 * <p>Copied from <a href=https://github.com/FabricMC/fabric/blob/1.18/fabric-resource-conditions-api-v1/src/main/java/net/fabricmc/fabric/impl/resource/conditions/ResourceConditionsImpl.java>Fabric 1.18</a>
 */
@ApiStatus.Internal
public class ResourceConditionsImpl {

    public static final Logger LOGGER = LogManager.getLogger("Fabric Resource Conditions");

    // Providers

    public static ConditionJsonProvider array(ResourceLocation id, ConditionJsonProvider... values) {
        Preconditions.checkArgument(values.length > 0, "Must register at least one value.");

        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();

                for (ConditionJsonProvider provider : values) {
                    array.add(provider.toJson());
                }

                object.add("values", array);
            }
        };
    }

    public static ConditionJsonProvider mods(ResourceLocation id, String... modIds) {
        Preconditions.checkArgument(modIds.length > 0, "Must register at least one mod id.");

        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();

                for (String modId : modIds) {
                    array.add(modId);
                }

                object.add("values", array);
            }
        };
    }

    public static <T> ConditionJsonProvider tagsPopulated(ResourceLocation id, Tag.Named<T>... tags) {
        Preconditions.checkArgument(tags.length > 0, "Must register at least one tag.");

        return new ConditionJsonProvider() {
            @Override
            public ResourceLocation getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();

                for (Tag.Named<T> tag : tags) {
                    array.add(tag.getName().toString());
                }

                object.add("values", array);
            }
        };
    }

    // Condition implementations

    public static boolean modsLoadedMatch(JsonObject object, boolean and) {
        JsonArray array = GsonHelper.getAsJsonArray(object, "values");

        for (JsonElement element : array) {
            if (element.isJsonPrimitive()) {
                if (FabricLoader.getInstance().isModLoaded(element.getAsString()) != and) {
                    return !and;
                }
            } else {
                throw new JsonParseException("Invalid mod id entry: " + element);
            }
        }

        return and;
    }

    public static <T> boolean tagsPopulatedMatch(JsonObject object, Function<TagContainer, TagCollection<T>> tags) {
        JsonArray array = GsonHelper.getAsJsonArray(object, "values");

        for (JsonElement element : array) {
            if (element.isJsonPrimitive()) {
                ResourceLocation id = new ResourceLocation(element.getAsString());
                Tag<T> tag = tags.apply(SerializationTags.getInstance()).getTagOrEmpty(id);

                if (tag.getValues().isEmpty()) {
                    return false;
                }
            } else {
                throw new JsonParseException("Invalid tag id entry: " + element);
            }
        }

        return true;
    }
}
