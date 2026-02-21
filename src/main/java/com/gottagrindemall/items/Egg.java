package com.gottagrindemall.items;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Egg extends Item {
  public Egg(Properties properties) {
    super(properties);
  }

  public abstract List<Block> targetableBlocks();

  public abstract BlockState replacementBlockState();

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    Player player = context.getPlayer();
    BlockPos pos = context.getClickedPos();

    if (world.isClientSide) {
      player.swing(context.getHand());

      return InteractionResult.PASS;
    } else {
      for (int x = -2; x <= 2; x++) {
        for (int z = -2; z <= 2; z++) {
          BlockState state = world.getBlockState(pos.offset(x, 0, z));
          Block block = state.getBlock();
          if (targetableBlocks().contains(block)) {
            world.levelEvent(
                null,
                2001,
                pos.offset(x, 0, z),
                Block.getId(world.getBlockState(pos.offset(x, 0, z))));
            world.setBlock(pos.offset(x, 0, z), replacementBlockState(), 3);
          }
        }
      }
      if (!player.getAbilities().instabuild) {
        player.getItemInHand(context.getHand()).shrink(1);
      }

      return InteractionResult.SUCCESS;
    }
  }
}
