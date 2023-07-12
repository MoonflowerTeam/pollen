package gg.moonflower.pollen.impl.render.particle.component;

import com.mojang.logging.LogUtils;
import gg.moonflower.pinwheel.api.particle.component.ParticleExpireNotInBlocksComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Objects;

@ApiStatus.Internal
public class ParticleExpireNotInBlocksComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleTickComponent {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Block[] blocks;

    public ParticleExpireNotInBlocksComponentImpl(BedrockParticle particle, ParticleExpireNotInBlocksComponent data) {
        super(particle);
        this.blocks = Arrays.stream(data.blocks()).map(name -> {
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (id == null) {
                LOGGER.error("Invalid block id: " + name);
                return null;
            }

            if (!Registry.BLOCK.containsKey(id)) {
                LOGGER.error("Unknown block: " + name);
                return null;
            }

            return Registry.BLOCK.get(id);
        }).filter(Objects::nonNull).toArray(Block[]::new);
    }

    @Override
    public void tick() {
        Level level = this.particle.getLevel();
        Block block = level.getBlockState(this.particle.blockPosition()).getBlock();
        for (Block test : this.blocks) {
            if (block != test) {
                this.particle.expire();
                break;
            }
        }
    }
}
