package com.enovak.lotrmoremobs.model;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class LOTRMumakilModel extends ModelBase {
    private final ModelRenderer front_left_foot;
    private final ModelRenderer back_left_foot;
    private final ModelRenderer body;
    private final ModelRenderer body_lower_front_r1;
    private final ModelRenderer body_upper_back_r1;
    private final ModelRenderer back_right_foot;
    private final ModelRenderer front_right_foot;
    private final ModelRenderer head;
    private final ModelRenderer left_small_tusk_r1;
    private final ModelRenderer right_small_tusk_r1;
    private final ModelRenderer left_ear_r1;
    private final ModelRenderer right_ear_r1;
    private final ModelRenderer trunk;
    private final ModelRenderer right_main_tusk;
    private final ModelRenderer t5_r1;
    private final ModelRenderer t4_r1;
    private final ModelRenderer t3_r1;
    private final ModelRenderer t2_r1;
    private final ModelRenderer t1_r1;
    private final ModelRenderer left_main_tusk;
    private final ModelRenderer t5_r2;
    private final ModelRenderer t4_r2;
    private final ModelRenderer t3_r2;
    private final ModelRenderer t2_r2;
    private final ModelRenderer t1_r2;

    public LOTRMumakilModel() {
        textureWidth = 512;
        textureHeight = 512;

        front_left_foot = new ModelRenderer(this);
        front_left_foot.setRotationPoint(-8.0F, 21.0F, -40.0F);
        front_left_foot.cubeList.add(new ModelBox(front_left_foot, 176, 44, -14.0F, 0.0F, 5.0F, 15, 3, 15, 0.0F));
        front_left_foot.cubeList.add(new ModelBox(front_left_foot, 240, 199, -13.0F, -4.0F, 7.0F, 13, 4, 12, 0.0F));
        front_left_foot.cubeList.add(new ModelBox(front_left_foot, 180, 188, -14.0F, -58.0F, 8.0F, 15, 22, 15, 0.0F));
        front_left_foot.cubeList.add(new ModelBox(front_left_foot, 60, 195, -12.0F, -36.0F, 9.0F, 11, 32, 11, 0.0F));

        back_left_foot = new ModelRenderer(this);
        back_left_foot.setRotationPoint(-7.0F, 24.0F, 12.0F);
        back_left_foot.cubeList.add(new ModelBox(back_left_foot, 216, 163, -14.0F, -3.0F, 5.0F, 15, 3, 15, 0.0F));
        back_left_foot.cubeList.add(new ModelBox(back_left_foot, 166, 120, -13.0F, -7.0F, 7.0F, 13, 4, 12, 0.0F));
        back_left_foot.cubeList.add(new ModelBox(back_left_foot, 104, 232, -12.0F, -31.0F, 9.0F, 11, 24, 11, 0.0F));
        back_left_foot.cubeList.add(new ModelBox(back_left_foot, 176, 0, -15.0F, -57.0F, 6.0F, 17, 26, 18, 0.0F));

        body = new ModelRenderer(this);
        body.setRotationPoint(19.0F, -4.0F, -20.0F);
        body.cubeList.add(new ModelBox(body, 0, 75, -36.0F, -27.0F, 5.0F, 35, 21, 48, 0.0F));
        body.cubeList.add(new ModelBox(body, 0, 0, -39.0F, -54.0F, -16.0F, 40, 27, 48, 0.0F));
        body.cubeList.add(new ModelBox(body, 240, 263, -22.0F, -26.0F, 53.0F, 6, 21, 4, 0.0F));
        body.cubeList.add(new ModelBox(body, 216, 62, -20.0F, 5.0F, 57.0F, 2, 10, 2, 0.0F));
        body.cubeList.add(new ModelBox(body, 202, 62, -21.0F, -5.0F, 55.0F, 4, 10, 3, 0.0F));

        body_lower_front_r1 = new ModelRenderer(this);
        body_lower_front_r1.setRotationPoint(-1.0F, -22.0F, 9.0F);
        body.addChild(body_lower_front_r1);
        setRotationAngle(body_lower_front_r1, 0.7854F, 0.0F, 0.0F);
        body_lower_front_r1.cubeList.add(new ModelBox(body_lower_front_r1, 110, 144, -37.0F, -21.0F, -14.0F, 38, 29, 15, 0.0F));

        body_upper_back_r1 = new ModelRenderer(this);
        body_upper_back_r1.setRotationPoint(-1.0F, -34.0F, 46.0F);
        body.addChild(body_upper_back_r1);
        setRotationAngle(body_upper_back_r1, 0.6545F, 0.0F, 0.0F);
        body_upper_back_r1.cubeList.add(new ModelBox(body_upper_back_r1, 0, 144, -37.0F, -24.0F, -16.0F, 38, 34, 17, 0.0F));

        back_right_foot = new ModelRenderer(this);
        back_right_foot.setRotationPoint(7.0F, 24.0F, 12.0F);
        back_right_foot.cubeList.add(new ModelBox(back_right_foot, 236, 44, -1.0F, -3.0F, 5.0F, 15, 3, 15, 0.0F));
        back_right_foot.cubeList.add(new ModelBox(back_right_foot, 246, 0, 0.0F, -7.0F, 7.0F, 13, 4, 12, 0.0F));
        back_right_foot.cubeList.add(new ModelBox(back_right_foot, 46, 238, 1.0F, -31.0F, 9.0F, 11, 24, 11, 0.0F));
        back_right_foot.cubeList.add(new ModelBox(back_right_foot, 110, 188, -2.0F, -57.0F, 6.0F, 17, 26, 18, 0.0F));

        front_right_foot = new ModelRenderer(this);
        front_right_foot.setRotationPoint(8.0F, 21.0F, -40.0F);
        front_right_foot.cubeList.add(new ModelBox(front_right_foot, 240, 181, -1.0F, 0.0F, 5.0F, 15, 3, 15, 0.0F));
        front_right_foot.cubeList.add(new ModelBox(front_right_foot, 246, 16, 0.0F, -4.0F, 7.0F, 13, 4, 12, 0.0F));
        front_right_foot.cubeList.add(new ModelBox(front_right_foot, 0, 195, -1.0F, -58.0F, 8.0F, 15, 22, 15, 0.0F));
        front_right_foot.cubeList.add(new ModelBox(front_right_foot, 216, 120, 1.0F, -36.0F, 9.0F, 11, 32, 11, 0.0F));

        head = new ModelRenderer(this);
        head.setRotationPoint(2.0F, 5.0F, -63.0F);
        head.cubeList.add(new ModelBox(head, 166, 75, -16.0F, -63.0F, 6.0F, 28, 24, 21, 0.0F));

        left_small_tusk_r1 = new ModelRenderer(this);
        left_small_tusk_r1.setRotationPoint(-19.0F, -26.0F, 17.0F);
        head.addChild(left_small_tusk_r1);
        setRotationAngle(left_small_tusk_r1, 0.0873F, 0.0F, 0.2182F);
        left_small_tusk_r1.cubeList.add(new ModelBox(left_small_tusk_r1, 122, 267, -1.0F, -14.0F, -1.0F, 3, 14, 3, 0.0F));

        right_small_tusk_r1 = new ModelRenderer(this);
        right_small_tusk_r1.setRotationPoint(15.0F, -26.0F, 17.0F);
        head.addChild(right_small_tusk_r1);
        setRotationAngle(right_small_tusk_r1, 0.0873F, 0.0F, -0.2182F);
        right_small_tusk_r1.cubeList.add(new ModelBox(right_small_tusk_r1, 90, 238, -2.0F, -14.0F, -1.0F, 3, 14, 3, 0.0F));

        left_ear_r1 = new ModelRenderer(this);
        left_ear_r1.setRotationPoint(-33.0F, -43.0F, 12.0F);
        head.addChild(left_ear_r1);
        setRotationAngle(left_ear_r1, 0.2722F, -0.4918F, 0.1321F);
        left_ear_r1.cubeList.add(new ModelBox(left_ear_r1, 0, 232, 11.0F, -19.0F, -1.0F, 4, 19, 19, 0.0F));

        right_ear_r1 = new ModelRenderer(this);
        right_ear_r1.setRotationPoint(29.0F, -43.0F, 12.0F);
        head.addChild(right_ear_r1);
        setRotationAngle(right_ear_r1, 0.2722F, 0.4918F, -0.1321F);
        right_ear_r1.cubeList.add(new ModelBox(right_ear_r1, 180, 225, -15.0F, -19.0F, -1.0F, 4, 19, 19, 0.0F));

        trunk = new ModelRenderer(this);
        trunk.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(trunk);
        trunk.cubeList.add(new ModelBox(trunk, 226, 225, -8.0F, -54.0F, 0.0F, 12, 27, 11, 0.0F));
        trunk.cubeList.add(new ModelBox(trunk, 148, 232, -6.0F, -27.0F, 1.0F, 8, 27, 8, 0.0F));
        trunk.cubeList.add(new ModelBox(trunk, 260, 263, -6.0F, 0.0F, 3.0F, 8, 7, 7, 0.0F));
        trunk.cubeList.add(new ModelBox(trunk, 176, 62, -6.0F, 2.0F, 10.0F, 8, 7, 5, 0.0F));
        trunk.cubeList.add(new ModelBox(trunk, 264, 100, -6.0F, -2.0F, 15.0F, 8, 8, 6, 0.0F));

        right_main_tusk = new ModelRenderer(this);
        right_main_tusk.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(right_main_tusk);


        t5_r1 = new ModelRenderer(this);
        t5_r1.setRotationPoint(13.0F, -37.0F, -22.0F);
        right_main_tusk.addChild(t5_r1);
        setRotationAngle(t5_r1, 0.2732F, -0.1058F, -0.1076F);
        t5_r1.cubeList.add(new ModelBox(t5_r1, 146, 267, -1.0F, -10.0F, -1.0F, 3, 11, 3, 0.0F));

        t4_r1 = new ModelRenderer(this);
        t4_r1.setRotationPoint(14.0F, -17.0F, -13.0F);
        right_main_tusk.addChild(t4_r1);
        setRotationAngle(t4_r1, 0.4553F, -0.0867F, -0.1307F);
        t4_r1.cubeList.add(new ModelBox(t4_r1, 106, 267, -1.0F, -21.0F, -1.0F, 4, 21, 4, 0.0F));

        t3_r1 = new ModelRenderer(this);
        t3_r1.setRotationPoint(14.0F, -14.0F, -12.0F);
        right_main_tusk.addChild(t3_r1);
        setRotationAngle(t3_r1, -0.122F, -0.1089F, -0.1622F);
        t3_r1.cubeList.add(new ModelBox(t3_r1, 208, 263, -1.0F, -5.0F, -1.0F, 5, 5, 11, 0.0F));

        t2_r1 = new ModelRenderer(this);
        t2_r1.setRotationPoint(12.0F, -16.0F, -4.0F);
        right_main_tusk.addChild(t2_r1);
        setRotationAngle(t2_r1, -0.6932F, -0.0594F, -0.1642F);
        t2_r1.cubeList.add(new ModelBox(t2_r1, 264, 81, -1.0F, -13.0F, -1.0F, 6, 13, 6, 0.0F));

        t1_r1 = new ModelRenderer(this);
        t1_r1.setRotationPoint(10.0F, -24.0F, 3.0F);
        right_main_tusk.addChild(t1_r1);
        setRotationAngle(t1_r1, -0.2156F, -0.0302F, -0.1719F);
        t1_r1.cubeList.add(new ModelBox(t1_r1, 180, 263, -1.0F, -19.0F, -1.0F, 7, 19, 7, 0.0F));

        left_main_tusk = new ModelRenderer(this);
        left_main_tusk.setRotationPoint(-4.0F, 0.0F, 0.0F);
        head.addChild(left_main_tusk);


        t5_r2 = new ModelRenderer(this);
        t5_r2.setRotationPoint(-13.0F, -37.0F, -22.0F);
        left_main_tusk.addChild(t5_r2);
        setRotationAngle(t5_r2, 0.2732F, 0.1058F, 0.1076F);
        t5_r2.cubeList.add(new ModelBox(t5_r2, 134, 267, -2.0F, -10.0F, -1.0F, 3, 11, 3, 0.0F));

        t4_r2 = new ModelRenderer(this);
        t4_r2.setRotationPoint(-14.0F, -17.0F, -13.0F);
        left_main_tusk.addChild(t4_r2);
        setRotationAngle(t4_r2, 0.4553F, 0.0867F, 0.1307F);
        t4_r2.cubeList.add(new ModelBox(t4_r2, 90, 267, -3.0F, -21.0F, -1.0F, 4, 21, 4, 0.0F));

        t3_r2 = new ModelRenderer(this);
        t3_r2.setRotationPoint(-14.0F, -14.0F, -12.0F);
        left_main_tusk.addChild(t3_r2);
        setRotationAngle(t3_r2, -0.122F, 0.1089F, 0.1622F);
        t3_r2.cubeList.add(new ModelBox(t3_r2, 260, 146, -4.0F, -5.0F, -1.0F, 5, 5, 11, 0.0F));

        t2_r2 = new ModelRenderer(this);
        t2_r2.setRotationPoint(-12.0F, -16.0F, -4.0F);
        left_main_tusk.addChild(t2_r2);
        setRotationAngle(t2_r2, -0.6932F, 0.0594F, 0.1642F);
        t2_r2.cubeList.add(new ModelBox(t2_r2, 264, 62, -5.0F, -13.0F, -1.0F, 6, 13, 6, 0.0F));

        t1_r2 = new ModelRenderer(this);
        t1_r2.setRotationPoint(-10.0F, -24.0F, 3.0F);
        left_main_tusk.addChild(t1_r2);
        setRotationAngle(t1_r2, -0.2156F, 0.0302F, 0.1719F);
        t1_r2.cubeList.add(new ModelBox(t1_r2, 260, 120, -6.0F, -19.0F, -1.0F, 7, 19, 7, 0.0F));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        front_left_foot.render(f5);
        back_left_foot.render(f5);
        body.render(f5);
        back_right_foot.render(f5);
        front_right_foot.render(f5);
        head.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}