package com.gottagrindemall.events;

import com.gottagrindemall.ModItems;
import com.gottagrindemall.network.MobFeedSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class MobTransformEvent {
  private static final String TAG_TRANSFORMING = "mobFeedExplode";
  private static final String TAG_TICK_PROGRESS = "mobCountDown";
  private static final int SYNC_TICKS = 19;
  private static final int TRANSFORM_THRESHOLD = 20;

  @SubscribeEvent
  public void onEntityTick(EntityTickEvent.Post event) {
    Entity target = event.getEntity();
    if (!(target instanceof LivingEntity living)) return;
    if (MobFeedRegistry.getItemFor(living) == null) return;
    if (target.level().isClientSide) return;

    CompoundTag data = target.getPersistentData();
    if (!data.contains(TAG_TRANSFORMING)) return;

    int elapsed = data.getInt(TAG_TICK_PROGRESS);

    if (elapsed <= SYNC_TICKS) {
      advanceProgress(living, data, elapsed);
    }

    if (elapsed >= TRANSFORM_THRESHOLD) {
      completeTransform(target);
    }
  }

  // -- Private methods --

  private void advanceProgress(LivingEntity target, CompoundTag data, int elapsed) {
    data.putInt(TAG_TICK_PROGRESS, elapsed + 1);
    PacketDistributor.sendToAllPlayers(new MobFeedSyncPacket(target, data));
  }

  private void completeTransform(Entity target) {
    target.spawnAtLocation(new ItemStack(ModItems.COBBLEMON_EGG.get()), 0.0F);
    target.playSound(SoundEvents.CREEPER_DEATH, 1F, 1F);
    target.remove(Entity.RemovalReason.DISCARDED);
  }
}
