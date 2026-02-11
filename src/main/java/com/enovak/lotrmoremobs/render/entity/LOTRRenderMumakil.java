//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enovak.lotrmoremobs.render.entity;

import com.enovak.lotrmoremobs.entity.animal.LOTREntityMumakil;
import com.enovak.lotrmoremobs.model.LOTRModelNumakil;
import com.enovak.lotrmoremobs.model.Mumakil;
import lotr.client.model.LOTRModelRhino;
import lotr.client.render.entity.LOTRRenderHorse;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class LOTRRenderMumakil extends RenderLiving {
    private static ResourceLocation rhinoTexture = new ResourceLocation("lotrmoremobs:mob/mumakil.png");
    private static ResourceLocation saddleTexture = new ResourceLocation("lotr:mob/rhino/saddle.png");

    public LOTRRenderMumakil() {
        super(new Mumakil( 0.5f), .5F);
        this.setRenderPassModel(new Mumakil(0.5F));
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
}
