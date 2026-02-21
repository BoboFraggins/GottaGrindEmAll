package com.gottagrindemall.mob_spawners;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;

public abstract class MobSpawner {
  private static final int DEFAULT_MAX_NEARBY = 8;
  private static final double DEFAULT_SCAN_XZ = 5.0;
  private static final double DEFAULT_SCAN_Y = 2.0;

  private final int maxNearbyMobs;
  private final double scanRadiusXZ;
  private final double scanRadiusY;
  private final Class<? extends Entity> entityClass;

  protected MobSpawner(Class<? extends Entity> entityClass) {
    this(entityClass, DEFAULT_MAX_NEARBY, DEFAULT_SCAN_XZ, DEFAULT_SCAN_Y);
  }

  protected MobSpawner(
      Class<? extends Entity> entityClass,
      int maxNearbyMobs,
      double scanRadiusXZ,
      double scanRadiusY) {
    this.entityClass = entityClass;
    this.maxNearbyMobs = maxNearbyMobs;
    this.scanRadiusXZ = scanRadiusXZ;
    this.scanRadiusY = scanRadiusY;
  }

  public abstract Optional<Mob> createMob(ServerLevel world);

  public int countNearby(ServerLevel world, BlockPos position) {
    AABB scanArea = new AABB(position).inflate(scanRadiusXZ, scanRadiusY, scanRadiusXZ);
    return world.getEntitiesOfClass(entityClass, scanArea, e -> e != null && e.isAlive()).size();
  }

  public int maxNearby() {
    return maxNearbyMobs;
  }

  protected boolean hasRoomToSpawn(ServerLevel world, Entity mob) {
    if (!world.noCollision(mob)) return false;
    return world.getEntities(mob, mob.getBoundingBox(), Entity::isAlive).isEmpty();
  }

  public boolean attemptSpawn(ServerLevel world, BlockPos position) {
    Optional<Mob> maybeMob = createMob(world);

    if (maybeMob.isPresent()) {
      Mob mob = maybeMob.get();
      mob.moveTo(
          position.getX() + 0.5D,
          position.getY() + 1.0D,
          position.getZ() + 0.5D,
          world.random.nextFloat() * 360F,
          0F);

      if (!hasRoomToSpawn(world, mob)) return false;

      mob.finalizeSpawn(world, world.getCurrentDifficultyAt(position), MobSpawnType.NATURAL, null);
      world.addFreshEntity(mob);

      return true;
    } else {
      return false;
    }
  }
}
