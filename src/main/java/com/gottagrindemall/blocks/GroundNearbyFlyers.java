package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.ai.PokemonMoveControl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.AABB;

public final class GroundNearbyFlyers {
  public static final double RADIUS_XZ = 10.0;
  public static final double RADIUS_Y = 7.0;

  private GroundNearbyFlyers() {}

  public static void ground(ServerLevel level, BlockPos pos) {
    AABB area = new AABB(pos).inflate(RADIUS_XZ, RADIUS_Y, RADIUS_XZ);
    for (PokemonEntity mon :
        level.getEntitiesOfClass(PokemonEntity.class, area, PokemonEntity::isFlying)) {
      if (mon.getMoveControl() instanceof PokemonMoveControl pmc) {
        pmc.stop();
      }
      mon.getNavigation().stop();
      mon.getBrain().setActiveActivityIfPossible(Activity.IDLE);
      if (mon.couldStopFlying()) {
        mon.setFlying(false);
      }
    }
  }
}
