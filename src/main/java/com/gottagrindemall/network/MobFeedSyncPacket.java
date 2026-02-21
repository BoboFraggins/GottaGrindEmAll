package com.gottagrindemall.network;

import com.gottagrindemall.GottaGrindEmAll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MobFeedSyncPacket(int entityID, CompoundTag nbt) implements CustomPacketPayload {
  public static final Type<MobFeedSyncPacket> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(GottaGrindEmAll.MOD_ID, "mon_feed_sync"));

  public static final StreamCodec<FriendlyByteBuf, MobFeedSyncPacket> STREAM_CODEC =
      CustomPacketPayload.codec(MobFeedSyncPacket::write, MobFeedSyncPacket::new);

  public MobFeedSyncPacket(LivingEntity entity, CompoundTag entityNBT) {
    this(entity.getId(), entityNBT);
  }

  public MobFeedSyncPacket(FriendlyByteBuf buf) {
    this(buf.readInt(), buf.readNbt());
  }

  public static void handle(MobFeedSyncPacket message, final IPayloadContext ctx) {
    ctx.enqueueWork(() -> MobClientPackets.handleMonFeedSync(message));
  }

  public void write(FriendlyByteBuf buf) {
    buf.writeInt(entityID);
    buf.writeNbt(nbt);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
