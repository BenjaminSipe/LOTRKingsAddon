package com.enovak.lotrmoremobs.entity.animal;

import lotr.common.entity.animal.LOTREntityHorse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class LOTREntityMumakilGecko extends LOTREntityHorse implements IAnimatable {
    protected static final AnimationBuilder TRUNK_SWAY = new AnimationBuilder().addAnimation("Trunk Sway", true);


    private final AnimationFactory factory = new AnimationFactory( this );

    public LOTREntityMumakilGecko(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController( new AnimationController(this, "TrunkSway", 5, this::trunkSwayAnimController ));
    }

    protected <E extends LOTREntityMumakilGecko> PlayState trunkSwayAnimController( final AnimationEvent<E> event ) {
        if ( ! event.isMoving() ) {
            event.getController().setAnimation( TRUNK_SWAY );
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
