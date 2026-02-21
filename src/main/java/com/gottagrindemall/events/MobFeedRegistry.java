package com.gottagrindemall.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class MobFeedRegistry {
  private static final Map<Class<? extends LivingEntity>, Supplier<? extends Item>> REGISTRY =
      new HashMap<>();

  public static void register(
      Class<? extends LivingEntity> entityClass, Supplier<? extends Item> item) {
    REGISTRY.put(entityClass, item);
  }

  public static Item getItemFor(LivingEntity entity) {
    Supplier<? extends Item> supplier = REGISTRY.get(entity.getClass());
    return supplier != null ? supplier.get() : null;
  }
}
