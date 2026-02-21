package com.gottagrindemall.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class MobClientPackets {
  public static void handleMonFeedSync(MobFeedSyncPacket message) {
    Level world = Minecraft.getInstance().level;
    if (world == null || !world.isClientSide) return;

    if (world.getEntity(message.entityID()) instanceof LivingEntity mob) {
      CompoundTag nbt = mob.getPersistentData();
      nbt.putBoolean("mobFeedExplode", message.nbt().getBoolean("mobFeedExplode"));
      nbt.putInt("mobCountDown", message.nbt().getInt("mobCountDown"));

      if (message.nbt().getInt("mobCountDown") >= 20) {
        for (int k = 0; k < 20; ++k) {
          double xSpeed = world.random.nextGaussian() * 0.02D;
          double ySpeed = world.random.nextGaussian() * 0.02D;
          double zSpeed = world.random.nextGaussian() * 0.02D;
          world.addParticle(
              ParticleTypes.EXPLOSION,
              mob.getX() + (world.random.nextFloat() * mob.getBbWidth() * 2.0F) - mob.getBbWidth(),
              mob.getY() + (world.random.nextFloat() * mob.getBbHeight()),
              mob.getZ() + (world.random.nextFloat() * mob.getBbWidth() * 2.0F) - mob.getBbWidth(),
              xSpeed,
              ySpeed,
              zSpeed);
          world.addParticle(
              ParticleTypes.LAVA,
              mob.getX() + (world.random.nextFloat() * mob.getBbWidth() * 2.0F) - mob.getBbWidth(),
              mob.getY() + (world.random.nextFloat() * mob.getBbHeight()),
              mob.getZ() + (world.random.nextFloat() * mob.getBbWidth() * 2.0F) - mob.getBbWidth(),
              xSpeed,
              ySpeed,
              zSpeed);
        }
      }
    }
  }
}
