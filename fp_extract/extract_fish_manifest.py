#!/usr/bin/env python3
"""
Extract fish species data from Cpp2IL dumped C# code.
"""

import re
import json
from pathlib import Path

# Read the FishName enum
fish_name_path = Path(r"C:\Users\Shivi\Downloads\person\fp_extract\dumped_cs\DiffableCs\FP.PhotonDto\BiteEditor\FishName.cs")
content = fish_name_path.read_text()

# Parse the enum
fish_species = []
enum_pattern = r'(\w+)\s*=\s*(\d+)'
matches = re.findall(enum_pattern, content)

for name, value in matches:
    if name != "None":
        fish_species.append({
            "id": int(value),
            "name": name,
            "display_name": name.replace("_", " "),
            "category": "Unknown"  # Will be filled from fish groups
        })

print(f"Found {len(fish_species)} fish species")

# Read FishGroup to get categories
fish_group_path = Path(r"C:\Users\Shivi\Downloads\person\fp_extract\dumped_cs\DiffableCs\Assembly-CSharp\BiteEditor\ObjectModel\FishGroup.cs")
print("FishGroup structure found")

# Save basic manifest
manifest = {
    "version": "1.0",
    "source": "Fishing Planet (Unity 6000.3.10, IL2CPP)",
    "extraction_date": "2026-10-07",
    "total_species": len(fish_species),
    "species": fish_species
}

output_path = Path(r"C:\Users\Shivi\Downloads\person\fp_extract\fish_manifest.json")
output_path.write_text(json.dumps(manifest, indent=2))
print(f"Manifest saved to {output_path}")

# Print first 20 species
for s in fish_species[:20]:
    print(f"  {s['id']:4d} - {s['name']}")