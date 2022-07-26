package gg.moonflower.pollen.api.sync.forge;

import gg.moonflower.pollen.api.sync.DataComponent;
import gg.moonflower.pollen.api.sync.SyncedDataKey;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.forge.ClientboundUpdateSyncedDataPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public class SyncedDataManagerImpl {

    public static final Capability<ForgeDataComponent> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private static LazyOptional<ForgeDataComponent> getDataComponent(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    @SubscribeEvent
    public static void onEvent(RegisterCapabilitiesEvent event) {
        event.register(ForgeDataComponent.class);
    }

    @SubscribeEvent
    public static void onEvent(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation(Pollen.MOD_ID, "synced_data"), new Provider());
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            Entity target = event.getTarget();
            getDataComponent(player).ifPresent(component -> {
                if (component.shouldSyncWith(target, player))
                    PollenMessages.PLAY.sendTo(player, new ClientboundUpdateSyncedDataPacket(target, player, true));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            getDataComponent(player).ifPresent(component -> {
                if (component.shouldSyncWith(player, player))
                    PollenMessages.PLAY.sendTo(player, new ClientboundUpdateSyncedDataPacket(player, player, true));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer && event.getEntity() instanceof ServerPlayer) {
            ServerPlayer original = (ServerPlayer) event.getOriginal();
            ServerPlayer player = (ServerPlayer) event.getEntity();

            Optional<ForgeDataComponent> originalOptional = getDataComponent(original).resolve();
            Optional<ForgeDataComponent> copyOptional = getDataComponent(original).resolve();

            if (!originalOptional.isPresent() || !copyOptional.isPresent())
                return;

            ForgeDataComponent oldHolder = originalOptional.get();
            ForgeDataComponent newHolder = copyOptional.get();
            if (oldHolder.shouldCopyForRespawn(!event.isWasDeath(), player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)))
                newHolder.copyForRespawn(oldHolder, !event.isWasDeath());

            if (newHolder.shouldSyncWith(player, player))
                PollenMessages.PLAY.sendTo(player, new ClientboundUpdateSyncedDataPacket(player, player, true));
        }
    }

    public static void sync(Entity entity) {
        getDataComponent(entity).ifPresent(component -> {
            if (component.isDirty()) {
                for (ServerPlayer other : ((ServerLevel) entity.level).getServer().getPlayerList().getPlayers()) {
                    if (component.shouldSyncWith(entity, other))
                        PollenMessages.PLAY.sendTo(other, new ClientboundUpdateSyncedDataPacket(entity, other, false));
                }
                component.clean();
            }
        });
    }

    public static <T> void set(Entity entity, SyncedDataKey<T> key, T value) {
        getDataComponent(entity).ifPresent(component -> {
            SyncedDataManager.markDirty();
            component.setValue(key, value);
        });
    }

    public static <T> T get(Entity entity, SyncedDataKey<T> key) {
        LazyOptional<ForgeDataComponent> optional = getDataComponent(entity);
        return optional.isPresent() ? optional.orElseThrow(() -> new IllegalStateException("Component should be present")).getValue(key) : key.getDefaultValueSupplier().get();
    }

    public static void writePacketData(FriendlyByteBuf buf, Entity provider, Entity entity, boolean sync) {
        if (sync) {
            getDataComponent(entity).ifPresent(component -> component.writeSyncPacket(buf, provider, entity));
        } else {
            getDataComponent(entity).ifPresent(component -> component.writeUpdatePacket(buf, provider, entity));
        }
    }

    public static void readPacketData(FriendlyByteBuf buf, Entity entity) {
        getDataComponent(entity).ifPresent(component -> component.applySyncPacket(buf));
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        private final ForgeDataComponent component = new ForgeDataComponent();
        private final LazyOptional<ForgeDataComponent> optional = LazyOptional.of(() -> this.component);

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            this.component.writeToNbt(tag, DataComponent.NbtWriteMode.SAVE);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.component.readFromNbt(tag);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return CAPABILITY.orEmpty(capability, this.optional);
        }
    }
}
