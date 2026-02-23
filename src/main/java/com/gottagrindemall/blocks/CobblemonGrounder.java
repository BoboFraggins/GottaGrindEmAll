package com.gottagrindemall.blocks;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CobblemonGrounder extends Block {
  public CobblemonGrounder(Properties properties) {
    super(properties);
  }

  @Override
  public void appendHoverText(
      ItemStack stack,
      Item.TooltipContext context,
      List<Component> list,
      @Nonnull TooltipFlag flag) {
    list.add(
        Component.translatable("jade.gottagrindemall.cobblemon_grounder")
            .withStyle(ChatFormatting.YELLOW));
  }

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
}
