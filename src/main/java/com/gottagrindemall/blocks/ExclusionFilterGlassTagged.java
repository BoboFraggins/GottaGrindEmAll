package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class ExclusionFilterGlassTagged extends ExclusionFilterGlass {
  private final String label;

  public ExclusionFilterGlassTagged(Properties properties, String label) {
    super(properties);
    this.label = label;
  }

  @Override
  protected boolean blocksCobblemon(PokemonEntity entity) {
    return entity.getPokemon().hasLabels(label);
  }
}
