# M0 Completion Report

## Toolchain Status

| Tool | Version | Status | Location |
|------|---------|--------|----------|
| JDK 21 | Temurin 21.0.5+11 | ✅ Working | `C:\Users\Shivi\Downloads\person\tools\jdk\jdk-21.0.5+11` |
| Gradle | 9.7.1 | ⚠️ Native lib issue | `C:\Users\Shivi\Downloads\person\tools\gradle\gradle-9.7.1` |
| .NET SDK | 8.0.404 | ✅ Working | `C:\Users\Shivi\Downloads\person\tools\dotnet` |
| Python | 3.14 | ✅ Working | System |
| universal-modder | 0.2.0 | ✅ Installed | `pip install -e tools/universal-modder` |

**Note:** Gradle 9.7.1 has a native library issue on Windows 11 (`native-platform.dll`). Will use Gradle Wrapper in the Fabric mod project instead (standard practice).

## Universal Modder Audit

| Check | Result |
|-------|--------|
| Telemetry | ❌ None found |
| Auto-update | ❌ None found |
| Obfuscation | ❌ None found |
| Dependencies | pillow, numpy, pyyaml only |
| Safe commands | `um scan`, `um backup`, `um publish check` ✅ |
| Unsafe commands | `um win`, `um fal`, `um comfy`, `um kb sync/pr` ⚠️ Blocked |

**Verdict:** Safe to use in isolated venv for `scan`, `backup`, `publish check` only.

## Fishing Planet Scan Results

```
Fishing Planet (steam 380600)
  path:      C:\Program Files (x86)\Steam\steamapps\common\Fishing Planet
  engine:    Unity (IL2CPP) [100%] evidence: UnityPlayer + GameAssembly / global-metadata.dat
             data_dir=fishingplanet_data, version=6000.3.10f1, company=Fishing Planet LLC
  anti-cheat: none found
  loaders:   none installed
  saves:     C:\Users\Shivi\AppData\LocalLow\Fishing Planet LLC\FishingPlanet
  routes:
    1. BepInEx 6 (IL2CPP) or MelonLoader; recover types with Cpp2IL / Il2CppDumper
    2. Proxy-DLL loader + function hooks (fallback)
```

**Extraction feasible:** Unity 6000.3.10, IL2CPP, no anti-cheat. Cpp2IL/Il2CppDumper can recover types from `GameAssembly.dll` + `global-metadata.dat`.

## Backup Complete

```
C:\Users\Shivi\Downloads\person\backups\20261007-184610\
├── mods\           (empty - mods managed by Lunar Client)
├── shaderpacks\    (Bliss, BSL, Complementary, Kappa, Optimized Prime)
├── resourcepacks\  (Destiny 128x, Optimum Realism R4.1.2 64x)
└── config\         (all .minecraft/config files)
```

## Lunar Client Modpack Matrix (1.21.1 Profile)

**Location:** `C:\Users\Shivi\.lunarclient\profiles\1.21\mods\fabric-1.21.1\`

**Core/Performance (8):**
- fabric-api-0.116.17+1.21.1.jar
- sodium-fabric-0.6.9+mc1.21.1.jar
- lithium-fabric-0.15.4+mc1.21.1.jar
- ferritecore-7.0.3-fabric.jar
- immediatelyfast-1.6.14+1.21.1.jar
- noxesium-2.2.0.jar.disabled
- indium-1.0.35+mc1.21.jar.disabled
- euphoriapatcher-1.8.6-r5.7.1-fabric.jar.disabled

**Shaders/Rendering (2):**
- iris-fabric-1.8.7+mc1.21.1.jar
- indium (disabled)

**Worldgen (11):**
- Terralith_1.21.x_v2.5.8.jar
- BiomesOPlenty-fabric-1.21.1-21.1.0.13.jar
- regions_unexplored-fabric-1.21.1-0.5.9.jar
- naturespirit-2.2.5-1.21.1.jar
- ecologics-fabric-1.21.1-2.3.3.jar
- tectonic-3.0.28-fabric-21.1.jar
- streamsreflowing-1.21.1-fabric-2.14.4.jar
- better-end-21.0.11.jar
- better-nether-21.0.11.jar
- bclib-21.0.13.jar
- terrablender-1.21.1-4.1.0.8.jar

**Quality of Life (9):**
- emi-1.1.22+1.21.1+fabric.jar
- jei-1.21.1-fabric-19.51.0.418.jar
- roughlyenoughitems-16.0.799-fabric.jar
- xaerominimap-fabric-1.21.1-25.3.10.jar
- xaeroworldmap-fabric-1.21.1-1.40.11.jar
- modmenu-11.0.5.jar
- cloth-config-15.0.140-fabric.jar
- yet_another_config_lib_v3-3.8.2+1.21.1-fabric.jar
- controlify-3.0.0-beta.2+1.21.1-fabric.jar

**Other (25+):**
- collective, cristellib, distanthorizons, alcocraftplus, physics-mod, treeharvester (disabled), veinminer (disabled), ore-vein-miner (disabled), fallingtree (disabled), hobbit_hill_village, lios_overhauled_villages, spiral_tower_village, worldweaver, silk, silk-api, placeholder-api, open-parties-and-claims, nirvana, fabric-carpet, fabric-language-kotlin, architectury, glitchcore, treeharvester, etc.

**Total: ~55 mods** (including disabled)

## Compatibility Notes for Our Mod

1. **Fabric API 0.116.17** - Target this version
2. **Sodium + Iris** - Must use Fabric Rendering API, avoid raw GL calls
3. **Lithium** - Don't break entity AI optimizations
4. **Indium** (disabled) - If user enables, need Indium compatibility for custom entity rendering
5. **Worldgen mods** - Fish spawner must work with Terralith, BOP, Regions Unexplored, Nature's Spirit biomes
5. **EMI/JEI/REI** - Items must register properly for recipe viewers
6. **Cloth Config / YACL** - Use for config GUI
7. **Mod Menu** - Mod will appear automatically

## Next: M1 - Data Extraction

Ready to begin Fishing Planet asset extraction using Cpp2IL/Il2CppDumper.