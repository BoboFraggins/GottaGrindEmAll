#!/usr/bin/env python3
"""Generates recipe PNG/GIF images for the GottaGrindEmAll wiki."""

import zipfile
from pathlib import Path
from PIL import Image

SCRIPT_DIR = Path(__file__).parent
PROJECT_DIR = SCRIPT_DIR.parent
GRADLE_CACHE = Path.home() / ".gradle"
MC_CLIENT_JAR = GRADLE_CACHE / "caches/neoformruntime/artifacts/minecraft_1.21.1_client.jar"
COBBLEMON_DIR = Path.home() / "src/cobblemon"
MGU_DIR = Path.home() / "src/Mob-Grinding-Utils"

SCALE = 4

# Slot positions in the original 176x166 crafting GUI (top-left of inner 16x16 area)
GRID_SLOTS = [
    (30, 17), (48, 17), (66, 17),  # Row 0
    (30, 35), (48, 35), (66, 35),  # Row 1
    (30, 53), (48, 53), (66, 53),  # Row 2
]
OUTPUT_SLOT = (124, 35)

APRICORN_COLORS = ["black", "blue", "green", "pink", "red", "white", "yellow"]


def extract_from_jar(jar_path, asset_path):
    """Extract a PNG from a JAR file and return as RGBA Image."""
    with zipfile.ZipFile(jar_path) as z:
        with z.open(asset_path) as f:
            return Image.open(f).convert("RGBA")


def load_texture(path):
    """Load a PNG texture and return as RGBA Image."""
    return Image.open(path).convert("RGBA")


def load_gui_background():
    """Load and scale the crafting table GUI background."""
    gui = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/gui/container/crafting_table.png"
    )
    gui = gui.crop((0, 0, 176, 166))
    return gui.resize((176 * SCALE, 166 * SCALE), Image.NEAREST)


def load_item(img, size):
    """Scale an item texture to the target size."""
    return img.resize((size, size), Image.NEAREST)


def build_frame(gui, bottle_tex, apricorn_tex, seeds_tex, output_tex):
    """Compose a single recipe frame onto a copy of the GUI background."""
    frame = gui.copy()
    item_size = 16 * SCALE

    bottle = load_item(bottle_tex, item_size)
    apricorn = load_item(apricorn_tex, item_size)
    seeds = load_item(seeds_tex, item_size)
    output = load_item(output_tex, item_size)

    # BAB / ASA / BAB
    ingredients = [
        bottle, apricorn, bottle,
        apricorn, seeds, apricorn,
        bottle, apricorn, bottle,
    ]

    for i, (sx, sy) in enumerate(GRID_SLOTS):
        frame.paste(ingredients[i], (sx * SCALE, sy * SCALE), ingredients[i])

    frame.paste(output, (OUTPUT_SLOT[0] * SCALE, OUTPUT_SLOT[1] * SCALE), output)
    return frame


def generate_pocket_chow_recipe():
    """Generate an animated GIF cycling through bottle/bucket and apricorn colors."""
    gui = load_gui_background()

    # Load bottle textures
    xp_bottle = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/item/experience_bottle.png"
    )

    mgu_bucket_full = load_texture(
        MGU_DIR
        / "MobGrindingUtils/MobGrindingUtils/src/main/resources/assets/mob_grinding_utils/textures/item/fluid_xp_bucket.png"
    )
    # MGU bucket is 16x64 (4 animated frames) - take first frame
    mgu_bucket = mgu_bucket_full.crop((0, 0, 16, 16))

    bottle_textures = [xp_bottle, mgu_bucket]

    # Load apricorn textures
    apricorn_textures = []
    for color in APRICORN_COLORS:
        tex = load_texture(
            COBBLEMON_DIR
            / f"common/src/main/resources/assets/cobblemon/textures/item/{color}_apricorn.png"
        )
        apricorn_textures.append(tex)

    # Load constant textures
    seeds = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/item/wheat_seeds.png"
    )
    pocket_chow = load_texture(
        PROJECT_DIR
        / "src/main/resources/assets/gottagrindemall/textures/item/pocket_chow.png"
    )

    # Generate frames: cycle through all combinations
    # Use apricorn count as the primary cycle, alternate bottle each frame
    frames = []
    for i, apricorn_tex in enumerate(apricorn_textures):
        bottle_tex = bottle_textures[i % len(bottle_textures)]
        frame = build_frame(gui, bottle_tex, apricorn_tex, seeds, pocket_chow)
        # Convert to RGB with white background for GIF compatibility
        bg = Image.new("RGB", frame.size, (198, 198, 198))
        bg.paste(frame, mask=frame.split()[3])
        frames.append(bg)

    # Save as animated GIF
    output_path = SCRIPT_DIR / "pocket_chow_recipe.gif"
    frames[0].save(
        output_path,
        save_all=True,
        append_images=frames[1:],
        duration=1000,
        loop=0,
    )
    print(f"Generated {output_path}")


if __name__ == "__main__":
    generate_pocket_chow_recipe()
