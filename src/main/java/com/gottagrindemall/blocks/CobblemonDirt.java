package com.gottagrindemall.blocks;

import com.gottagrindemall.mob_spawners.CobblemonSpawner;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CobblemonDirt extends MobSpawningBlock {
  private static final int MIN_LIGHT_LEVEL = 10;

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
    GroundNearbyFlyers.ground(level, pos);
    super.tick(state, level, pos, rand);
  }
}
