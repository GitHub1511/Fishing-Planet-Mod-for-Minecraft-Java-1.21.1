package com.fishingplanet.fishingplanet;

import com.fishingplanet.fishingplanet.client.render.FishEntityRenderer;
import com.fishingplanet.fishingplanet.compat.IrisCompat;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;

@Environment(EnvType.CLIENT)
public class FishingPlanetClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.FISHING_PLANET_FISH, FishEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.FISHING_PLANET_BOBBER, FishingBobberEntityRenderer::new);
        IrisCompat.register();
    }
}
