package com.fishingplanet.fishingplanet.client.render;

import com.fishingplanet.fishingplanet.entity.fish.FishingPlanetFishEntity;
import com.fishingplanet.fishingplanet.entity.fish.FishSpecies;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class FishEntityRenderer extends MobEntityRenderer<FishingPlanetFishEntity, CodEntityModel<FishingPlanetFishEntity>> {
    private static final java.util.Map<Integer, Identifier> TEXTURE_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    public FishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CodEntityModel<>(context.getPart(EntityModelLayers.COD)), 0.3F);
    }

    @Override
    public Identifier getTexture(FishingPlanetFishEntity entity) {
        return TEXTURE_CACHE.computeIfAbsent(entity.getSpecies().id(),
            id -> Identifier.of("fishingplanet", "textures/entity/fish/" + id + ".png"));
    }

    @Override
    public void render(FishingPlanetFishEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        FishSpecies species = entity.getSpecies();
        float range = Math.max(1.0F, species.maxLengthCm() - species.minLengthCm());
        float t = (entity.getFishLength() - species.minLengthCm()) / range;
        float scale = MathHelper.clamp(0.5F + t, 0.5F, 1.6F);
        matrices.push();
        matrices.scale(scale, scale, scale);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
