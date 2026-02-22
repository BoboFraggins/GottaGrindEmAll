package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class InclusionFilterGlass extends Block {
  public InclusionFilterGlass(Properties properties) {
    super(properties);
  }

  protected abstract boolean allowsCobblemon(PokemonEntity entity);

  @Override
  public void onPlace(
      BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 20, 60));
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
    GroundNearbyFlyers.ground(level, pos);
    level.scheduleTick(pos, this, Mth.nextInt(rand, 20, 60));
  }

  @Override
  protected VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    if (context instanceof EntityCollisionContext entityContext) {
      Entity entity = entityContext.getEntity();
      if (entity instanceof PokemonEntity pokemon && allowsCobblemon(pokemon)) {
        return Shapes.empty();
      }
    }
    return Shapes.block();
  }
}
