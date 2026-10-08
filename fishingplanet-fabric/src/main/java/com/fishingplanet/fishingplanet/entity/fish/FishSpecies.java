package com.fishingplanet.fishingplanet.entity.fish;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FishSpecies {
    private static final Map<Integer, FishSpecies> BY_ID = new ConcurrentHashMap<>();
    private static final List<FishSpecies> ALL_SPECIES = new ArrayList<>();
    private static boolean loaded = false;

    public final int id;
    public final String name;
    public final String displayName;
    public final String category;
    public final String habitat;
    public final String depth;
    public final String rarity;
    public final float minWeightKg;
    public final float maxWeightKg;
    public final float minLengthCm;
    public final float maxLengthCm;
    public final int spawnWeight;
    public final Identifier lootTable;
    public final Map<String, Object> minecraft;

    private FishSpecies(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.name = obj.get("name").getAsString();
        this.displayName = obj.get("display_name").getAsString();
        this.category = obj.get("category").getAsString();
        this.habitat = obj.get("habitat").getAsString();
        this.depth = obj.get("depth").getAsString();
        this.rarity = obj.get("rarity").getAsString();
        this.minWeightKg = obj.get("min_weight_kg").getAsFloat();
        this.maxWeightKg = obj.get("max_weight_kg").getAsFloat();
        this.minLengthCm = obj.get("min_length_cm").getAsFloat();
        this.maxLengthCm = obj.get("max_length_cm").getAsFloat();
        this.spawnWeight = obj.has("spawn_weight") ? obj.get("spawn_weight").getAsInt() : 10;
        this.lootTable = Identifier.of("fishingplanet", "fish/" + name.toLowerCase());
        
        this.minecraft = new HashMap<>();
        if (obj.has("minecraft")) {
            JsonObject mc = obj.getAsJsonObject("minecraft");
            for (Map.Entry<String, JsonElement> entry : mc.entrySet()) {
                this.minecraft.put(entry.getKey(), entry.getValue().getAsJsonPrimitive().getAsString());
            }
        }
    }

    public static void load() {
        if (loaded) return;
        try (Reader reader = new InputStreamReader(
                FishSpecies.class.getResourceAsStream("/assets/fishingplanet/data/fish_manifest.json"))) {
            if (reader == null) {
                // Fallback: create default species
                createDefaultSpecies();
                return;
            }
            JsonObject root = new Gson().fromJson(reader, JsonObject.class);
            JsonArray speciesArray = root.getAsJsonArray("species");
            for (JsonElement elem : speciesArray) {
                FishSpecies species = new FishSpecies(elem.getAsJsonObject());
                BY_ID.put(species.id, species);
                ALL_SPECIES.add(species);
            }
            loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultSpecies();
        }
    }

    private static void createDefaultSpecies() {
        // Create a few default species for testing
        String[][] defaults = {
            {"61", "Pumpkinseed", "Sunfish", "Freshwater", "Shallow", "Common", "0.1", "0.5", "10", "25"},
            {"62", "Bluegill", "Sunfish", "Freshwater", "Shallow", "Common", "0.1", "0.5", "10", "25"},
            {"111", "LargemouthBass", "Bass (Freshwater)", "Freshwater", "Mid", "Common", "0.5", "10", "20", "60"},
            {"501", "RainbowTrout", "Trout/Char/Salmonids", "Freshwater", "Mid-Top", "Common", "0.2", "10", "15", "80"},
            {"702", "AtlanticSalmon", "Salmon/Whitefish/Taimen", "Freshwater/Anadromous", "Mid", "Uncommon", "1", "50", "40", "150"},
        };
        for (String[] d : defaults) {
            FishSpecies s = new FishSpecies(
                Integer.parseInt(d[0]), d[1], d[1].replace("_", " "), d[2], d[3], d[4], d[5],
                Float.parseFloat(d[6]), Float.parseFloat(d[7]), Float.parseFloat(d[8]), Float.parseFloat(d[9])
            );
            BY_ID.put(s.id, s);
            ALL_SPECIES.add(s);
        }
        loaded = true;
    }

    public FishSpecies(int id, String name, String displayName, String category, String habitat, String depth, String rarity,
                       float minWeightKg, float maxWeightKg, float minLengthCm, float maxLengthCm) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.category = category;
        this.habitat = habitat;
        this.depth = depth;
        this.rarity = rarity;
        this.minWeightKg = minWeightKg;
        this.maxWeightKg = maxWeightKg;
        this.minLengthCm = minLengthCm;
        this.maxLengthCm = maxLengthCm;
        this.spawnWeight = "Common".equals(rarity) ? 10 : ("Uncommon".equals(rarity) ? 5 : ("Rare".equals(rarity) ? 2 : 1));
        this.lootTable = Identifier.of("fishingplanet", "fish/" + name.toLowerCase());
        this.minecraft = new HashMap<>();
    }

    public static FishSpecies getById(int id) {
        load();
        return BY_ID.getOrDefault(id, BY_ID.get(61)); // Default to Pumpkinseed
    }

    public static FishSpecies getRandomSpecies(Random random) {
        load();
        if (ALL_SPECIES.isEmpty()) return BY_ID.get(61);
        int totalWeight = ALL_SPECIES.stream().mapToInt(s -> s.spawnWeight).sum();
        int roll = random.nextInt(totalWeight);
        for (FishSpecies s : ALL_SPECIES) {
            roll -= s.spawnWeight;
            if (roll < 0) return s;
        }
        return ALL_SPECIES.get(0);
    }

    public static List<FishSpecies> getAll() {
        load();
        return ALL_SPECIES;
    }

    public static List<FishSpecies> getByCategory(String category) {
        load();
        return ALL_SPECIES.stream().filter(s -> s.category.equals(category)).toList();
    }

    public static List<FishSpecies> getByHabitat(String habitat) {
        load();
        return ALL_SPECIES.stream().filter(s -> s.habitat.contains(habitat)).toList();
    }
}