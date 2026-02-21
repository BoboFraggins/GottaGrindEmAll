package com.gottagrindemall.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
  private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
  public static ModConfigSpec SERVER_CONFIG;

  public static ModConfigSpec.BooleanValue ALLOW_SHINY;

  public static ModConfigSpec.DoubleValue COMMON_SPAWN_WEIGHT;
  public static ModConfigSpec.DoubleValue UNCOMMON_SPAWN_WEIGHT;
  public static ModConfigSpec.DoubleValue RARE_SPAWN_WEIGHT;
  public static ModConfigSpec.DoubleValue ULTRA_RARE_SPAWN_WEIGHT;

  static {
    ALLOW_SHINY =
        SERVER_BUILDER
            .comment("Allow shiny mon to spawn from Cobblemon Dirt")
            .define("allow_shiny", true);

    SERVER_BUILDER
        .comment(
            "Spawn bucket weights for Cobblemon Dirt. Values are normalized so the total equals 100%.",
            "For example, common=2, uncommon=1, rare=1, ultra_rare=1 means 40% common, 20% each for the rest.",
            "Set a weight to 0 to disable that rarity tier entirely.")
        .push("spawn_weights");

    COMMON_SPAWN_WEIGHT =
        SERVER_BUILDER
            .comment("Weight for common mon spawns")
            .defineInRange("common_spawn_weight", 100.0, 0.0, 1000.0);

    UNCOMMON_SPAWN_WEIGHT =
        SERVER_BUILDER
            .comment("Weight for uncommon mon spawns")
            .defineInRange("uncommon_spawn_weight", 10.0, 0.0, 1000.0);

    RARE_SPAWN_WEIGHT =
        SERVER_BUILDER
            .comment("Weight for rare mon spawns")
            .defineInRange("rare_spawn_weight", 5.0, 0.0, 1000.0);

    ULTRA_RARE_SPAWN_WEIGHT =
        SERVER_BUILDER
            .comment("Weight for ultra-rare mon spawns")
            .defineInRange("ultra_rare_spawn_weight", 1.0, 0.0, 1000.0);

    SERVER_BUILDER.pop();

    SERVER_CONFIG = SERVER_BUILDER.build();
  }
}
