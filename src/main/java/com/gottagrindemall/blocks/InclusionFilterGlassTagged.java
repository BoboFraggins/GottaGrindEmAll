package com.gottagrindemall.blocks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public class InclusionFilterGlassTagged extends InclusionFilterGlass {
  private final String label;

  public InclusionFilterGlassTagged(Properties properties, String label) {
    super(properties);
    this.label = label;
  }

  @Override
  protected boolean allowsCobblemon(PokemonEntity entity) {
    return entity.getPokemon().hasLabels(label);
  }
}
