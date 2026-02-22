package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class InclusionFilterGlassTyped extends InclusionFilterGlass {
  private final ElementalType targetType;

  public InclusionFilterGlassTyped(Properties properties, ElementalType targetType) {
    super(properties);
    this.targetType = targetType;
  }

  @Override
  protected boolean allowsCobblemon(PokemonEntity entity) {
    for (ElementalType t : entity.getPokemon().getTypes()) {
      if (t.equals(targetType)) return true;
    }
    return false;
  }
}
