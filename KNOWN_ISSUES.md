# Known Issues

## Flying Cobblemon

Some Cobblemon species are fly-only and cannot walk on land (e.g., Magnemite, Gastly, Koffing, Lunatone). These species:

- **Will not spawn from Mon Dirt.** They are filtered out during spawning since they cannot be grounded.
- **Cannot be pushed by Mob Grinding Utils fans while flying.** Cobblemon's flying AI overwrites external velocity changes each tick, making fan-based movement ineffective for airborne Pokemon.

Mon Dirt will ground nearby flying Cobblemon that are capable of walking, but fly-only species are excluded entirely to avoid creating unpushable mobs.
