package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererTicker;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import gg.moonflower.pollen.pinwheel.core.client.DataContainerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements BlockRendererTicker {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private final Map<BlockPos, BlockState> pendingUpdates = new HashMap<>();
    @Unique
    private final Map<BlockPos, BlockState> updates = new HashMap<>();
    @Unique
    private final DataContainerImpl dataContainer = new DataContainerImpl((ClientLevel) (Object) this);

    protected ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l, i);
    }

    @Override
    public void scheduleBlockRendererTick(BlockPos pos, BlockState state) {
        this.pendingUpdates.put(pos.immutable(), state);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.updates.clear();
        this.updates.putAll(this.pendingUpdates);
        this.pendingUpdates.clear();

        this.getProfiler().push(Pollen.MOD_ID + "TickBlockRenderers");
        ((LevelRendererExtension) this.minecraft.levelRenderer).pollen_getTickingBlockRenderers().forEach(pos -> {
            BlockState state = this.getBlockState(pos);
            for (BlockRenderer renderer : BlockRendererRegistry.get(state.getBlock())) {
                if (!(renderer instanceof TickableBlockRenderer))
                    continue;

                BlockState oldState = this.updates.remove(pos);
                if (oldState != null)
                    renderer.receiveUpdate(this, pos, oldState, state, this.dataContainer.get(pos));
                ((TickableBlockRenderer) renderer).tick(this, pos, this.dataContainer.get(pos));
            }
        });

        this.getProfiler().popPush(Pollen.MOD_ID + "UpdateBlockRenderers");
        this.updates.forEach((pos, state) -> {
            List<BlockRenderer> renderers = BlockRendererRegistry.get(state.getBlock());
            for (BlockRenderer renderer : renderers)
                renderer.receiveUpdate(this, pos, state, this.getBlockState(pos), this.dataContainer.get(pos));
        });
        this.getProfiler().pop();
    }
}
