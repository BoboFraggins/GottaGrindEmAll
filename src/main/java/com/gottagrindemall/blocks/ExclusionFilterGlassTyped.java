package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class ExclusionFilterGlassTyped extends ExclusionFilterGlass {
  private final ElementalType targetType;

  public ExclusionFilterGlassTyped(Properties properties, ElementalType targetType) {
    super(properties);
    this.targetType = targetType;
  }

  @Override
  protected boolean blocksCobblemon(PokemonEntity entity) {
    for (ElementalType t : entity.getPokemon().getTypes()) {
      if (t.equals(targetType)) return true;
    }
    return false;
  }
}
