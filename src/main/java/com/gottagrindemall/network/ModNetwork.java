package com.gottagrindemall.network;

import com.gottagrindemall.GottaGrindEmAll;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ModNetwork {
  public static void register(final RegisterPayloadHandlersEvent event) {
    event
        .registrar(GottaGrindEmAll.MOD_ID)
        .playToClient(
            MobFeedSyncPacket.TYPE, MobFeedSyncPacket.STREAM_CODEC, MobFeedSyncPacket::handle);
  }
}
