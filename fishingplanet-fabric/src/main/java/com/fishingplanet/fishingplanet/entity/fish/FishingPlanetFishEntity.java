package com.fishingplanet.fishingplanet.entity.fish;

import com.fishingplanet.fishingplanet.registry.ModSounds;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FishingPlanetFishEntity extends FishEntity {
    private static final TrackedData<Integer> SPECIES_ID = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FISH_LENGTH = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FISH_WEIGHT = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> FISH_FORM = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FROM_FISHING = DataTracker.registerData(FishingPlanetFishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private FishSpecies species;

    public FishingPlanetFishEntity(EntityType<? extends FishEntity> type, World world) {
        super(type, world);
        this.setSpecies(FishSpecies.getRandomSpecies(world.random));
    }

    public static DefaultAttributeContainer.Builder createFishAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0F, 1.6, 1.4));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPECIES_ID, 0);
        builder.add(FISH_LENGTH, 1.0F);
        builder.add(FISH_WEIGHT, 1.0F);
        builder.add(FISH_FORM, 1);
        builder.add(FROM_FISHING, false);
    }

    public void setSpecies(FishSpecies species) {
        this.species = species;
        this.dataTracker.set(SPECIES_ID, species != null ? species.id() : 0);
        if (species != null) {
            float length = (float) (species.minLengthCm() + this.getRandom().nextDouble() * (species.maxLengthCm() - species.minLengthCm()));
            float weight = (float) (species.minWeightKg() + this.getRandom().nextDouble() * (species.maxWeightKg() - species.minWeightKg()));
            this.dataTracker.set(FISH_LENGTH, length);
            this.dataTracker.set(FISH_WEIGHT, weight);
            this.dataTracker.set(FISH_FORM, this.getRandom().nextInt(4));
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(1.0, weight * 2.0));
            this.setHealth(this.getMaxHealth());
        }
    }

    public FishSpecies getSpecies() {
        if (this.species == null) {
            this.species = FishSpecies.getById(this.dataTracker.get(SPECIES_ID));
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

    public SoundEvent getFlopSound() {
        return ModSounds.FISH_FLOP;
    }

    @Override
    public ItemStack getBucketItem() {
        // TODO (M5): custom bucket item holding the caught species/weight
        return new ItemStack(Items.WATER_BUCKET);
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
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        if (this.species == null) {
            this.setSpecies(FishSpecies.getRandomSpecies(this.getRandom()));
        }
        return super.initialize(world, difficulty, spawnReason, entityData);
    }
}
