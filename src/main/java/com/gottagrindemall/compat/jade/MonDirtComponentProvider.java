package com.gottagrindemall.compat.jade;

import com.gottagrindemall.GottaGrindEmAll;
import com.gottagrindemall.blocks.MobSpawningBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MonDirtComponentProvider implements IBlockComponentProvider {
  INSTANCE;

  private static final int MIN_LIGHT = 10;

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    BlockState state = accessor.getBlockState();

    if (state.getValue(MobSpawningBlock.POWERED)) {
      tooltip.add(Component.translatable("jade.gottagrindemall.cobblemon_dirt.redstone_disabled"));
      return;
    }

    Level level = accessor.getLevel();
    BlockPos above = accessor.getPosition().above();
    int light = level.getMaxLocalRawBrightness(above);

    if (light >= MIN_LIGHT) {
      tooltip.add(Component.translatable("jade.gottagrindemall.cobblemon_dirt.spawning"));
    } else {
      tooltip.add(
          Component.translatable("jade.gottagrindemall.cobblemon_dirt.too_dark", light, MIN_LIGHT));
    }
  }

  @Override
  public ResourceLocation getUid() {
    return ResourceLocation.fromNamespaceAndPath(GottaGrindEmAll.MOD_ID, "cobblemon_dirt");
  }
}
