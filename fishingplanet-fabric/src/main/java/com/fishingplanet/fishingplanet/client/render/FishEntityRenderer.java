package com.fishingplanet.fishingplanet.client.render;

import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import com.fishingplanet.fishingplanet.compat.IrisCompat;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FishModel;
import net.minecraft.client.render.entity.state.FishEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class FishEntityRenderer extends MobEntityRenderer<FishingPlanetFishEntity, FishEntityRenderState, FishModel<FishEntityRenderState>> {
    public FishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FishModel<>(context.getPart(EntityModelLayers.FISH)), 0.3f);
    }

    @Override
    public FishEntityRenderState createRenderState() {
        return new FishEntityRenderState();
    }

    @Override
    public void updateRenderState(FishingPlanetFishEntity entity, FishEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        
        FishSpecies species = entity.getSpecies();
        if (species != null) {
            // Scale model based on fish length
            float lengthScale = MathHelper.lerp(
                (entity.getFishLength() - species.minLengthCm()) / 
                Math.max(1, species.maxLengthCm() - species.minLengthCm()),
                0.5f, 1.5f
            );
            state.scale = lengthScale;
            
            // Store species info for custom shader
            state.customData.put("species_id", species.id());
            state.customData.put("fish_form", entity.getFishForm());
            state.customData.put("fish_weight", entity.getFishWeight());
        }
        
        // Add swimming animation
        state.bodyPitch = entity.getPitch();
        state.bodyYaw = entity.getYaw();
    }

    @Override
    public Identifier getTexture(FishEntityRenderState state) {
        // Dynamic texture based on species
        int speciesId = (int) state.customData.getOrDefault("species_id", 61);
        return Identifier.of("fishingplanet", "textures/entity/fish/" + speciesId + ".png");
    }
}