# Changelog

## 1.21.1.1

### Added
- Pocket Chow now shows a tooltip explaining its usage
- Cobblemon Dirt grounds nearby flying Cobblemon so they can be pushed by fans

### Changed
- Mob feed system now uses a registry mapping entity types to items, making it extensible beyond Cobblemon

### Fixed
- Cobblemon Dirt spawn tick chain now properly reschedules, greatly improving spawn rates
- Cobblemon Egg tooltip now displays correctly instead of showing the localization key

## 1.21.1.0 - Initial Release

### Added
- **Cobblemon Egg** - Use on grass, dirt, mycelium, or farmland to create a 5x5 area of Mon Dirt
- **Cobblemon Dirt** - Spawns random Cobblemon with configurable rarity bucket weights
  - Redstone integration: constant signal disables spawning, rising edge forces a spawn
  - Optional Jade tooltip support showing spawn status
- **Pocket Chow** - Use on a Cobblemon to transform it into a Cobblemon Egg
- Server configuration for shiny spawns and spawn bucket weights
