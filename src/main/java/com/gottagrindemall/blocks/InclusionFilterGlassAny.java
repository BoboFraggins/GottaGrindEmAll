package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class InclusionFilterGlassAny extends InclusionFilterGlass {
  public InclusionFilterGlassAny(Properties properties) {
    super(properties);
  }

  @Override
  protected boolean allowsCobblemon(PokemonEntity entity) {
    return true;
  }
}
