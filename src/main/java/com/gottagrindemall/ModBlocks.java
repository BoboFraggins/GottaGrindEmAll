package com.gottagrindemall;

import com.gottagrindemall.blocks.CobblemonDirt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
  public static final DeferredRegister.Blocks BLOCKS =
      DeferredRegister.createBlocks(GottaGrindEmAll.MOD_ID);
  public static final DeferredRegister.Items BLOCK_ITEMS =
      DeferredRegister.createItems(GottaGrindEmAll.MOD_ID);

  public static final DeferredBlock<CobblemonDirt> COBBLEMON_DIRT =
      BLOCKS.register(
          "cobblemon_dirt",
          () ->
              new CobblemonDirt(
                  Block.Properties.of()
                      .mapColor(MapColor.COLOR_RED)
                      .strength(1.0F, 2000.0F)
                      .sound(SoundType.GRAVEL)
                      .randomTicks()));

  public static final DeferredItem<BlockItem> COBBLEMON_DIRT_ITEM =
      BLOCK_ITEMS.register(
          "cobblemon_dirt", () -> new BlockItem(COBBLEMON_DIRT.get(), new Item.Properties()));

  public static void init(IEventBus bus) {
    BLOCKS.register(bus);
    BLOCK_ITEMS.register(bus);
  }
}
