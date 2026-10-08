package com.fishingplanet.fishingplanet.client.render;

import com.fishingplanet.fishingplanet.entity.bobber.FishingPlanetBobberEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

public class BobberEntityRenderer extends EntityRenderer<FishingPlanetBobberEntity, ProjectileEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of("fishingplanet", "textures/entity/bobber.png");

    public BobberEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }

    @Override
    public void updateRenderState(FishingPlanetBobberEntity entity, ProjectileEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        
        // Custom bobber rendering based on tackle type
        state.yaw = entity.getYaw();
        state.pitch = entity.getPitch();
    }

    @Override
    public Identifier getTexture(ProjectileEntityRenderState state) {
        return TEXTURE;
    }
}