#!/usr/bin/env python3
"""Generates recipe PNG/GIF images for the GottaGrindEmAll wiki."""

import zipfile
from pathlib import Path
from PIL import Image, ImageDraw

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

FILTER_TYPES = [
    "normal", "fire", "water", "grass", "electric", "ice", "fighting",
    "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
    "dragon", "dark", "steel", "fairy",
]

TEXTURES_DIR = (
    PROJECT_DIR / "src/main/resources/assets/gottagrindemall/textures/block/filters/cobblemon"
)
GEM_DIR = (
    COBBLEMON_DIR / "common/src/main/resources/assets/cobblemon/textures/item/type_gem"
)


def extract_from_jar(jar_path, asset_path):
    """Extract a PNG from a JAR file and return as RGBA Image."""
    with zipfile.ZipFile(jar_path) as z:
        with z.open(asset_path) as f:
            return Image.open(f).convert("RGBA")


def load_texture(path):
    """Load a PNG texture and return as RGBA Image."""
    return Image.open(path).convert("RGBA")


GUI_HEIGHT = 80  # Crop to just the crafting area, excluding the player inventory below


def load_gui_background():
    """Load and scale the crafting table GUI background."""
    gui = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/gui/container/crafting_table.png"
    )
    gui = gui.crop((0, 0, 176, GUI_HEIGHT))
    return gui.resize((176 * SCALE, GUI_HEIGHT * SCALE), Image.NEAREST)


def load_item(img, size):
    """Scale an item texture to the target size."""
    return img.resize((size, size), Image.NEAREST)


def render_isometric_block(side_tex, top_tex, size):
    """Render a Minecraft-style isometric block icon at native resolution.

    Uses inverse mapping: for each output pixel, determine which face it
    belongs to and sample the source texture, giving full-resolution output
    with no upscaling artifacts.

    Canvas layout (in normalised [0,1] coords, origin top-left):
      Top face (rhombus):
        top:   (0.5, 0),  left: (0, 0.25),  bottom: (0.5, 0.5),  right: (1, 0.25)

      Left face (parallelogram):
        top-left: (0, 0.25), top-right: (0.5, 0.5)
        bot-left: (0, 0.75), bot-right: (0.5, 1.0)

      Right face (parallelogram):
        top-left: (0.5, 0.5), top-right: (1, 0.25)
        bot-left: (0.5, 1.0), bot-right: (1, 0.75)

    Brightness: top=100%, left=50%, right=75%.
    """
    # Work with source textures at their native resolution
    side = side_tex.convert("RGBA")
    top = top_tex.convert("RGBA")
    sw, sh = side.size
    tw, th = top.size
    side_px = side.load()
    top_px = top.load()

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    c_px = canvas.load()

    for py in range(size):
        for px in range(size):
            # Normalised coords [0, 1]
            nx = px / size
            ny = py / size

            # ---- Determine face and compute texture UV ----
            # The 2:1 dimetric cube divides the icon into three regions.
            # We use the parallelogram equations to test membership and
            # compute (u, v) in [0, 1] for each face.

            # TOP FACE: rhombus with vertices at (0.5,0),(1,0.25),(0.5,0.5),(0,0.25)
            # Parametric: point = (0.5,0) + s*(0.5,0.25) + t*(-0.5,0.25)
            #   => nx = 0.5 + 0.5*s - 0.5*t
            #   => ny =       0.25*s + 0.25*t
            # Solve: s+t = ny/0.25 = 4*ny,  s-t = (nx-0.5)/0.5 = 2*nx-1
            #   s = (4*ny + 2*nx - 1) / 2 = 2*ny + nx - 0.5
            #   t = (4*ny - 2*nx + 1) / 2 = 2*ny - nx + 0.5
            s_top = 2 * ny + nx - 0.5
            t_top = 2 * ny - nx + 0.5
            in_top = 0 <= s_top <= 1 and 0 <= t_top <= 1

            # LEFT FACE: parallelogram (0,0.25)→(0.5,0.5)→(0.5,1)→(0,0.75)
            # Parametric: point = (0,0.25) + s*(0.5,0.25) + t*(0,0.5)
            #   nx = 0.5*s  => s = 2*nx
            #   ny = 0.25 + 0.25*s + 0.5*t  => t = (ny - 0.25 - 0.25*s) / 0.5
            s_left = 2 * nx
            t_left = (ny - 0.25 - 0.25 * s_left) / 0.5
            in_left = 0 <= s_left <= 1 and 0 <= t_left <= 1

            # RIGHT FACE: parallelogram (0.5,0.5)→(1,0.25)→(1,0.75)→(0.5,1)
            # Parametric: point = (0.5,0.5) + s*(0.5,-0.25) + t*(0,0.5)
            #   nx = 0.5 + 0.5*s  => s = 2*(nx-0.5) = 2*nx - 1
            #   ny = 0.5 - 0.25*s + 0.5*t  => t = (ny - 0.5 + 0.25*s) / 0.5
            s_right = 2 * nx - 1
            t_right = (ny - 0.5 + 0.25 * s_right) / 0.5
            in_right = 0 <= s_right <= 1 and 0 <= t_right <= 1

            # Priority: top > right > left (matches Minecraft's painter's order)
            if in_top:
                u = int(s_top * tw) % tw
                v = int(t_top * th) % th
                r, g, b, a = top_px[u, v]
            elif in_right:
                u = int(s_right * sw) % sw
                v = int(t_right * sh) % sh
                r, g, b, a = side_px[u, v]
                r, g, b = r * 3 // 4, g * 3 // 4, b * 3 // 4  # 75%
            elif in_left:
                u = int(s_left * sw) % sw
                v = int(t_left * sh) % sh
                r, g, b, a = side_px[u, v]
                r, g, b = r // 2, g // 2, b // 2  # 50%
            else:
                continue

            c_px[px, py] = (r, g, b, a)

    return canvas


def compose_frame(gui, grid_items, output_tex):
    """Paste 9 grid items and 1 output onto a copy of the GUI background.

    grid_items: list of 9 RGBA Images (or None for empty slot), row-major order.
    Each image is scaled to item_size if not already that size.
    """
    frame = gui.copy()
    item_size = 16 * SCALE
    output = output_tex if output_tex.size == (item_size, item_size) else load_item(output_tex, item_size)
    for i, (sx, sy) in enumerate(GRID_SLOTS):
        tex = grid_items[i]
        if tex is not None:
            item = tex if tex.size == (item_size, item_size) else load_item(tex, item_size)
            frame.paste(item, (sx * SCALE, sy * SCALE), item)
    frame.paste(output, (OUTPUT_SLOT[0] * SCALE, OUTPUT_SLOT[1] * SCALE), output)
    return frame


def to_gif_frame(frame):
    """Flatten RGBA frame onto grey background for GIF compatibility."""
    bg = Image.new("RGB", frame.size, (198, 198, 198))
    bg.paste(frame, mask=frame.split()[3])
    return bg


def save_gif(frames, path, duration=1000):
    frames[0].save(
        path,
        save_all=True,
        append_images=frames[1:],
        duration=duration,
        loop=0,
    )
    print(f"Generated {path}")


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
        frames.append(to_gif_frame(frame))

    save_gif(frames, SCRIPT_DIR / "pocket_chow_recipe.gif")


def generate_inclusion_any_recipe():
    """Inclusion Glass (Any): 8 vanilla glass + 1 apricorn -> 8 Inclusion Glass.
    Animated: cycles through all 7 apricorn colors.
    """
    gui = load_gui_background()
    item_size = 16 * SCALE

    glass_tex = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/block/glass.png"
    )
    glass_top_tex = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/block/glass.png"
    )
    vanilla_glass_iso = render_isometric_block(glass_tex, glass_top_tex, item_size)

    inclusion_any_side = load_texture(TEXTURES_DIR / "any/inclusion_side.png")
    inclusion_any_top = load_texture(TEXTURES_DIR / "any/inclusion_top.png")
    output_iso = render_isometric_block(inclusion_any_side, inclusion_any_top, item_size)

    frames = []
    for color in APRICORN_COLORS:
        apricorn_tex = load_texture(
            COBBLEMON_DIR
            / f"common/src/main/resources/assets/cobblemon/textures/item/{color}_apricorn.png"
        )
        apricorn_scaled = load_item(apricorn_tex, item_size)
        # GGG / GAG / GGG
        grid = [
            vanilla_glass_iso, vanilla_glass_iso, vanilla_glass_iso,
            vanilla_glass_iso, apricorn_scaled,   vanilla_glass_iso,
            vanilla_glass_iso, vanilla_glass_iso, vanilla_glass_iso,
        ]
        frame = compose_frame(gui, grid, output_iso)
        frames.append(to_gif_frame(frame))

    save_gif(frames, SCRIPT_DIR / "inclusion_any_recipe.gif")


def generate_exclusion_any_recipe():
    """Exclusion Glass (Any): lava bucket center + 8 Inclusion Glass -> 8 Exclusion Glass.
    Static (no animation needed).
    """
    gui = load_gui_background()
    item_size = 16 * SCALE

    lava_bucket_tex = extract_from_jar(
        MC_CLIENT_JAR, "assets/minecraft/textures/item/lava_bucket.png"
    )
    lava_bucket_scaled = load_item(lava_bucket_tex, item_size)

    inclusion_any_side = load_texture(TEXTURES_DIR / "any/inclusion_side.png")
    inclusion_any_top = load_texture(TEXTURES_DIR / "any/inclusion_top.png")
    inclusion_any_iso = render_isometric_block(inclusion_any_side, inclusion_any_top, item_size)

    exclusion_any_side = load_texture(TEXTURES_DIR / "any/exclusion_side.png")
    exclusion_any_top = load_texture(TEXTURES_DIR / "any/exclusion_top.png")
    output_iso = render_isometric_block(exclusion_any_side, exclusion_any_top, item_size)

    grid = [
        inclusion_any_iso, inclusion_any_iso, inclusion_any_iso,
        inclusion_any_iso, lava_bucket_scaled, inclusion_any_iso,
        inclusion_any_iso, inclusion_any_iso, inclusion_any_iso,
    ]
    frame = compose_frame(gui, grid, output_iso)
    frame = to_gif_frame(frame)
    frame.save(SCRIPT_DIR / "exclusion_any_recipe.png")
    print(f"Generated {SCRIPT_DIR / 'exclusion_any_recipe.png'}")


def generate_typed_glass_recipe():
    """Typed glass recipe: 8 any-variant glass + type gem -> 8 typed glass.
    Animated: cycles through all 18 types x 2 variants (inclusion then exclusion).
    Each frame shows the gem in the center, surrounding glass of the appropriate base variant,
    and the typed output.
    """
    gui = load_gui_background()
    item_size = 16 * SCALE

    frames = []
    for type_name in FILTER_TYPES:
        gem_tex = load_texture(GEM_DIR / f"{type_name}_gem.png")
        gem_scaled = load_item(gem_tex, item_size)
        for variant in ("inclusion", "exclusion"):
            base_side = load_texture(TEXTURES_DIR / f"any/{variant}_side.png")
            base_top = load_texture(TEXTURES_DIR / f"any/{variant}_top.png")
            base_iso = render_isometric_block(base_side, base_top, item_size)

            out_side = load_texture(TEXTURES_DIR / f"{type_name}/{variant}_side.png")
            out_top = load_texture(TEXTURES_DIR / f"{type_name}/{variant}_top.png")
            output_iso = render_isometric_block(out_side, out_top, item_size)

            grid = [
                base_iso, base_iso, base_iso,
                base_iso, gem_scaled, base_iso,
                base_iso, base_iso, base_iso,
            ]
            frame = compose_frame(gui, grid, output_iso)
            frames.append(to_gif_frame(frame))

    save_gif(frames, SCRIPT_DIR / "typed_glass_recipe.gif", duration=800)


if __name__ == "__main__":
    generate_pocket_chow_recipe()
    generate_inclusion_any_recipe()
    generate_exclusion_any_recipe()
    generate_typed_glass_recipe()
