package com.enovak.lotrmoremobs.model;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Mumakil extends ModelBase {
    private final ModelRenderer bb_main;

    public Mumakil () {
        this( 0.0f );
    }

    public Mumakil( float f) {
        textureWidth = 64;
        textureHeight = 64;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.cubeList.add(new ModelBox(bb_main, 24, 17, -5.0F, -8.0F, 3.0F, 3, 8, 3, f));
        bb_main.cubeList.add(new ModelBox(bb_main, 24, 28, 2.0F, -8.0F, 3.0F, 3, 8, 3, f));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 29, 2.0F, -8.0F, -6.0F, 3, 8, 3, f));
        bb_main.cubeList.add(new ModelBox(bb_main, 12, 29, -5.0F, -8.0F, -6.0F, 3, 8, 3, f));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -4.0F, -13.0F, -6.0F, 8, 5, 12, f));
        bb_main.cubeList.add(new ModelBox(bb_main, 0, 17, -3.0F, -17.0F, -10.0F, 6, 6, 6, f));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        bb_main.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}