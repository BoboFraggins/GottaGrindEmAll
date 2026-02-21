package com.gottagrindemall.mob_spawners;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.SpawnPool;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Species;
import com.gottagrindemall.config.ServerConfig;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;

public class CobblemonSpawner extends MobSpawner {
  public CobblemonSpawner() {
    super(PokemonEntity.class);
  }

  public Optional<Mob> createMob(ServerLevel world) {
    Species target = chooseSpecies(world.random);
    if (target == null) return Optional.empty();

    PokemonProperties props = PokemonProperties.Companion.parse(buildPropertiesString(target));
    PokemonEntity entity = props.createEntity(world);

    if (entity.getBehaviour().getMoving().getFly().getCanFly()
        && (!entity.getBehaviour().getMoving().getWalk().getCanWalk()
            || entity.getBehaviour().getMoving().getWalk().getAvoidsLand())) {
      entity.discard();
      return Optional.empty();
    }

    return Optional.of(entity);
  }

  // -- Private methods --

  private String buildPropertiesString(Species target) {
    String props = "species=" + target.getName();
    if (!ServerConfig.ALLOW_SHINY.get()) {
      props += " shiny=no";
    }
    return props;
  }

  private Species chooseSpecies(RandomSource rng) {
    String tier = selectWeightedTier(rng);
    if (tier == null) return null;

    Map<String, Set<String>> tierMap = collectSpeciesByTier();
    Set<String> candidates = tierMap.get(tier);

    if (candidates == null || candidates.isEmpty()) {
      return PokemonSpecies.random();
    }

    List<String> candidateList = new ArrayList<>(candidates);
    String chosen = candidateList.get(rng.nextInt(candidateList.size()));
    return PokemonSpecies.INSTANCE.getByName(chosen);
  }

  private Map<String, Set<String>> collectSpeciesByTier() {
    Map<String, Set<String>> result = new HashMap<>();
    try {
      SpawnPool pool = CobblemonSpawnPools.INSTANCE.getWORLD_SPAWN_POOL();
      for (SpawnDetail entry : pool.getDetails()) {
        if (entry instanceof PokemonSpawnDetail monEntry) {
          String name = monEntry.getPokemon().getSpecies();
          String tier = monEntry.getBucket().getName();
          if (name != null && tier != null) {
            result.computeIfAbsent(tier, k -> new LinkedHashSet<>()).add(name);
          }
        }
      }
    } catch (Exception ignored) {
      // Spawn pool may not be initialized yet
    }
    return result;
  }

  private String selectWeightedTier(RandomSource rng) {
    double wCommon = ServerConfig.COMMON_SPAWN_WEIGHT.get();
    double wUncommon = ServerConfig.UNCOMMON_SPAWN_WEIGHT.get();
    double wRare = ServerConfig.RARE_SPAWN_WEIGHT.get();
    double wUltraRare = ServerConfig.ULTRA_RARE_SPAWN_WEIGHT.get();
    double sum = wCommon + wUncommon + wRare + wUltraRare;
    if (sum <= 0) return null;

    double value = rng.nextDouble() * sum;
    if (value < wCommon) return "common";
    value -= wCommon;
    if (value < wUncommon) return "uncommon";
    value -= wUncommon;
    if (value < wRare) return "rare";
    return "ultra-rare";
  }
}
