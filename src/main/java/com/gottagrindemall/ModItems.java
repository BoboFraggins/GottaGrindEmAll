package com.gottagrindemall;

import com.gottagrindemall.items.CobblemonEgg;
import com.gottagrindemall.items.PocketChow;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
  public static final DeferredRegister.Items ITEMS =
      DeferredRegister.createItems(GottaGrindEmAll.MOD_ID);

  public static final DeferredItem<Item> COBBLEMON_EGG =
      ITEMS.register("cobblemon_egg", () -> new CobblemonEgg(new Item.Properties().stacksTo(1)));

  public static final DeferredItem<Item> POCKET_CHOW =
      ITEMS.register("pocket_chow", () -> new PocketChow(new Item.Properties().stacksTo(1)));

  public static void init(IEventBus bus) {
    ITEMS.register(bus);
  }
}
