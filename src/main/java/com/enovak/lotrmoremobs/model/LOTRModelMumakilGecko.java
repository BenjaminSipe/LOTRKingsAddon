package com.enovak.lotrmoremobs.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LOTRModelMumakilGecko extends AnimatedGeoModel {
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation( "lotrmoremobs:geo/LOTRMumakilModel.geo.json") ;;
    public static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation( "lotrmoremobs:textures/mumakil.png") ;;
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation( "lotrmoremobs:animations/LOTRMumakilModel.animation.json") ;;


    @Override
    public void setLivingAnimations(Object o, Integer integer, AnimationEvent animationEvent) {

    }

    @Override
    public ResourceLocation getModelLocation(Object o) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(Object o) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Object o) {
        return ANIMATION_RESOURCE;
    }
}
