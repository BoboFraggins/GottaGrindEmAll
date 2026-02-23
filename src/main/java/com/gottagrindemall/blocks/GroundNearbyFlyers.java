package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.ai.PokemonMoveControl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GroundNearbyFlyers {
  public static final double RADIUS_XZ = 7.0;
  public static final double RADIUS_Y = 7.0;

  // Downward nudge applied each tick while a mon is airborne above the block.
  // Strong enough to overcome hover/levitation but not so large it looks like a drop.
  private static final double DOWNWARD_NUDGE = -0.3;

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

      // If the mon is still airborne (not on the ground), push it downward so
      // that external forced movement (e.g. MGU fans) can actually move it.
      if (!mon.onGround()) {
        Vec3 current = mon.getDeltaMovement();
        // Only add downward velocity if the mon isn't already falling faster.
        if (current.y > DOWNWARD_NUDGE) {
          mon.setDeltaMovement(current.x, DOWNWARD_NUDGE, current.z);
        }
      }
    }
  }
}
