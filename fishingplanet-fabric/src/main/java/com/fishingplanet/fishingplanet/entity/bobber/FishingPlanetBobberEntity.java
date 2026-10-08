package com.fishingplanet.fishingplanet.entity.bobber;

import com.fishingplanet.fishingplanet.item.RodItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FishingPlanetBobberEntity extends FishingBobberEntity {
    private int tackleType = 0; // 0=none, 1=float, 2=bottom, 3=feeder, 4=lure, 5=spod
    private float sinkRate = 0.05f;
    private boolean hasBite = false;
    private int biteCooldown = 0;
    private float dragForce = 0f;

    public FishingPlanetBobberEntity(EntityType<? extends FishingBobberEntity> type, World world) {
        super(type, world);
    }

    public FishingPlanetBobberEntity(World world, LivingEntity owner, ItemStack stack) {
        super(world, owner, stack);
        // Determine tackle type from rod
        if (stack.getItem() instanceof RodItem rod) {
            this.tackleType = rod.getTackleType();
            this.sinkRate = rod.getSinkRate();
        }
    }

    public static DefaultAttributeContainer.Builder createBobberAttributes() {
        return FishingBobberEntity.createAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.hasBite) {
            this.biteCooldown--;
            if (this.biteCooldown <= 0) {
                this.hasBite = false;
            }
        }

        // Custom physics for different tackle types
        if (!this.isSubmergedInWater() && this.tackleType > 0) {
            // Apply custom sink rate
            this.setVelocity(this.getVelocity().add(0, -this.sinkRate, 0));
        }

        // Drag simulation for fighting fish
        if (this.hookedEntity != null) {
            this.dragForce = Math.min(this.dragForce + 0.01f, 1.0f);
            // Apply drag to hooked entity
            this.hookedEntity.addVelocity(this.getVelocity().multiply(-this.dragForce * 0.1));
        } else {
            this.dragForce = 0f;
        }
    }

    @Override
    protected void pullHookedEntity() {
        // Override with custom tackle physics
        if (this.hookedEntity != null) {
            Entity owner = this.getOwner();
            if (owner != null) {
                // Calculate pull force based on rod and line
                float pullForce = 0.5f;
                if (this.getOwner() instanceof LivingEntity living && living.getMainHandStack().getItem() instanceof RodItem rod) {
                    pullForce = rod.getPullForce();
                }
                
                Vec3d direction = owner.getPos().subtract(this.hookedEntity.getPos()).normalize();
                this.hookedEntity.setVelocity(this.hookedEntity.getVelocity().add(direction.multiply(pullForce)));
            }
        }
    }

    public int getTackleType() {
        return tackleType;
    }

    public void setTackleType(int tackleType) {
        this.tackleType = tackleType;
    }

    public void setHasBite(boolean hasBite) {
        this.hasBite = hasBite;
        this.biteCooldown = hasBite ? 100 : 0; // 5 seconds
    }

    public boolean hasBite() {
        return hasBite;
    }

    public float getDragForce() {
        return dragForce;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TackleType", tackleType);
        nbt.putFloat("SinkRate", sinkRate);
        nbt.putBoolean("HasBite", hasBite);
        nbt.putInt("BiteCooldown", biteCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.tackleType = nbt.getInt("TackleType");
        this.sinkRate = nbt.getFloat("SinkRate");
        this.hasBite = nbt.getBoolean("HasBite");
        this.biteCooldown = nbt.getInt("BiteCooldown");
    }
}