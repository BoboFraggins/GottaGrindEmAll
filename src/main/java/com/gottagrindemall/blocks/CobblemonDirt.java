package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.gottagrindemall.mob_spawners.CobblemonSpawner;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CobblemonDirt extends MobSpawningBlock {
  private static final int MIN_LIGHT_LEVEL = 10;
  private static final double GROUND_RADIUS_XZ = 3.0;
  private static final double GROUND_HEIGHT = 7.0;

  public CobblemonDirt(Properties properties) {
    super(properties, new CobblemonSpawner());
  }

  protected boolean canSpawnMob(LevelAccessor world, BlockPos position) {
    BlockPos above = position.above();
    return world.getMaxLocalRawBrightness(above) >= MIN_LIGHT_LEVEL
        && world.getBlockState(above).isAir();
  }

  @Override
  public void tick(
      @Nonnull BlockState state,
      @Nonnull ServerLevel level,
      @Nonnull BlockPos pos,
      @Nonnull RandomSource rand) {
    groundNearbyFlyers(level, pos);
    super.tick(state, level, pos, rand);
  }

  private void groundNearbyFlyers(ServerLevel level, BlockPos pos) {
    AABB area = new AABB(pos).inflate(GROUND_RADIUS_XZ, GROUND_HEIGHT, GROUND_RADIUS_XZ);
    for (PokemonEntity mon :
        level.getEntitiesOfClass(PokemonEntity.class, area, PokemonEntity::isFlying)) {
      if (mon.couldStopFlying()) {
        mon.setFlying(false);
      }
    }
  }
}
