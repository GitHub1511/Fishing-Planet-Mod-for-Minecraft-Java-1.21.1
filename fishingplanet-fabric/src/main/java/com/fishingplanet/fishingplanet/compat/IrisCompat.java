package com.fishingplanet.fishingplanet.compat;

import net.fabricmc.loader.api.FabricLoader;

public class IrisCompat {
    private static boolean irisLoaded = false;
    private static boolean sodiumLoaded = false;
    private static boolean blissLoaded = false;

    public static void register() {
        irisLoaded = FabricLoader.getInstance().isModLoaded("iris");
        sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
        blissLoaded = FabricLoader.getInstance().isModLoaded("bliss");
        
        if (irisLoaded) {
            // Register custom shader uniforms for fish rendering
            registerFishUniforms();
        }
    }

    private static void registerFishUniforms() {
        // Iris/Bliss custom uniforms for fish:
        // - fish_scales: scale intensity
        // - fish_translucency: translucency factor
        // - fish_caustics: caustics intensity
        // - fish_shimmer: shimmer effect
    }

    public static boolean isIrisLoaded() {
        return irisLoaded;
    }

    public static boolean isSodiumLoaded() {
        return sodiumLoaded;
    }

    public static boolean isBlissLoaded() {
        return blissLoaded;
    }
}