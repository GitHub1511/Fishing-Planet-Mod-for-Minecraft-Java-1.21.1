#!/usr/bin/env python3
"""
Create tackle manifests for rods, reels, lures, lines, hooks based on Fishing Planet data.
"""

import json
from pathlib import Path

# Fishing types from RodInventoryPaterns enum
rod_types = [
    {"id": 0, "name": "Float", "description": "Float fishing rod", "power": "Light", "action": "Slow", "length_m": 4.5, "price": 5000},
    {"id": 1, "name": "Jig", "description": "Jigging rod", "power": "Medium", "action": "Fast", "length_m": 2.4, "price": 8000},
    {"id": 2, "name": "Lure", "description": "Lure casting rod", "power": "Medium-Heavy", "action": "Fast", "length_m": 2.1, "price": 10000},
    {"id": 3, "name": "Bottom", "description": "Bottom fishing rod", "power": "Heavy", "action": "Moderate", "length_m": 3.6, "price": 12000},
    {"id": 4, "name": "Feeder", "description": "Feeder rod", "power": "Medium", "action": "Moderate", "length_m": 3.9, "price": 15000},
    {"id": 5, "name": "Spod", "description": "Spod/Marker rod", "power": "Heavy", "action": "Fast", "length_m": 3.6, "price": 20000},
]

# Line materials from LineMaterialTypes enum
line_materials = [
    {"id": 0, "name": "Mono", "description": "Monofilament line", "stretch": "High", "visibility": "Medium", "abrasion": "Medium"},
    {"id": 1, "name": "Fluoro", "description": "Fluorocarbon line", "stretch": "Low", "visibility": "Low", "abrasion": "High"},
    {"id": 2, "name": "Braid", "description": "Braided line", "stretch": "None", "visibility": "High", "abrasion": "Very High"},
]

# Reel types (inferred from game mechanics)
reel_types = [
    {"id": 0, "name": "Spinning", "description": "Spinning reel", "gear_ratio": 5.2, "bearings": 5, "drag_kg": 8, "line_capacity_m": 150},
    {"id": 1, "name": "Baitcasting", "description": "Baitcasting reel", "gear_ratio": 6.4, "bearings": 7, "drag_kg": 10, "line_capacity_m": 120},
    {"id": 2, "name": "Conventional", "description": "Conventional/Trolling reel", "gear_ratio": 4.1, "bearings": 4, "drag_kg": 15, "line_capacity_m": 300},
    {"id": 3, "name": "Fly", "description": "Fly reel", "gear_ratio": 1.0, "bearings": 2, "drag_kg": 5, "line_capacity_m": 50},
]

# Hook sizes (standard fishing hook sizes)
hook_sizes = [
    {"size": 10, "description": "Size 10 - Small panfish", "wire": "Fine", "gap_mm": 4.8},
    {"size": 8, "description": "Size 8 - Small panfish/trout", "wire": "Fine", "gap_mm": 5.9},
    {"size": 6, "description": "Size 6 - Trout/panfish", "wire": "Fine", "gap_mm": 7.1},
    {"size": 4, "description": "Size 4 - Bass/walleye", "wire": "Medium", "gap_mm": 8.9},
    {"size": 2, "description": "Size 2 - Bass/pike", "wire": "Medium", "gap_mm": 10.7},
    {"size": 1, "description": "Size 1 - Bass/pike/catfish", "wire": "Medium", "gap_mm": 12.3},
    {"size": 0, "description": "Size 1/0 - Large bass/pike", "wire": "Heavy", "gap_mm": 13.9},
    {"size": -1, "description": "Size 2/0 - Large pike/muskie", "wire": "Heavy", "gap_mm": 15.5},
    {"size": -2, "description": "Size 3/0 - Muskie/catfish", "wire": "Heavy", "gap_mm": 17.1},
    {"size": -3, "description": "Size 4/0 - Large catfish/saltwater", "wire": "Heavy", "gap_mm": 19.1},
    {"size": -4, "description": "Size 5/0 - Large saltwater", "wire": "Heavy", "gap_mm": 21.0},
    {"size": -5, "description": "Size 6/0 - Shark/large game", "wire": "Heavy", "gap_mm": 23.0},
    {"size": -6, "description": "Size 7/0 - Shark/large game", "wire": "Heavy", "gap_mm": 25.0},
    {"size": -7, "description": "Size 8/0 - Shark/large game", "wire": "Heavy", "gap_mm": 27.0},
    {"size": -8, "description": "Size 9/0 - Shark/large game", "wire": "Heavy", "gap_mm": 29.0},
    {"size": -9, "description": "Size 10/0 - Shark/large game", "wire": "Heavy", "gap_mm": 31.0},
]

# Lure categories (based on filter classes found)
lure_categories = [
    {"category": "Crankbait", "description": "Diving hard bait", "target": "Bass, Pike, Walleye"},
    {"category": "Jerkbait", "description": "Suspending/twitch bait", "target": "Bass, Pike, Trout"},
    {"category": "Spinnerbait", "description": "Wire-frame spinner", "target": "Bass, Pike, Muskie"},
    {"category": "Buzzbait", "description": "Surface buzz bait", "target": "Bass, Pike"},
    {"category": "Jig", "description": "Lead head jig", "target": "All species"},
    {"category": "Soft Plastic", "description": "Worm/creature/swimbait", "target": "All species"},
    {"category": "Spoon", "description": "Metal spoon", "target": "Trout, Pike, Salmon"},
    {"category": "Spinner", "description": "Inline spinner", "target": "Trout, Panfish, Bass"},
    {"category": "Topwater", "description": "Surface popper/walker", "target": "Bass, Pike, Muskie"},
    {"category": "Swimbait", "description": "Soft/hard swimbait", "target": "Bass, Pike, Muskie"},
    {"category": "Feeder", "description": "Feeder cage/bait", "target": "Carp, Bream, Catfish"},
    {"category": "Float Bait", "description": "Float fishing bait", "target": "Panfish, Trout, Carp"},
]

# Generate specific rod items (multiple per type for progression)
rods = []
rod_names = {
    "Float": ["Telescopic Float Rod", "Match Float Rod", "Bolognese Rod", "Carbon Float Rod", "Pro Match Rod"],
    "Jig": ["Micro Jig Rod", "Light Jig Rod", "Medium Jig Rod", "Heavy Jig Rod", "Titanium Jig Rod"],
    "Lure": ["Ultralight Spin Rod", "Light Spin Rod", "Medium Spin Rod", "Heavy Spin Rod", "XH Spin Rod"],
    "Bottom": ["Ledger Rod", "Beachcaster", "Heavy Bottom Rod", "Surf Rod", "Distance Casting Rod"],
    "Feeder": ["Light Feeder Rod", "Method Feeder Rod", "Heavy Feeder Rod", "Competition Feeder", "Distance Feeder"],
    "Spod": ["Spod Rod", "Marker Rod", "Heavy Spod Rod", "Spomb Rod", "Pro Spod Rod"],
}

for rt in rod_types:
    type_name = rt["name"]
    names = rod_names.get(type_name, [f"{type_name} Rod"])
    for i, name in enumerate(names):
        tier = i + 1
        rods.append({
            "id": f"rod_{type_name.lower()}_{tier}",
            "name": name,
            "type": type_name,
            "tier": tier,
            "power": rt["power"],
            "action": rt["action"],
            "length_m": round(rt["length_m"] + (i * 0.15), 2),
            "weight_g": 150 + (tier * 30) + (i * 10),
            "price": rt["price"] * tier,
            "casting_weight_g": {"Light": (2, 10), "Medium": (10, 30), "Medium-Heavy": (15, 40), "Heavy": (40, 100)}.get(rt["power"], (10, 30)),
            "line_rating_kg": {"Light": (2, 6), "Medium": (6, 12), "Medium-Heavy": (8, 15), "Heavy": (12, 25)}.get(rt["power"], (6, 12)),
            "description": rt["description"],
            "unlock_level": tier * 5,
        })

# Generate reel items
reels = []
reel_names = {
    "Spinning": ["1000 Size Spinning", "2500 Size Spinning", "3000 Size Spinning", "4000 Size Spinning", "5000 Size Spinning"],
    "Baitcasting": ["Low Profile Baitcaster", "Round Baitcaster", "High Speed Baitcaster", "Power Baitcaster", "Tournament Baitcaster"],
    "Conventional": ["Small Conventional", "Medium Conventional", "Large Conventional", "Big Game Conventional", "Trolling Reel"],
    "Fly": ["1/2 Weight Fly Reel", "3/4 Weight Fly Reel", "5/6 Weight Fly Reel", "7/8 Weight Fly Reel", "9/10 Weight Fly Reel"],
}

for rt in reel_types:
    type_name = rt["name"]
    names = reel_names.get(type_name, [f"{type_name} Reel"])
    for i, name in enumerate(names):
        tier = i + 1
        reels.append({
            "id": f"reel_{type_name.lower()}_{tier}",
            "name": name,
            "type": type_name,
            "tier": tier,
            "gear_ratio": round(rt["gear_ratio"] + (i * 0.3), 1),
            "bearings": rt["bearings"] + tier,
            "drag_kg": rt["drag_kg"] + (tier * 2),
            "line_capacity_m": rt["line_capacity_m"] + (tier * 20),
            "weight_g": 200 + (tier * 50),
            "price": 3000 * tier * (2 if type_name == "Baitcasting" else 1),
            "description": rt["description"],
            "unlock_level": tier * 5,
        })

# Generate line items
lines = []
line_tests = [2, 4, 6, 8, 10, 12, 15, 20, 25, 30, 40, 50, 60, 80, 100]  # lb test
for lm in line_materials:
    mat_name = lm["name"]
    for test in line_tests:
        diameter_mm = round(0.15 * (test ** 0.5) * (1.0 if mat_name == "Mono" else (0.9 if mat_name == "Fluoro" else 0.6)), 2)
        lines.append({
            "id": f"line_{mat_name.lower()}_{test}lb",
            "name": f"{test}lb {mat_name}",
            "material": mat_name,
            "test_lb": test,
            "test_kg": round(test * 0.454, 1),
            "diameter_mm": diameter_mm,
            "stretch": lm["stretch"],
            "visibility": lm["visibility"],
            "abrasion": lm["abrasion"],
            "price": test * 50 * (1 if mat_name == "Mono" else (2 if mat_name == "Fluoro" else 3)),
            "length_m": 300 if test <= 20 else 150,
            "description": lm["description"],
            "unlock_level": max(1, test // 5),
        })

# Generate hook items
hooks = []
hook_types = ["Aberdeen", "Baitholder", "Circle", "Octopus", "Wide Gap", "Treble", "Siwash", "Jig Hook"]
for hs in hook_sizes:
    size = hs["size"]
    size_str = f"{abs(size)}/0" if size <= 0 else str(size)
    for ht in hook_types:
        hooks.append({
            "id": f"hook_{ht.lower().replace(' ', '_')}_{size_str.replace('/', '_')}",
            "name": f"{ht} Size {size_str}",
            "type": ht,
            "size": size,
            "size_str": size_str,
            "wire": hs["wire"],
            "gap_mm": hs["gap_mm"],
            "price": 50 + (abs(size) * 10),
            "description": hs["description"],
            "unlock_level": max(1, abs(size) // 2),
        })

# Generate lure items (representative set)
lures = []
lure_colors = ["Natural", "Chartreuse", "Firetiger", "White", "Black", "Blue", "Red", "Green Pumpkin", "Watermelon", "Junebug"]
lure_sizes = ["1/32 oz", "1/16 oz", "1/8 oz", "1/4 oz", "3/8 oz", "1/2 oz", "3/4 oz", "1 oz", "1.5 oz", "2 oz"]

lure_id = 0
for lc in lure_categories:
    cat = lc["category"]
    # Create 3-5 variants per category
    for i in range(min(5, len(lure_sizes))):
        size = lure_sizes[i]
        for color in lure_colors[:3]:  # 3 colors per size
            lure_id += 1
            lures.append({
                "id": f"lure_{cat.lower().replace(' ', '_')}_{lure_id}",
                "name": f"{color} {cat} {size}",
                "category": cat,
                "size": size,
                "color": color,
                "target": lc["target"],
                "price": 200 + (i * 100) + (lure_id % 50),
                "depth_m": i + 1,
                "action": "Wobble" if "Crank" in cat else ("Dart" if "Jerk" in cat else "Swim"),
                "description": lc["description"],
                "unlock_level": i + 1,
            })
            if lure_id >= 200:  # Limit to ~200 lures
                break
        if lure_id >= 200:
            break
    if lure_id >= 200:
        break

# Save all manifests
manifests = {
    "rods": rods,
    "reels": reels,
    "lines": lines,
    "hooks": hooks,
    "lures": lures,
}

for key, items in manifests.items():
    output_path = Path(f"C:\\Users\\Shivi\\Downloads\\person\\fp_extract\\{key}_manifest.json")
    output_path.write_text(json.dumps({
        "version": "1.0",
        "source": "Fishing Planet (inferred from game data)",
        "extraction_date": "2026-10-07",
        "count": len(items),
        "items": items
    }, indent=2))
    print(f"{key}: {len(items)} items saved to {output_path}")

print("\nSummary:")
for key, items in manifests.items():
    print(f"  {key}: {len(items)}")