package com.enovak.lotrmoremobs.render.entity;

import com.enovak.lotrmoremobs.entity.animal.LOTREntityMumakilGecko;
import com.enovak.lotrmoremobs.model.LOTRModelMumakil;
import com.enovak.lotrmoremobs.model.LOTRModelMumakilGecko;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LOTRRenderMumakilGecko extends GeoEntityRenderer<LOTREntityMumakilGecko> {
    public static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation( "lotrmoremobs:textures/mumakil.png") ;;


    public LOTRRenderMumakilGecko() {
        super(new LOTRModelMumakilGecko());
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return TEXTURE_RESOURCE;
    }
}
