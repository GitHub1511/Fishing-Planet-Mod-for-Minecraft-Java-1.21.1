#!/usr/bin/env python3
"""Generate placeholder textures for the Fishing Planet mod.

- 286 fish skins (32x32, cod-UV-safe flat bands) per species id
- 8 item icons (16x16) + mod icon (128x128)
- 8 item model JSONs
"""
import json
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
MOD = ROOT / "fishingplanet-fabric" / "src" / "main" / "resources" / "assets" / "fishingplanet"
FISH_DIR = MOD / "textures" / "entity" / "fish"
ITEM_DIR = MOD / "textures" / "item"
MODEL_DIR = MOD / "models" / "item"

CATEGORY_COLORS = {
    "Sunfish": (86, 142, 78),
    "Crappie": (110, 120, 130),
    "Shiner/Minnow": (150, 160, 170),
    "Freshwater Drum": (120, 110, 95),
    "Perch/Pikeperch": (96, 140, 82),
    "Ruffe": (130, 120, 100),
    "Carp/Tench/Bream": (148, 128, 84),
    "Pickerel/Pike/Muskie": (90, 130, 80),
    "Bass (Freshwater)": (88, 128, 74),
    "Catfish (North America)": (100, 100, 110),
    "Trout/Char/Salmonids": (150, 120, 110),
    "Special/Furry Trout": (200, 150, 200),
    "Sturgeon": (110, 105, 95),
    "Salmon/Whitefish/Taimen": (170, 130, 120),
    "Gar": (100, 125, 90),
    "Misc Freshwater (Tarpon, Bowfin, etc.)": (120, 135, 110),
    "European Cyprinids": (150, 145, 120),
    "Asian Carp/Permit/Bonefish": (140, 150, 150),
    "Historic/Monster Fish": (60, 60, 80),
    "South American Catfish": (90, 90, 100),
    "South American Predators": (150, 110, 80),
    "Misc Freshwater (Buffalo, Shad, Bass)": (120, 135, 110),
    "South American Exotics (Payara, Arapaima, etc.)": (160, 100, 70),
    "African Catfish": (95, 95, 105),
    "African Fish (Tigerfish, Tilapia, etc.)": (130, 140, 120),
    "Misc Small Fish": (160, 165, 170),
    "Whitefish/Omul": (165, 170, 175),
    "Tuna": (70, 90, 130),
    "Jacks/Mackerel/Billfish/Reef Fish": (80, 120, 150),
    "European Marine": (100, 130, 150),
    "Billfish/Tuna/Shark": (70, 85, 120),
    "European Marine Species": (100, 130, 150),
}
DEFAULT_COLOR = (120, 135, 110)
BELLY = (232, 228, 218)


def shade(rgb, factor):
    return tuple(max(0, min(255, int(c * factor))) for c in rgb)


def make_fish_texture(species):
    base = CATEGORY_COLORS.get(species["category"], DEFAULT_COLOR)
    jitter = 0.92 + 0.16 * ((species["id"] * 2654435761 % 1000) / 1000.0)
    base = shade(base, jitter)
    back = shade(base, 0.65)
    mid = base
    stripe = shade(base, 1.25)
    tail = shade(base, 0.55)
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    px = img.load()
    for y in range(32):
        for x in range(32):
            if y < 12:
                c = back
            elif y < 20:
                c = mid
            elif y < 24:
                c = stripe
            else:
                c = BELLY
            if x >= 26:
                c = tail
            px[x, y] = c + (255,)
    return img


def draw_rod(d):
    d.line([(2, 13), (13, 2)], fill=(139, 90, 43, 255), width=2)
    d.ellipse([3, 9, 7, 13], fill=(90, 90, 90, 255))
    d.ellipse([4, 10, 6, 12], fill=(180, 180, 180, 255))


def draw_reel(d):
    d.ellipse([2, 2, 13, 13], fill=(70, 70, 75, 255))
    d.ellipse([5, 5, 10, 10], fill=(170, 170, 175, 255))
    d.ellipse([7, 7, 8, 8], fill=(50, 50, 55, 255))
    d.line([(11, 11), (14, 14)], fill=(150, 150, 150, 255), width=2)


def draw_line(d):
    d.rectangle([3, 5, 12, 10], fill=(0, 128, 128, 255))
    d.rectangle([4, 6, 11, 9], fill=(120, 220, 220, 255))
    d.line([(0, 12), (15, 12)], fill=(150, 230, 150, 255), width=1)
    d.line([(12, 12), (12, 15)], fill=(150, 230, 150, 255), width=1)


def draw_hook(d):
    d.line([(8, 1), (8, 10)], fill=(160, 160, 165, 255), width=2)
    d.arc([4, 7, 11, 14], start=200, end=340, fill=(160, 160, 165, 255), width=2)
    d.line([(8, 1), (8, 3)], fill=(160, 160, 165, 255), width=3)


def draw_lure(d):
    d.ellipse([1, 6, 10, 10], fill=(235, 235, 235, 255))
    d.pieslice([1, 6, 10, 10], start=180, end=360, fill=(200, 60, 50, 255))
    d.polygon([(10, 4), (10, 11), (14, 7)], fill=(200, 60, 50, 255))
    d.ellipse([3, 7, 4, 8], fill=(20, 20, 20, 255))


def draw_fish(d):
    d.ellipse([1, 5, 11, 10], fill=(70, 120, 160, 255))
    d.polygon([(11, 3), (11, 12), (15, 7)], fill=(50, 90, 130, 255))
    d.polygon([(4, 5), (7, 5), (5, 2)], fill=(50, 90, 130, 255))
    d.ellipse([2, 6, 4, 8], fill=(255, 255, 255, 255))
    d.ellipse([3, 6, 4, 7], fill=(10, 10, 10, 255))


def draw_bobber(d):
    d.ellipse([3, 1, 12, 8], fill=(210, 50, 40, 255))
    d.ellipse([3, 7, 12, 14], fill=(240, 240, 240, 255))
    d.rectangle([3, 7, 12, 8], fill=(30, 30, 30, 255))
    d.line([(7, 0), (7, 2)], fill=(30, 30, 30, 255), width=2)


def draw_tackle_box(d):
    d.rectangle([1, 5, 14, 13], fill=(40, 110, 60, 255))
    d.rectangle([1, 5, 14, 8], fill=(60, 140, 80, 255))
    d.rectangle([7, 6, 8, 11], fill=(180, 180, 180, 255))


ITEM_DRAWERS = {
    "rod": draw_rod,
    "reel": draw_reel,
    "line": draw_line,
    "hook": draw_hook,
    "lure": draw_lure,
    "fish": draw_fish,
    "bobber": draw_bobber,
    "tackle_box": draw_tackle_box,
}


def main():
    FISH_DIR.mkdir(parents=True, exist_ok=True)
    ITEM_DIR.mkdir(parents=True, exist_ok=True)
    MODEL_DIR.mkdir(parents=True, exist_ok=True)

    manifest = json.loads((ROOT / "fp_extract" / "fish_manifest_enhanced.json").read_text())
    count = 0
    for species in manifest["species"]:
        img = make_fish_texture(species)
        img.save(FISH_DIR / f"{species['id']}.png")
        count += 1
    print(f"fish textures: {count}")

    for name, drawer in ITEM_DRAWERS.items():
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        drawer(ImageDraw.Draw(img))
        img.save(ITEM_DIR / f"{name}.png")
        parent = "minecraft:item/handheld" if name == "rod" else "minecraft:item/generated"
        model = {"parent": parent, "textures": {"layer0": f"fishingplanet:item/{name}"}}
        (MODEL_DIR / f"{name}.json").write_text(json.dumps(model, indent=2))
    print(f"item textures+models: {len(ITEM_DRAWERS)}")

    fish_icon = Image.open(ITEM_DIR / "fish.png").convert("RGBA")
    fish_icon.resize((128, 128), Image.NEAREST).save(MOD / "icon.png")
    print("mod icon written")


if __name__ == "__main__":
    main()
