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
    $dl = Join-Path $env:USERPROFILE "Downloads"
    # 1. jar directly in Downloads
    $found = Get-ChildItem $dl -Filter "fishingplanet*.jar" -File -ErrorAction SilentlyContinue | Select-Object -First 1
    # 2. jar inside an extracted artifact subfolder
    if (-not $found) {
        $found = Get-ChildItem $dl -Filter "fishingplanet*.jar" -File -Recurse -Depth 2 -ErrorAction SilentlyContinue | Select-Object -First 1
    }
    # 3. unextracted artifact zip -> expand to temp, then find jar
    if (-not $found) {
        $zip = Get-ChildItem $dl -Filter "fishingplanet*.zip" -File -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($zip) {
            Write-Host "Found artifact zip, extracting: $($zip.Name)" -ForegroundColor Yellow
            $tmp = Join-Path ([System.IO.Path]::GetTempPath()) "fp-mod-install"
            if (Test-Path $tmp) { Remove-Item $tmp -Recurse -Force }
            Expand-Archive $zip.FullName $tmp -Force
            $found = Get-ChildItem $tmp -Filter "fishingplanet*.jar" -File -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
        }
    }
    if ($found) { $JarPath = $found.FullName }
}

if ([string]::IsNullOrWhiteSpace($JarPath) -or -not (Test-Path $JarPath)) {
    Write-Error "Mod jar not found. Did the GitHub Actions 'Build Mod' run finish green? Download the 'fishingplanet-fabric' artifact into your Downloads folder (zip is fine, it will be extracted automatically), then re-run this script.`nExample: .\install_to_lunar.ps1 -JarPath 'C:\Users\Shivi\Downloads\fishingplanet-fabric-1.0.0.jar'"
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
