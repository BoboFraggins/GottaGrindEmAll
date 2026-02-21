from PIL import Image
import os

SRC = '/tmp/assets/minecraft/textures/block'
DST = '/Users/peter/src/GottaGrindEmAll/src/main/resources/assets/gottagrindemall/textures/block'
SIZE = 64

# Load vanilla textures
grass_top = Image.open(f'{SRC}/grass_block_top.png').convert('RGBA')
grass_side = Image.open(f'{SRC}/grass_block_side.png').convert('RGBA')
grass_side_overlay = Image.open(f'{SRC}/grass_block_side_overlay.png').convert('RGBA')
dirt = Image.open(f'{SRC}/dirt.png').convert('RGBA')

# The grass_block_top.png is grayscale - Minecraft tints it green at runtime.
# We'll tint it bright red instead and bake that in.
# The grass_block_side.png already has dirt + a green grass strip baked in.
# The grass_block_side_overlay.png is the grass-only overlay (tinted at runtime).

# --- Cobblemon Dirt Top ---
# Tint the grayscale grass top with bright red
top = grass_top.copy()
pixels = top.load()
for y in range(top.height):
    for x in range(top.width):
        r, g, b, a = pixels[x, y]
        # The grayscale value represents intensity; tint it red
        gray = r  # since it's grayscale, r==g==b
        # Bright red tint: high red, low green/blue
        new_r = min(255, int(gray * 1.1))
        new_g = min(255, int(gray * 0.25))
        new_b = min(255, int(gray * 0.22))
        pixels[x, y] = (new_r, new_g, new_b, a)

top = top.resize((SIZE, SIZE), Image.NEAREST)
top.save(f'{DST}/cobblemon_dirt_top.png')

# --- Cobblemon Dirt Side ---
# Start with the vanilla grass_block_side (has dirt + green grass baked in)
# Composite the overlay on top, tinted red, to replace the green
side = grass_side.copy()
overlay = grass_side_overlay.copy()

# Tint the overlay red
ov_pixels = overlay.load()
for y in range(overlay.height):
    for x in range(overlay.width):
        r, g, b, a = ov_pixels[x, y]
        if a > 0:
            gray = max(r, g, b)  # use brightness
            new_r = min(255, int(gray * 1.1))
            new_g = min(255, int(gray * 0.25))
            new_b = min(255, int(gray * 0.22))
            ov_pixels[x, y] = (new_r, new_g, new_b, a)

# The vanilla side texture has a green tint baked into the top rows.
# Replace the top portion with dirt + red overlay.
# First, use the dirt texture as a base for the whole side
side_base = dirt.copy()
side_pixels = side_base.load()
# Now paste the original side's dirt portion (it's basically dirt with grass on top)
orig_side_pixels = grass_side.load()

# Actually, simpler approach: take the vanilla side, desaturate the green from the
# top grass portion, then composite red overlay on top.
side_result = grass_side.copy().convert('RGBA')
sr_pixels = side_result.load()

# Desaturate/neutralize the green from the grass portion of the side texture
for y in range(side_result.height):
    for x in range(side_result.width):
        r, g, b, a = sr_pixels[x, y]
        # The top rows have greenish tint; convert to neutral dirt-like
        if g > r and g > b:  # greenish pixel
            gray = int(0.3 * r + 0.59 * g + 0.11 * b)
            sr_pixels[x, y] = (min(255, int(gray * 1.05)), int(gray * 0.85), int(gray * 0.7), a)

# Composite the red-tinted overlay on top
side_result = Image.alpha_composite(side_result, overlay)
side_result = side_result.resize((SIZE, SIZE), Image.NEAREST)
side_result.save(f'{DST}/cobblemon_dirt_side.png')

print(f"Cobblemon Dirt textures generated at {SIZE}x{SIZE} from vanilla grass/dirt textures")
print(f"  {DST}/cobblemon_dirt_top.png")
print(f"  {DST}/cobblemon_dirt_side.png")
