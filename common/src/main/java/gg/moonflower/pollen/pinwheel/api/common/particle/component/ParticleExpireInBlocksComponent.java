package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.*;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Component that specifies what blocks particles will immediately expire in.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleExpireInBlocksComponent implements CustomParticleComponent, CustomParticleTickComponent {

    private final Block[] blocks;

    public ParticleExpireInBlocksComponent(JsonElement json) throws JsonParseException {
        JsonArray jsonObject = json.getAsJsonArray();
        this.blocks = new Block[jsonObject.size()];
        try {
            for (int i = 0; i < jsonObject.size(); i++) {
                this.blocks[i] = Registry.BLOCK.get(new ResourceLocation(GsonHelper.convertToString(jsonObject.get(i), "minecraft:particle_expire_if_in_blocks[" + i + "]")));
            }
        } catch (ResourceLocationException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public void tick(CustomParticle particle) {
        Level level = particle.getLevel();
        Block block = level.getBlockState(particle.blockPos()).getBlock();
        for (Block test : this.blocks) {
            if (block == test) {
                particle.expire();
                break;
            }
        }
    }
}
