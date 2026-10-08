package com.fishingplanet.fishingplanet;

import com.fishingplanet.fishingplanet.client.render.FishEntityRenderer;
import com.fishingplanet.fishingplanet.client.render.BobberEntityRenderer;
import com.fishingplanet.fishingplanet.compat.IrisCompat;
import com.fishingplanet.fishingplanet.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.entity.EntityRendererFactory;

@Environment(EnvType.CLIENT)
public class FishingPlanetClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register entity renderers
        EntityRendererRegistry.register(ModEntities.FISHING_PLANET_FISH, FishEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.FISHING_PLANET_BOBBER, BobberEntityRenderer::new);

        // Register shader compatibility
        IrisCompat.register();

        // Register model loading plugin for dynamic fish models
        ModelLoadingPlugin.register((resourceManager, context) -> {
            // Dynamic model loading for fish species
        });
    }
}