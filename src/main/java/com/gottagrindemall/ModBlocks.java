package com.gottagrindemall;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.gottagrindemall.blocks.CobblemonDirt;
import com.gottagrindemall.blocks.ExclusionFilterGlassAny;
import com.gottagrindemall.blocks.ExclusionFilterGlassTyped;
import com.gottagrindemall.blocks.InclusionFilterGlassAny;
import com.gottagrindemall.blocks.InclusionFilterGlassTyped;
import java.util.LinkedHashMap;
import java.util.Map;
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

  // --- Cobblemon Filter Glass Blocks ---
  // 19 types x 2 variants (inclusion, exclusion) = 38 blocks

  private static final String[] FILTER_TYPES = {
    "any", "bug", "dark", "dragon", "electric", "fairy", "fighting",
    "fire", "flying", "ghost", "grass", "ground", "ice", "normal",
    "poison", "psychic", "rock", "steel", "water"
  };

  private static final String[] FILTER_VARIANTS = {"inclusion", "exclusion"};

  // Map of block id -> DeferredBlock, populated statically below
  public static final Map<String, DeferredBlock<Block>> FILTER_GLASS_BLOCKS = new LinkedHashMap<>();
  public static final Map<String, DeferredItem<BlockItem>> FILTER_GLASS_ITEMS =
      new LinkedHashMap<>();

  static {
    for (String variant : FILTER_VARIANTS) {
      for (String type : FILTER_TYPES) {
        String id = "cobblemon_filter_glass_" + variant + "_" + type;
        DeferredBlock<Block> block =
            BLOCKS.register(
                id,
                () -> {
                  Block.Properties props =
                      Block.Properties.of()
                          .strength(0.3F)
                          .sound(SoundType.GLASS)
                          .noOcclusion()
                          .isSuffocating((state, level, pos) -> false);
                  if (type.equals("any")) {
                    return variant.equals("inclusion")
                        ? new InclusionFilterGlassAny(props)
                        : new ExclusionFilterGlassAny(props);
                  } else {
                    ElementalType elemType = ElementalTypes.INSTANCE.get(type);
                    return variant.equals("inclusion")
                        ? new InclusionFilterGlassTyped(props, elemType)
                        : new ExclusionFilterGlassTyped(props, elemType);
                  }
                });
        DeferredItem<BlockItem> item =
            BLOCK_ITEMS.register(
                id, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(64)));
        FILTER_GLASS_BLOCKS.put(id, block);
        FILTER_GLASS_ITEMS.put(id, item);
      }
    }
  }

  public static void init(IEventBus bus) {
    BLOCKS.register(bus);
    BLOCK_ITEMS.register(bus);
  }
}
