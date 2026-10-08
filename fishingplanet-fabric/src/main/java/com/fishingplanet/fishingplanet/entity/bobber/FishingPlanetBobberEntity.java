package com.fishingplanet.fishingplanet.entity.bobber;

import com.fishingplanet.fishingplanet.item.RodItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class FishingPlanetBobberEntity extends FishingBobberEntity {
    private int tackleType = 0;
    private float sinkRate = 0.05F;
    private boolean hasBite = false;
    private int biteCooldown = 0;

    public FishingPlanetBobberEntity(EntityType<? extends FishingBobberEntity> type, World world) {
        super(type, world);
    }

    public FishingPlanetBobberEntity(EntityType<? extends FishingBobberEntity> type, World world, PlayerEntity owner, ItemStack stack) {
        super(type, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        if (stack.getItem() instanceof RodItem rod) {
            this.tackleType = rod.getTackleType();
            this.sinkRate = rod.getSinkRate();
        }
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
        if (!this.isSubmergedInWater()) {
            this.setVelocity(this.getVelocity().add(0.0, -this.sinkRate, 0.0));
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
        this.biteCooldown = hasBite ? 100 : 0;
    }

    public boolean hasBite() {
        return hasBite;
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
