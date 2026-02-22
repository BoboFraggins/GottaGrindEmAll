package com.gottagrindemall;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTab {
  public static final DeferredRegister<CreativeModeTab> TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GottaGrindEmAll.MOD_ID);

  public static final Supplier<CreativeModeTab> GOTTA_GRIND_TAB =
      TABS.register(
          "gottagrindemall_tab",
          () ->
              CreativeModeTab.builder()
                  .title(Component.translatable("itemGroup.gottagrindemall"))
                  .icon(() -> new ItemStack(ModItems.COBBLEMON_EGG.get()))
                  .displayItems(
                      (params, output) -> {
                        output.accept(ModItems.COBBLEMON_EGG.get());
                        output.accept(ModItems.POCKET_CHOW.get());
                        output.accept(ModBlocks.COBBLEMON_DIRT_ITEM.get());
                        ModBlocks.FILTER_GLASS_ITEMS
                            .values()
                            .forEach(item -> output.accept(item.get()));
                      })
                  .build());

  public static void init(IEventBus bus) {
    TABS.register(bus);
  }
}
