#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Installs the built Fishing Planet mod jar into Lunar Client mod folders.

.DESCRIPTION
    Copies fishingplanet-fabric-1.0.0.jar to BOTH:
      1. C:\Users\Shivi\.lunarclient\profiles\1.21\mods\               (profile mod folder)
      2. C:\Users\Shivi\.lunarclient\profiles\1.21\mods\fabric-1.21.1\ (1.21.1 version folder)

    Usage:
      1. Download the jar from GitHub: repo Actions tab > latest "Build Mod" run > Artifacts > fishingplanet-fabric
      2. Put the jar in your Downloads folder (or pass -JarPath explicitly)
      3. Run: powershell -ExecutionPolicy Bypass -File install_to_lunar.ps1
#>
param(
    [string]$JarPath = ""
)

$ErrorActionPreference = "Stop"

$profileMods = "C:\Users\Shivi\.lunarclient\profiles\1.21\mods"
$versionMods = "C:\Users\Shivi\.lunarclient\profiles\1.21\mods\fabric-1.21.1"

# Find the jar if not given explicitly
if ([string]::IsNullOrWhiteSpace($JarPath)) {
    $cands = @(
        (Join-Path $env:USERPROFILE "Downloads\fishingplanet-fabric-1.0.0.jar"),
        (Join-Path $env:USERPROFILE "Downloads\fishingplanet-fabric\fishingplanet-fabric-1.0.0.jar")
    )
    foreach ($c in $cands) {
        if (Test-Path $c) { $JarPath = $c; break }
    }
    if ([string]::IsNullOrWhiteSpace($JarPath)) {
        $found = Get-ChildItem (Join-Path $env:USERPROFILE "Downloads") -Filter "fishingplanet*.jar" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) { $JarPath = $found.FullName }
    }
}

if (-not (Test-Path $JarPath)) {
    Write-Error "Mod jar not found. Download it from GitHub Actions artifacts into your Downloads folder, or pass -JarPath explicitly.`nExample: .\install_to_lunar.ps1 -JarPath 'C:\Users\Shivi\Downloads\fishingplanet-fabric-1.0.0.jar'"
    exit 1
}

Write-Host "Using jar: $JarPath" -ForegroundColor Cyan
$jarName = Split-Path $JarPath -Leaf

foreach ($dest in @($profileMods, $versionMods)) {
    if (-not (Test-Path $dest)) {
        Write-Error "Lunar folder missing: $dest. Launch Lunar Client 1.21.1 once first."
        exit 1
    }
    Copy-Item $JarPath (Join-Path $dest $jarName) -Force
    Write-Host "Installed -> $dest\$jarName" -ForegroundColor Green
}

Write-Host "`nDone. Launch Lunar Client (1.21.1, Fabric 0.19.5 profile) and check Mod Menu for 'Fishing Planet'." -ForegroundColor Green
