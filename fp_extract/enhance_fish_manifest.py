#!/usr/bin/env python3
"""
Enhance fish manifest with categories and default stats based on ID ranges.
"""

import json
from pathlib import Path

# Load existing manifest
manifest_path = Path(r"C:\Users\Shivi\Downloads\person\fp_extract\fish_manifest.json")
manifest = json.loads(manifest_path.read_text())

# Categorize by ID ranges (based on FishName enum ordering)
# These ranges correspond to fish families in Fishing Planet
categories = [
    (0, 60, "Unknown"),
    (61, 70, "Sunfish"),
    (71, 72, "Crappie"),
    (73, 78, "Shiner/Minnow"),
    (79, 79, "Freshwater Drum"),
    (81, 88, "Perch/Pikeperch"),
    (89, 89, "Ruffe"),
    (90, 99, "Carp/Tench/Bream"),
    (101, 107, "Pickerel/Pike/Muskie"),
    (111, 119, "Bass (Freshwater)"),
    (120, 126, "Catfish (North America)"),
    (501, 519, "Trout/Char/Salmonids"),
    (590, 590, "Special/Furry Trout"),
    (600, 603, "Sturgeon"),
    (701, 711, "Salmon/Whitefish/Taimen"),
    (801, 808, "Gar"),
    (900, 920, "Misc Freshwater (Tarpon, Bowfin, etc.)"),
    (921, 940, "European Cyprinids"),
    (935, 939, "Asian Carp/Permit/Bonefish"),
    (1000, 1025, "Historic/Monster Fish"),
    (1300, 1370, "South American Catfish"),
    (1400, 1520, "South American Predators"),
    (1530, 1590, "Misc Freshwater (Buffalo, Shad, Bass)"),
    (1650, 1810, "South American Exotics (Payara, Arapaima, etc.)"),
    (1940, 1990, "African Catfish"),
    (2020, 2140, "African Fish (Tigerfish, Tilapia, etc.)"),
    (2210, 2220, "Misc Small Fish"),
    (2240, 2250, "Whitefish/Omul"),
    (2284, 2289, "Tuna"),
    (2290, 2304, "Jacks/Mackerel/Billfish/Reef Fish"),
    (2307, 2310, "European Marine"),
    (2318, 2329, "Billfish/Tuna/Shark"),
    (2330, 2374, "European Marine Species"),
]

# Assign categories
for species in manifest["species"]:
    fish_id = species["id"]
    for min_id, max_id, category in categories:
        if min_id <= fish_id <= max_id:
            species["category"] = category
            break

# Add default stats based on category
category_defaults = {
    "Sunfish": {"min_weight_kg": 0.1, "max_weight_kg": 0.5, "min_length_cm": 10, "max_length_cm": 25, "habitat": "Freshwater", "depth": "Shallow", "rarity": "Common"},
    "Crappie": {"min_weight_kg": 0.2, "max_weight_kg": 1.5, "min_length_cm": 15, "max_length_cm": 35, "habitat": "Freshwater", "depth": "Shallow-Mid", "rarity": "Common"},
    "Shiner/Minnow": {"min_weight_kg": 0.01, "max_weight_kg": 0.1, "min_length_cm": 5, "max_length_cm": 15, "habitat": "Freshwater", "depth": "Shallow", "rarity": "Common"},
    "Freshwater Drum": {"min_weight_kg": 0.5, "max_weight_kg": 5, "min_length_cm": 20, "max_length_cm": 50, "habitat": "Freshwater", "depth": "Bottom", "rarity": "Uncommon"},
    "Perch/Pikeperch": {"min_weight_kg": 0.3, "max_weight_kg": 3, "min_length_cm": 15, "max_length_cm": 40, "habitat": "Freshwater", "depth": "Mid-Bottom", "rarity": "Common"},
    "Ruffe": {"min_weight_kg": 0.05, "max_weight_kg": 0.2, "min_length_cm": 8, "max_length_cm": 20, "habitat": "Freshwater", "depth": "Bottom", "rarity": "Common"},
    "Carp/Tench/Bream": {"min_weight_kg": 1, "max_weight_kg": 15, "min_length_cm": 30, "max_length_cm": 80, "habitat": "Freshwater", "depth": "Bottom", "rarity": "Common"},
    "Pickerel/Pike/Muskie": {"min_weight_kg": 1, "max_weight_kg": 20, "min_length_cm": 40, "max_length_cm": 120, "habitat": "Freshwater", "depth": "Mid-Top", "rarity": "Uncommon"},
    "Bass (Freshwater)": {"min_weight_kg": 0.5, "max_weight_kg": 10, "min_length_cm": 20, "max_length_cm": 60, "habitat": "Freshwater", "depth": "Mid", "rarity": "Common"},
    "Catfish (North America)": {"min_weight_kg": 1, "max_weight_kg": 50, "min_length_cm": 30, "max_length_cm": 150, "habitat": "Freshwater", "depth": "Bottom", "rarity": "Uncommon"},
    "Trout/Char/Salmonids": {"min_weight_kg": 0.2, "max_weight_kg": 10, "min_length_cm": 15, "max_length_cm": 80, "habitat": "Freshwater", "depth": "Mid-Top", "rarity": "Common"},
    "Special/Furry Trout": {"min_weight_kg": 0.5, "max_weight_kg": 5, "min_length_cm": 20, "max_length_cm": 50, "habitat": "Freshwater", "depth": "Mid", "rarity": "Legendary"},
    "Sturgeon": {"min_weight_kg": 5, "max_weight_kg": 200, "min_length_cm": 60, "max_length_cm": 300, "habitat": "Freshwater", "depth": "Bottom", "rarity": "Rare"},
    "Salmon/Whitefish/Taimen": {"min_weight_kg": 1, "max_weight_kg": 50, "min_length_cm": 40, "max_length_cm": 150, "habitat": "Freshwater/Anadromous", "depth": "Mid", "rarity": "Uncommon"},
    "Gar": {"min_weight_kg": 2, "max_weight_kg": 100, "min_length_cm": 60, "max_length_cm": 250, "habitat": "Freshwater", "depth": "Top-Mid", "rarity": "Uncommon"},
    "Misc Freshwater (Tarpon, Bowfin, etc.)": {"min_weight_kg": 1, "max_weight_kg": 30, "min_length_cm": 30, "max_length_cm": 100, "habitat": "Freshwater", "depth": "Mid", "rarity": "Uncommon"},
    "European Cyprinids": {"min_weight_kg": 0.1, "max_weight_kg": 5, "min_length_cm": 10, "max_length_cm": 50, "habitat": "Freshwater", "depth": "Bottom-Mid", "rarity": "Common"},
    "Asian Carp/Permit/Bonefish": {"min_weight_kg": 2, "max_weight_kg": 40, "min_length_cm": 40, "max_length_cm": 120, "habitat": "Freshwater/Brackish", "depth": "Mid", "rarity": "Uncommon"},
    "Historic/Monster Fish": {"min_weight_kg": 10, "max_weight_kg": 500, "min_length_cm": 80, "max_length_cm": 300, "habitat": "Freshwater", "depth": "Various", "rarity": "Legendary"},
    "South American Catfish": {"min_weight_kg": 5, "max_weight_kg": 200, "min_length_cm": 60, "max_length_cm": 250, "habitat": "Freshwater (Amazon)", "depth": "Bottom", "rarity": "Rare"},
    "South American Predators": {"min_weight_kg": 1, "max_weight_kg": 50, "min_length_cm": 30, "max_length_cm": 150, "habitat": "Freshwater (Amazon)", "depth": "Mid-Top", "rarity": "Uncommon"},
    "Misc Freshwater (Buffalo, Shad, Bass)": {"min_weight_kg": 1, "max_weight_kg": 20, "min_length_cm": 30, "max_length_cm": 80, "habitat": "Freshwater", "depth": "Mid", "rarity": "Common"},
    "South American Exotics (Payara, Arapaima, etc.)": {"min_weight_kg": 10, "max_weight_kg": 300, "min_length_cm": 80, "max_length_cm": 300, "habitat": "Freshwater (Amazon)", "depth": "Mid-Top", "rarity": "Legendary"},
    "African Catfish": {"min_weight_kg": 5, "max_weight_kg": 100, "min_length_cm": 50, "max_length_cm": 200, "habitat": "Freshwater (Africa)", "depth": "Bottom", "rarity": "Rare"},
    "African Fish (Tigerfish, Tilapia, etc.)": {"min_weight_kg": 1, "max_weight_kg": 50, "min_length_cm": 20, "max_length_cm": 150, "habitat": "Freshwater (Africa)", "depth": "Mid", "rarity": "Uncommon"},
    "Misc Small Fish": {"min_weight_kg": 0.01, "max_weight_kg": 0.1, "min_length_cm": 3, "max_length_cm": 10, "habitat": "Freshwater", "depth": "Shallow", "rarity": "Common"},
    "Whitefish/Omul": {"min_weight_kg": 0.5, "max_weight_kg": 3, "min_length_cm": 20, "max_length_cm": 50, "habitat": "Freshwater (Cold)", "depth": "Mid-Bottom", "rarity": "Uncommon"},
    "Tuna": {"min_weight_kg": 10, "max_weight_kg": 500, "min_length_cm": 80, "max_length_cm": 300, "habitat": "Saltwater", "depth": "Pelagic", "rarity": "Rare"},
    "Jacks/Mackerel/Billfish/Reef Fish": {"min_weight_kg": 2, "max_weight_kg": 200, "min_length_cm": 40, "max_length_cm": 300, "habitat": "Saltwater", "depth": "Pelagic/Reef", "rarity": "Uncommon"},
    "European Marine": {"min_weight_kg": 0.5, "max_weight_kg": 20, "min_length_cm": 20, "max_length_cm": 100, "habitat": "Saltwater (Europe)", "depth": "Various", "rarity": "Common"},
    "Billfish/Tuna/Shark": {"min_weight_kg": 20, "max_weight_kg": 1000, "min_length_cm": 150, "max_length_cm": 500, "habitat": "Saltwater", "depth": "Pelagic", "rarity": "Legendary"},
    "European Marine Species": {"min_weight_kg": 0.5, "max_weight_kg": 50, "min_length_cm": 20, "max_length_cm": 200, "habitat": "Saltwater (Europe)", "depth": "Various", "rarity": "Uncommon"},
    "Unknown": {"min_weight_kg": 1, "max_weight_kg": 10, "min_length_cm": 20, "max_length_cm": 60, "habitat": "Unknown", "depth": "Unknown", "rarity": "Unknown"},
}

for species in manifest["species"]:
    cat = species["category"]
    if cat in category_defaults:
        species.update(category_defaults[cat])
    else:
        species.update(category_defaults["Unknown"])

# Add Minecraft-specific fields
for species in manifest["species"]:
    species["minecraft"] = {
        "entity_id": f"fishingplanet:{species['name'].lower()}",
        "spawn_weight": 10 if species["rarity"] == "Common" else (5 if species["rarity"] == "Uncommon" else (2 if species["rarity"] == "Rare" else 1)),
        "creative_tab": "fishingplanet:fish",
        "loot_table": f"fishingplanet:fish/{species['name'].lower()}"
    }

# Save enhanced manifest
output_path = Path(r"C:\Users\Shivi\Downloads\person\fp_extract\fish_manifest_enhanced.json")
output_path.write_text(json.dumps(manifest, indent=2))
print(f"Enhanced manifest saved to {output_path}")
print(f"Total species: {len(manifest['species'])}")

# Print by category
from collections import Counter
cat_counts = Counter(s["category"] for s in manifest["species"])
for cat, count in sorted(cat_counts.items()):
    print(f"  {cat}: {count}")