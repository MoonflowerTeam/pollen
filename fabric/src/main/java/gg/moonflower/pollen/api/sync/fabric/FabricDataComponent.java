package gg.moonflower.pollen.api.sync.fabric;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.CopyableComponent;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import gg.moonflower.pollen.api.sync.DataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Internal
public class FabricDataComponent extends DataComponent implements Component, CopyableComponent<FabricDataComponent>, AutoSyncedComponent {

    private final Entity provider;

    public FabricDataComponent(Entity provider) {
        this.provider = provider;
    }

    @Override
    public void writeToNbt(CompoundTag nbt) {
        this.writeToNbt(nbt, NbtWriteMode.SAVE);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return this.shouldSyncWith(this.provider, player);
    }

    @Override
    public void copyFrom(FabricDataComponent other) {
        CompoundTag tag = new CompoundTag();
        other.writeToNbt(tag, NbtWriteMode.COPY);
        this.readFromNbt(tag);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        this.writePacketData(buf, this.dirtyValues.stream().mapToInt(i -> i).toArray());
        this.dirtyValues.clear();
    }
}
