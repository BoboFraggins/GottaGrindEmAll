package com.gottagrindemall.items;

import com.gottagrindemall.ModBlocks;
import java.util.*;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CobblemonEgg extends Egg {
  public CobblemonEgg(Properties properties) {
    super(properties);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, @Nonnull TooltipContext context, List<Component> list, TooltipFlag flag) {
    list.add(
        Component.translatable("tooltip.gottagrindemall.cobblemon_egg_1")
            .withStyle(ChatFormatting.YELLOW));
    list.add(
        Component.translatable("tooltip.gottagrindemall.cobblemon_egg_2")
            .withStyle(ChatFormatting.YELLOW));
  }

  public List<Block> targetableBlocks() {
    return new ArrayList<>(
        List.of(Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.MYCELIUM));
  }

  public BlockState replacementBlockState() {
    return ModBlocks.COBBLEMON_DIRT.get().defaultBlockState();
  }
}
