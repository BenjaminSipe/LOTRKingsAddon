//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enovak.lotrmoremobs.render.entity;

import com.enovak.lotrmoremobs.entity.animal.LOTREntityMumakil;
import com.enovak.lotrmoremobs.model.LOTRModelMumakil;
import lotr.client.model.LOTRModelElk;
import lotr.client.model.LOTRModelRhino;
import lotr.client.render.entity.LOTRRenderHorse;
import lotr.common.entity.animal.LOTREntityRhino;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class LOTRRenderMumakil extends RenderLiving {
    private static ResourceLocation mumakilTexture = new ResourceLocation("lotrmoremobs:mob/mumakil/mumakil.png");
    private static ResourceLocation saddleTexture = new ResourceLocation("lotrmoremobs:mob/mumakil/mumakil.png");

    public LOTRRenderMumakil() {
        super(new LOTRModelMumakil(), 0.5F);
        this.setRenderPassModel(new LOTRModelMumakil(0.5F));
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        LOTREntityMumakil mumakil = (LOTREntityMumakil) entity;
        return LOTRRenderHorse.getLayeredMountTexture(mumakil, mumakilTexture);
    }

    protected int shouldRenderPass(EntityLivingBase entity, int pass, float f) {
        if (pass == 0 && ((LOTREntityRhino)entity).isMountSaddled()) {
            this.bindTexture(saddleTexture);
            return 1;
        } else {
            return super.shouldRenderPass(entity, pass, f);
        }
    }
}
