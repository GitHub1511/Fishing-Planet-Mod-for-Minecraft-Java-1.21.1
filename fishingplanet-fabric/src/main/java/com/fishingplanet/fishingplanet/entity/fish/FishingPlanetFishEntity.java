package com.fishingplanet.fishingplanet.entity.fish;

import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.registry.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FishingPlanetFishEntity extends FishEntity {
    private static final TrackedData<Integer> SPECIES_ID = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FISH_LENGTH = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FISH_WEIGHT = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> FISH_FORM = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FROM_FISHING = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private FishSpecies species;
    private int schoolCooldown = 0;
    private UUID schoolLeaderId = null;

    public FishingPlanetFishEntity(EntityType<? extends FishEntity> type, World world) {
        super(type, world);
        this.setSpecies(FishSpecies.getRandomSpecies(world.random));
    }

    public static DefaultAttributeContainer.Builder createFishAttributes() {
        return FishEntity.createFishAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, LivingEntity.class, 8.0f, 1.5, 1.5));
        this.goalSelector.add(2, new LookAroundGoal(this));
        this.goalSelector.add(3, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(4, new SchoolingGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPECIES_ID, 0);
        this.dataTracker.startTracking(FISH_LENGTH, 1.0f);
        this.dataTracker.startTracking(FISH_WEIGHT, 1.0f);
        this.dataTracker.startTracking(FISH_FORM, 1); // Common form
        this.dataTracker.startTracking(FROM_FISHING, false);
    }

    public void setSpecies(FishSpecies species) {
        this.species = species;
        this.dataTracker.set(SPECIES_ID, species != null ? species.id() : 0);
        if (species != null) {
            // Set length and weight based on species stats with some variation
            Random random = this.getRandom();
            float length = (float) (species.minLengthCm() + random.nextDouble() * (species.maxLengthCm() - species.minLengthCm()));
            float weight = (float) (species.minWeightKg() + random.nextDouble() * (species.maxWeightKg() - species.minWeightKg()));
            this.dataTracker.set(FISH_LENGTH, length);
            this.dataTracker.set(FISH_WEIGHT, weight);
            this.dataTracker.set(FISH_FORM, random.nextInt(4)); // 0=Young, 1=Common, 2=Trophy, 3=Unique
            
            // Scale health based on weight
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(1.0, weight * 2.0));
            this.setHealth(this.getMaxHealth());
        }
    }

    public FishSpecies getSpecies() {
        if (this.species == null) {
            int id = this.dataTracker.get(SPECIES_ID);
            this.species = FishSpecies.getById(id);
        }
        return this.species;
    }

    public float getFishLength() {
        return this.dataTracker.get(FISH_LENGTH);
    }

    public float getFishWeight() {
        return this.dataTracker.get(FISH_WEIGHT);
    }

    public int getFishForm() {
        return this.dataTracker.get(FISH_FORM);
    }

    public void setFromFishing(boolean fromFishing) {
        this.dataTracker.set(FROM_FISHING, fromFishing);
    }

    public boolean isFromFishing() {
        return this.dataTracker.get(FROM_FISHING);
    }

    @Override
    public void tick() {
        super.tick();
        
        // Schooling behavior
        if (this.schoolCooldown > 0) {
            this.schoolCooldown--;
        }
        
        // Update school leader reference
        if (this.schoolLeaderId != null && this.getWorld() instanceof ServerWorld serverWorld) {
            Entity leader = serverWorld.getEntity(this.schoolLeaderId);
            if (leader == null || !leader.isAlive() || leader.squaredDistanceTo(this) > 100) {
                this.schoolLeaderId = null;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FISH_SWIM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FISH_FLOP;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FISH_FLOP;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.species != null) {
            nbt.putInt("SpeciesId", this.species.id());
        }
        nbt.putFloat("FishLength", this.getFishLength());
        nbt.putFloat("FishWeight", this.getFishWeight());
        nbt.putInt("FishForm", this.getFishForm());
        nbt.putBoolean("FromFishing", this.isFromFishing());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("SpeciesId")) {
            this.setSpecies(FishSpecies.getById(nbt.getInt("SpeciesId")));
        }
        if (nbt.contains("FishLength")) {
            this.dataTracker.set(FISH_LENGTH, nbt.getFloat("FishLength"));
        }
        if (nbt.contains("FishWeight")) {
            this.dataTracker.set(FISH_WEIGHT, nbt.getFloat("FishWeight"));
        }
        if (nbt.contains("FishForm")) {
            this.dataTracker.set(FISH_FORM, nbt.getInt("FishForm"));
        }
        if (nbt.contains("FromFishing")) {
            this.dataTracker.set(FROM_FISHING, nbt.getBoolean("FromFishing"));
        }
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
        // Random species if not from fishing
        if (spawnReason != SpawnReason.EVENT && this.species == null) {
            this.setSpecies(FishSpecies.getRandomSpecies(world.getRandom()));
        }
        return super.initialize(world, difficulty, spawnReason, entityData, nbt);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public boolean canBreatheInAir() {
        return false;
    }
}