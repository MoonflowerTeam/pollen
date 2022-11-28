package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.Flipbook;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleParser;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.SingleQuadRenderProperties;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.util.GsonHelper;

/**
 * Component that specifies the billboard properties of a particle.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleAppearanceBillboardComponent implements CustomParticleComponent, CustomParticleRenderComponent {

    private final MolangExpression[] size;
    private final int textureWidth;
    private final int textureHeight;
    private final Flipbook flipbook;
    private final SingleQuadRenderProperties renderProperties;

    public ParticleAppearanceBillboardComponent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        this.size = JSONTupleParser.getExpression(jsonObject, "size", 2, null);

        // TODO direction

        if (jsonObject.has("uv")) {
            JsonObject uvJson = GsonHelper.getAsJsonObject(jsonObject, "uv");
            this.textureWidth = GsonHelper.getAsInt(uvJson, "texture_width", 1);
            this.textureHeight = GsonHelper.getAsInt(uvJson, "texture_height", 1);

            if (uvJson.has("flipbook")) {
                this.flipbook = ParticleParser.parseFlipbook(uvJson.get("flipbook"));
            } else {
                MolangExpression[] uv = JSONTupleParser.getExpression(uvJson, "uv", 2, null);
                MolangExpression[] uvSize = JSONTupleParser.getExpression(uvJson, "uv_size", 2, null);
                this.flipbook = new Flipbook(uv[0], uv[1], uvSize[0], uvSize[1], 0, 0, 1, MolangExpression.of(1), false, false);
            }
        } else {
            this.textureWidth = 1;
            this.textureHeight = 1;
            this.flipbook = new Flipbook(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.of(1), MolangExpression.of(1), 0, 0, 1, MolangExpression.of(1), false, false);
        }

        this.renderProperties = new SingleQuadRenderProperties();
    }

    @Override
    public void tick(CustomParticle particle) {
    }

    @Override
    public void render(CustomParticle particle) {
        MolangRuntime runtime = particle.getRuntime();
        this.renderProperties.setWidth(this.size[0].safeResolve(runtime));
        this.renderProperties.setHeight(this.size[1].safeResolve(runtime));
        this.renderProperties.setUV(runtime, this.textureWidth, this.textureHeight, this.flipbook, particle.getParticleAge(), particle.getParticleLifetime());
        particle.setRenderProperties(this.renderProperties);
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.PARTICLE_APPEARANCE_BILLBOARD.get();
    }
}
