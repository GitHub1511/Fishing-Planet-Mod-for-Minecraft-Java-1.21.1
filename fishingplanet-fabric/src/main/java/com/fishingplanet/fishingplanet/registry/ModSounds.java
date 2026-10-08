package com.fishingplanet.fishingplanet.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    // Fishing sounds
    public static final SoundEvent FISH_BITE = register("fish_bite");
    public static final SoundEvent FISH_HOOKED = register("fish_hooked");
    public static final SoundEvent FISH_ESCAPE = register("fish_escape");
    public static final SoundEvent REEL_CAST = register("reel_cast");
    public static final SoundEvent REEL_RETRIEVE = register("reel_retrieve");
    public static final SoundEvent DRAG_SOUND = register("drag");
    public static final SoundEvent LINE_SNAP = register("line_snap");
    public static final SoundEvent ROD_BEND = register("rod_bend");
    public static final SoundEvent SPLASH_SMALL = register("splash_small");
    public static final SoundEvent SPLASH_MEDIUM = register("splash_medium");
    public static final SoundEvent SPLASH_LARGE = register("splash_large");

    // Fish ambient sounds
    public static final SoundEvent FISH_SWIM = register("fish_swim");
    public static final SoundEvent FISH_JUMP = register("fish_jump");
    public static final SoundEvent FISH_FLOP = register("fish_flop");

    private static SoundEvent register(String name) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(FishingPlanetMod.MOD_ID, name), SoundEvent.of(Identifier.of(FishingPlanetMod.MOD_ID, name)));
    }

    public static void register() {
        // Sounds are registered via static initialization
    }
}