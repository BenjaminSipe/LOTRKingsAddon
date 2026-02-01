//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enovak.lotrmoremobs.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class LOTRModelNumakil extends ModelBase {
    private ModelRenderer head;
    private ModelRenderer neck;
    private ModelRenderer horn1;
    private ModelRenderer horn2;
    private ModelRenderer body;
    private ModelRenderer tail;
    private ModelRenderer leg1;
    private ModelRenderer leg2;
    private ModelRenderer leg3;
    private ModelRenderer leg4;

    public LOTRModelNumakil() {
        this(0.0F);
    }

    public LOTRModelNumakil(float f) {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 3.0F, -12.0F);
        this.head.addBox(-5.0F, -2.0F, -22.0F, 10, 10, 16, f);
        this.head.addBox(-4.0F, -4.0F, -10.0F, 1, 2, 2, f);
        this.head.mirror = true;
        this.head.addBox(3.0F, -4.0F, -10.0F, 1, 2, 2, f);
        this.neck = new ModelRenderer(this, 52, 0);
        this.neck.setRotationPoint(0.0F, 3.0F, -12.0F);
        this.neck.addBox(-7.0F, -4.0F, -7.0F, 14, 13, 8, f);
        this.horn1 = new ModelRenderer(this, 36, 0);
        this.horn1.addBox(-1.0F, -14.0F, -20.0F, 2, 8, 2, f);
        this.horn1.rotateAngleX = (float)Math.toRadians((double)15.0F);
        this.head.addChild(this.horn1);
        this.horn2 = new ModelRenderer(this, 44, 0);
        this.horn2.addBox(-1.0F, -3.0F, -17.0F, 2, 4, 2, f);
        this.horn2.rotateAngleX = (float)Math.toRadians((double)-10.0F);
        this.head.addChild(this.horn2);
        this.body = new ModelRenderer(this, 0, 26);
        this.body.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.body.addBox(-8.0F, -7.0F, -13.0F, 16, 16, 34, f);
        this.tail = new ModelRenderer(this, 100, 63);
        this.tail.setRotationPoint(0.0F, 7.0F, 21.0F);
        this.tail.addBox(-1.5F, -1.0F, -1.0F, 3, 8, 2, f);
        this.leg1 = new ModelRenderer(this, 30, 76);
        this.leg1.setRotationPoint(-8.0F, 3.0F, 14.0F);
        this.leg1.addBox(-8.0F, -3.0F, -5.0F, 8, 12, 10, f);
        this.leg1.setTextureOffset(0, 95).addBox(-7.0F, 9.0F, -3.0F, 6, 12, 6, f);
        this.leg2 = new ModelRenderer(this, 30, 76);
        this.leg2.setRotationPoint(8.0F, 3.0F, 14.0F);
        this.leg2.mirror = true;
        this.leg2.addBox(0.0F, -3.0F, -5.0F, 8, 12, 10, f);
        this.leg2.setTextureOffset(0, 95).addBox(1.0F, 9.0F, -3.0F, 6, 12, 6, f);
        this.leg3 = new ModelRenderer(this, 0, 76);
        this.leg3.setRotationPoint(-8.0F, 4.0F, -6.0F);
        this.leg3.addBox(-7.0F, -3.0F, -4.0F, 7, 11, 8, f);
        this.leg3.setTextureOffset(0, 95).addBox(-6.5F, 8.0F, -3.0F, 6, 12, 6, f);
        this.leg4 = new ModelRenderer(this, 0, 76);
        this.leg4.setRotationPoint(8.0F, 4.0F, -6.0F);
        this.leg4.mirror = true;
        this.leg4.addBox(0.0F, -3.0F, -4.0F, 7, 11, 8, f);
        this.leg4.setTextureOffset(0, 95).addBox(0.5F, 8.0F, -3.0F, 6, 12, 6, f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.horn1.showModel = this.horn2.showModel = !this.isChild;
        if (this.isChild) {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 8.0F * f5, 12.0F * f5);
            this.head.render(f5);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * f5, 0.0F * f5);
            this.body.render(f5);
            this.tail.render(f5);
            this.leg1.render(f5);
            this.leg2.render(f5);
            this.leg3.render(f5);
            this.leg4.render(f5);
            GL11.glPopMatrix();
        } else {
            this.head.render(f5);
            this.neck.render(f5);
            this.body.render(f5);
            this.tail.render(f5);
            this.leg1.render(f5);
            this.leg2.render(f5);
            this.leg3.render(f5);
            this.leg4.render(f5);
        }

    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        this.head.rotateAngleX = (float)Math.toRadians((double)12.0F);
        this.head.rotateAngleY = 0.0F;
        ModelRenderer var10000 = this.head;
        var10000.rotateAngleX += MathHelper.cos(f * 0.2F) * 0.3F * f1;
        var10000 = this.head;
        var10000.rotateAngleX += (float)Math.toRadians((double)f4);
        var10000 = this.head;
        var10000.rotateAngleY += (float)Math.toRadians((double)f3);
        this.neck.rotateAngleX = this.head.rotateAngleX;
        this.neck.rotateAngleY = this.head.rotateAngleY;
        this.neck.rotateAngleZ = this.head.rotateAngleZ;
        this.tail.rotateAngleX = (float)Math.toRadians((double)40.0F);
        var10000 = this.tail;
        var10000.rotateAngleX += MathHelper.cos(f * 0.3F) * 0.5F * f1;
        this.leg1.rotateAngleX = MathHelper.cos(f * 0.4F) * 1.0F * f1;
        this.leg2.rotateAngleX = MathHelper.cos(f * 0.4F + (float)Math.PI) * 1.0F * f1;
        this.leg3.rotateAngleX = MathHelper.cos(f * 0.4F + (float)Math.PI) * 1.0F * f1;
        this.leg4.rotateAngleX = MathHelper.cos(f * 0.4F) * 1.0F * f1;
    }
}
