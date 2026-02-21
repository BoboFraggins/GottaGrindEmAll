package com.gottagrindemall.items;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class PocketChow extends Item {
  public PocketChow(Properties properties) {
    super(properties);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, @Nonnull TooltipContext context, List<Component> list, TooltipFlag flag) {
    list.add(
        Component.translatable("tooltip.gottagrindemall.pocket_chow")
            .withStyle(ChatFormatting.YELLOW));
  }
}
