package gg.moonflower.pollen.api.render.particle.v1;

import com.mojang.datafixers.util.Pair;
import gg.moonflower.pinwheel.api.particle.ParticleData;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariable;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariableProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Evaluates bedrock particle curves.
 *
 * @param variables The variables to use when updating
 * @author Ocelot
 * @since 2.0.0
 */
public record BedrockParticleCurves(Map<String, Pair<ParticleData.Curve, MolangVariable>> variables) implements MolangVariableProvider {

    public BedrockParticleCurves(ParticleData data) {
        this(data.curves().entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> {
            String[] parts = entry.getKey().split("\\.", 2);
            return parts.length > 1 ? parts[1] : parts[0];
        }, entry -> Pair.of(entry.getValue(), MolangVariable.create()))));
    }

    /**
     * Evaluates all curves.
     *
     * @param environment The environment to use
     * @param profiler    The profiler to record data into
     */
    public void evaluate(MolangEnvironment environment, ProfilerFiller profiler) {
        if (this.variables.isEmpty()) {
            return;
        }
        profiler.push("evaluateCurves");
        this.variables.forEach((variable, pair) -> pair.getSecond().setValue(evaluateCurve(environment, pair.getFirst())));
        profiler.pop();
    }

    @Override
    public void addMolangVariables(Context context) {
        this.variables.forEach((name, pair) -> context.addVariable(name, pair.getSecond()));
    }

    private static float evaluateCurve(MolangEnvironment runtime, ParticleData.Curve curve) {
        float horizontalRange = curve.horizontalRange().safeResolve(runtime);
        if (horizontalRange == 0) {
            return 1.0F;
        }
        float input = curve.input().safeResolve(runtime) / horizontalRange;

        ParticleData.CurveNode[] nodes = curve.nodes();
        int index = getIndex(curve, input);

        switch (curve.type()) {
            case LINEAR -> {
                ParticleData.CurveNode current = nodes[index];
                ParticleData.CurveNode next = index + 1 >= nodes.length ? current : nodes[index + 1];

                float a = current.getValue().safeResolve(runtime);
                float b = next.getValue().safeResolve(runtime);
                float progress = (input - current.getTime()) / (next.getTime() - current.getTime());

                return Mth.lerp(progress, a, b);
            }
            case BEZIER -> {
                float a = nodes[0].getValue().safeResolve(runtime);
                float b = nodes[1].getValue().safeResolve(runtime);
                float c = nodes[2].getValue().safeResolve(runtime);
                float d = nodes[3].getValue().safeResolve(runtime);

                return bezier(a, b, c, d, input);
            }
            case BEZIER_CHAIN -> {
                ParticleData.BezierChainCurveNode current = (ParticleData.BezierChainCurveNode) nodes[index];
                if (index + 1 >= nodes.length)
                    return current.getRightValue().safeResolve(runtime);

                ParticleData.BezierChainCurveNode next = (ParticleData.BezierChainCurveNode) nodes[index + 1];
                float step = input - current.getTime() + next.getTime() / 3F;
                float a = current.getRightValue().safeResolve(runtime);
                float b = a + step * current.getRightSlope().safeResolve(runtime);
                float d = next.getLeftValue().safeResolve(runtime);
                float c = d - step * next.getLeftSlope().safeResolve(runtime);
                float progress = (input - current.getTime()) / (next.getTime() - current.getTime());

                return bezier(a, b, c, d, progress);
            }
            case CATMULL_ROM -> {
                try {
                    ParticleData.CurveNode last = nodes[index - 1];
                    ParticleData.CurveNode from = nodes[index];
                    ParticleData.CurveNode to = nodes[index + 1];
                    ParticleData.CurveNode after = nodes[index + 2];

                    float a = last.getValue().safeResolve(runtime);
                    float b = from.getValue().safeResolve(runtime);
                    float c = to.getValue().safeResolve(runtime);
                    float d = after.getValue().safeResolve(runtime);
                    float nextTime = to.getTime();
                    float progress = (input - from.getTime()) / (nextTime - from.getTime());

                    return catmullRom(a, b, c, d, Mth.clamp(progress, 0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return input;
    }

    private static int getIndex(ParticleData.Curve curve, float input) {
        int best = 0;
        ParticleData.CurveNode[] nodes = curve.nodes();
        int offset = curve.type() == ParticleData.CurveType.CATMULL_ROM ? 1 : 0;
        for (int i = offset; i < nodes.length - offset * 2; i++) {
            ParticleData.CurveNode node = nodes[i];
            if (node.getTime() > input) {
                break;
            }

            best = i;
        }

        return best;
    }

    private static float bezier(float p0, float p1, float p2, float p3, float t) {
        return (1 - t) * (1 - t) * (1 - t) * p0 + 3 * (1 - t) * (1 - t) * t * p1 + 3 * (1 - t) * t * t * p2 + t * t * t * p3;
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        return 0.5F * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
    }
}
