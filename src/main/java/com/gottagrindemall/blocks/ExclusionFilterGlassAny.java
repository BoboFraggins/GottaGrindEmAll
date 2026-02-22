package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class ExclusionFilterGlassAny extends ExclusionFilterGlass {
  public ExclusionFilterGlassAny(Properties properties) {
    super(properties);
  }

  @Override
  protected boolean blocksCobblemon(PokemonEntity entity) {
    return true;
  }
}
