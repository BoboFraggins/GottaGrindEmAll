package com.gottagrindemall.blocks;

import com.gottagrindemall.mob_spawners.MobSpawner;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class MobSpawningBlock extends Block {
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  private final MobSpawner spawner;

  protected MobSpawningBlock(Properties properties, MobSpawner spawner) {
    super(properties);
    this.spawner = spawner;
    registerDefaultState(stateDefinition.any().setValue(POWERED, false));
  }

  protected abstract boolean canSpawnMob(LevelAccessor world, BlockPos position);

  @Override
  public void neighborChanged(
      @Nonnull BlockState state,
      @Nonnull Level level,
      @Nonnull BlockPos pos,
      @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos,
      boolean isMoving) {
    boolean wasPowered = state.getValue(POWERED);
    boolean isPowered = level.hasNeighborSignal(pos);

    if (wasPowered != isPowered) {
      level.setBlock(pos, state.setValue(POWERED, isPowered), 3);

      // Rising edge: force a spawn attempt
      if (!wasPowered && isPowered && level instanceof ServerLevel serverLevel) {
        spawner.attemptSpawn(serverLevel, pos);
      }
    }

    if (!isPowered) {
      scheduleIfValid(level, pos);
    }
  }

  @Override
  public void onPlace(
      @Nonnull BlockState state,
      @Nonnull Level level,
      @Nonnull BlockPos pos,
      @Nonnull BlockState oldState,
      boolean isMoving) {
    boolean isPowered = level.hasNeighborSignal(pos);
    if (isPowered != state.getValue(POWERED)) {
      level.setBlock(pos, state.setValue(POWERED, isPowered), 3);
    }
    if (!isPowered) {
      scheduleIfValid(level, pos);
    }
  }

  @Override
  public void randomTick(
      @Nonnull BlockState state,
      @Nonnull ServerLevel level,
      @Nonnull BlockPos pos,
      @Nonnull RandomSource rand) {
    if (state.getValue(POWERED)) return;
    if (!canSpawnMob(level, pos)) return;
    if (spawner.countNearby(level, pos) < spawner.maxNearby()) {
      spawner.attemptSpawn(level, pos);
    }
  }

  @Override
  public void tick(
      @Nonnull BlockState state,
      @Nonnull ServerLevel level,
      @Nonnull BlockPos pos,
      @Nonnull RandomSource rand) {
    randomTick(state, level, pos, rand);
    if (!state.getValue(POWERED)) {
      scheduleIfValid(level, pos);
    }
  }

  @Nonnull
  @Override
  public BlockState updateShape(
      @Nonnull BlockState stateIn,
      @Nonnull Direction facing,
      @Nonnull BlockState facingState,
      @Nonnull LevelAccessor level,
      @Nonnull BlockPos pos,
      @Nonnull BlockPos facingPos) {
    if (!stateIn.getValue(POWERED)) {
      scheduleIfValid(level, pos);
    }
    return super.updateShape(stateIn, facing, facingState, level, pos, facingPos);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
  }

  // -- Private methods --

  private void scheduleIfValid(LevelAccessor world, BlockPos position) {
    if (canSpawnMob(world, position)) {
      world.scheduleTick(position, this, Mth.nextInt(world.getRandom(), 20, 60));
    }
  }
}
