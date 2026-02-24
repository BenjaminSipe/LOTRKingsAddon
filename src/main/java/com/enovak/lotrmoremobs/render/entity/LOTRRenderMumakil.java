//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enovak.lotrmoremobs.render.entity;

import com.enovak.lotrmoremobs.entity.animal.LOTREntityMumakil;
import com.enovak.lotrmoremobs.model.LOTRModelNumakil;
import com.enovak.lotrmoremobs.model.LOTRMumakilModel;
import com.enovak.lotrmoremobs.model.Mumakil;
import lotr.client.model.LOTRModelRhino;
import lotr.client.render.entity.LOTRRenderHorse;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LOTRRenderMumakil extends RenderLiving {
    private static ResourceLocation rhinoTexture = new ResourceLocation("lotrmoremobs:mob/texture4.png");
    private static ResourceLocation saddleTexture = new ResourceLocation("lotr:mob/rhino/saddle.png");

    public LOTRRenderMumakil() {
        super(new LOTRMumakilModel( ), 0.5F);
        this.setRenderPassModel(new LOTRMumakilModel());
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        LOTREntityMumakil rhino = (LOTREntityMumakil)entity;
        return LOTRRenderHorse.getLayeredMountTexture(rhino, rhinoTexture);
    }

    protected int shouldRenderPass(EntityLivingBase entity, int pass, float f) {
        if (pass == 0 && ((LOTREntityMumakil)entity).isMountSaddled()) {
            this.bindTexture(saddleTexture);
            return 1;
        } else {
            return super.shouldRenderPass(entity, pass, f);
        }
    }

    protected void preRenderCallback(EntityLivingBase entity, float f) {
        GL11.glScalef(3F, 3F, 3F);
    }

//    public static final AnimationDefinition Walking = AnimationDefinition.Builder.withLength(6.0417F).looping()
//            .addAnimation("front_left_foot", new AnimationChannel(AnimationChannel.Targets.ROTATION,
//                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
//                    new Keyframe(1.5833F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
//            ))
//            .addAnimation("back_left_foot", new AnimationChannel(AnimationChannel.Targets.ROTATION,
//                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
//                    new Keyframe(2.4583F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
//            ))
//            .addAnimation("back_right_foot", new AnimationChannel(AnimationChannel.Targets.ROTATION,
//                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
//                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
//                    new Keyframe(2.875F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
//            ))
//            .addAnimation("front_right_foot", new AnimationChannel(AnimationChannel.Targets.ROTATION,
//                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
//            ))
//            .addAnimation("Sway", new AnimationChannel(AnimationChannel.Targets.ROTATION,
//                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
//                    new Keyframe(2.2917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
//            ))
//            .build();
}
