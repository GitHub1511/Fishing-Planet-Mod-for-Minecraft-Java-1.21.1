#!/usr/bin/env pwsh
<# 
.SYNOPSIS
    Pushes the Fishing Planet Minecraft mod to GitHub repository.

.DESCRIPTION
    Initializes git, adds all necessary files, commits, and pushes to:
    https://github.com/GitHub1511/Fishing-Planet-Mod-for-Minecraft-Java-1.21.1.git
#>

Set-Location "C:\Users\Shivi\Downloads\person"

Write-Host "=== Fishing Planet Mod - GitHub Push ===" -ForegroundColor Cyan

# Check if git is available
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error "Git is not installed or not in PATH"
    exit 1
}

# Initialize git if needed
if (-not (Test-Path ".git")) {
    Write-Host "Initializing git repository..." -ForegroundColor Yellow
    git init
    git branch -M main
}

# Add remote origin
Write-Host "Setting up remote origin..." -ForegroundColor Yellow
if (git remote get-url origin 2>$null) {
    git remote set-url origin "https://github.com/GitHub1511/Fishing-Planet-Mod-for-Minecraft-Java-1.21.1.git"
} else {
    git remote add origin "https://github.com/GitHub1511/Fishing-Planet-Mod-for-Minecraft-Java-1.21.1.git"
}

# Create .gitignore
Write-Host "Creating .gitignore..." -ForegroundColor Yellow
@"
# Gradle
.gradle/
build/
gradle/wrapper/gradle-wrapper.jar

# IDE
.idea/
.vscode/
*.iml
*.ipr
*.iws

# OS
.DS_Store
Thumbs.db

# Local caches
gradle_cache/
mc_cache/
mc_repo/
tools/
fp_dummy/
fp_extract/dumped*/
fp_extract/extracted/

# Backup files
*.backup
*.bak
backups/

# Logs
*.log

# Temp files
*.tmp
*.temp
"@ | Out-File -FilePath ".gitignore" -Encoding UTF8

# Files to include (the mod and documentation)
$filesToAdd = @(
    "fishingplanet-fabric/",
    "fp_extract/",
    "FISHING_PLANET_MC_MOD_PLAN.md",
    "M0_COMPLETION.md",
    "BUILD_WORKAROUND.md",
    ".gitignore"
)

Write-Host "Adding files to git..." -ForegroundColor Yellow
foreach ($file in $filesToAdd) {
    if (Test-Path $file) {
        git add $file
        Write-Host "  Added: $file" -ForegroundColor Green
    } else {
        Write-Warning "  Not found: $file"
    }
}

# Show status
Write-Host "`nGit status:" -ForegroundColor Cyan
git status --short

# Commit
Write-Host "`nCommitting..." -ForegroundColor Yellow
$commitMessage = @"
Add Fishing Planet Minecraft 1.21.1 Fabric Mod

- 286 fish species from Fishing Planet (data-driven)
- 30 rods, 20 reels, 45 lines, 128 hooks, 180 lures
- Custom fish entities with species-specific stats
- Tackle system: rod + reel + line + lure + hook combinations
- Biome-aware fish spawner (vanilla + modded biomes)
- Commands: /fp give, /fp spawn, /fp frequency, /fp density, /fp list
- Config via Cloth Config API
- Compatible with: Fabric 0.19.5, Iris, Sodium, Lunar Client
- Worldgen mods: Terralith, Biomes O' Plenty, Regions Unexplored, Nature's Spirit
- Shader compatibility: Bliss, Optimum Realism

Built for Minecraft 1.21.1 with Fabric Loader 0.19.5
"@

git commit -m $commitMessage

# Push
Write-Host "`nPushing to GitHub..." -ForegroundColor Yellow
try {
    git push -u origin main
    Write-Host "`n✅ Successfully pushed to GitHub!" -ForegroundColor Green
    Write-Host "Repository: https://github.com/GitHub1511/Fishing-Planet-Mod-for-Minecraft-Java-1.21.1" -ForegroundColor Cyan
} catch {
    Write-Error "Push failed. You may need to authenticate first."
    Write-Host "Try: gh auth login  (if you have GitHub CLI)" -ForegroundColor Yellow
    Write-Host "Or use a Personal Access Token when prompted." -ForegroundColor Yellow
    exit 1
}