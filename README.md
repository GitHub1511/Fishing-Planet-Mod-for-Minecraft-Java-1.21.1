# Fishing Planet Mod for Minecraft Java 1.21.1 (Fabric)

Brings Fishing Planet's fish, rods, reels, lures and tackle into Minecraft 1.21.1.

- **286 fish species** extracted from Fishing Planet (data-driven, no hand-written class per fish)
- **30 rods / 20 reels / 45 lines / 128 hooks / 180 lures** across Float, Jig, Lure, Bottom, Feeder and Spod types
- **All water bodies spawn fish** — vanilla biomes plus Terralith, Biomes O' Plenty, Regions Unexplored, Nature's Spirit
- **Commands**: `/fp give`, `/fp spawn`, `/fp frequency`, `/fp density`, `/fp list`
- **Config** via Cloth Config (spawn rate, density, catch chance, compatibility toggles)
- **Compatible with**: Fabric Loader 0.19.5, Fabric API 0.116.17, Iris, Sodium, Lunar Client, Bliss shaders, Optimum Realism

## Build (GitHub Actions, Option 1)

Every push to `main` builds the mod on Ubuntu with Temurin JDK 21 and uploads the jar as an artifact.

1. Go to the repo's **Actions** tab → latest **Build Mod** run → **Artifacts** → download `fishingplanet-fabric`
2. You get `fishingplanet-fabric-1.0.0.jar`

No local JDK/Gradle needed — Windows Java 21 ZIP-filesystem issue is bypassed by building on Linux.

## Install (Lunar Client 1.21.1)

Copy the built jar to **both** of these:

1. Lunar general mods folder
2. Lunar 1.21.1 profile mods folder (e.g. `profiles\1.21\mods\fabric-1.21.1`)

Then launch via Lunar Client (Fabric 0.19.5 profile) and verify in Mod Menu.

## Use

- `/fp give rod` — give yourself tackle (rod, reel, line, hook, lure, bobber, tackle_box, or any fish name)
- `/fp spawn largemouth_bass 5 10` — spawn 5 fish within radius 10
- `/fp frequency 2.0` — global spawn rate 0.1x–10x
- `/fp density 80` — max fish per chunk
- `/fp list` — list all 286 species
- `/fp reload` — reload config

## Layout

- `fishingplanet-fabric/` — the Fabric mod (Java 21, Loom, Yarn 1.21.1+build.3)
- `fp_extract/` — extraction scripts + JSON manifests (fish + tackle)
- `FISHING_PLANET_MC_MOD_PLAN.md` — full plan (M0–M7)
- `M0_COMPLETION.md` — environment verification + modpack matrix
- `BUILD_WORKAROUND.md` — why Windows builds fail and the Linux/CI fix
