package com.gottagrindemall.events;

import com.gottagrindemall.network.MobFeedSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class MobFeedInteractEvent {
  @SubscribeEvent
  public void clickOnEntity(PlayerInteractEvent.EntityInteract event) {
    if (!(event.getTarget() instanceof LivingEntity mob)) return;

    Item expectedItem = MobFeedRegistry.getItemFor(mob);
    if (expectedItem == null) return;

    ItemStack eventItem = event.getItemStack();
    if (eventItem.isEmpty() || eventItem.getItem() != expectedItem) return;

    if (mob.level().isClientSide) return;

    CompoundTag nbt = mob.getPersistentData();
    if (nbt.contains("mobFeedExplode")) return;

    mob.setInvulnerable(true);
    nbt.putBoolean("mobFeedExplode", true);
    nbt.putInt("mobCountDown", 0);

    Vec3 vec3d = mob.getDeltaMovement();
    mob.setDeltaMovement(vec3d.x, 0.06D, vec3d.z);
    mob.setNoGravity(true);

    if (event.getEntity() instanceof ServerPlayer) {
      PacketDistributor.sendToPlayersNear(
          (ServerLevel) mob.level(),
          null,
          mob.getX(),
          mob.getY(),
          mob.getZ(),
          32,
          new MobFeedSyncPacket(mob, nbt));
    }

    if (!event.getEntity().getAbilities().instabuild) eventItem.shrink(1);
  }
}
