package com.bsipe.lotrkingsaddon.mixins;

import com.bsipe.lotrkingsaddon.client.render.gui.LOTRAddonGuiAnvil;
import com.bsipe.lotrkingsaddon.common.inventory.LOTRAddonContainerAnvil;
import lotr.common.LOTRCommonProxy;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LOTRCommonProxy.class) // the class targeted by this mixin
public class MixinLOTRCommonProxy { // This is an example you should delete this class

    @Inject(method = "getClientGuiElement", at = @At("HEAD"), remap = false, cancellable = true )
    private void lotrkingsaddon$getClientGuiElement(int ID, EntityPlayer entityplayer, World world, int i, int j, int k, CallbackInfoReturnable<Object> cir) {

        if ( ID == 53 ) {
            System.out.println("Replacing LOTRGuiAnvil in client CommonProxy.");
            cir.setReturnValue( new LOTRAddonGuiAnvil( entityplayer, i, j, k));

            // we know it's an anvil. So I just need to figure out what to do from here.
            // make a new container
        } else if (ID == 54) {
            Entity entity = world.getEntityByID(i);
            if (entity instanceof LOTREntityNPC) {
                System.out.println("Replacing LOTRGuiAnvil in server CommonProxy.");
                cir.setReturnValue( new LOTRAddonGuiAnvil( entityplayer , (LOTREntityNPC) entity ) );
            }
        }
        // this line of code will be injected at the end of the method "startGame" in the Minecraft class

    }

    @Inject(method = "getServerGuiElement", at = @At("HEAD"), remap = false, cancellable = true)
    private void lotrkingsaddon$getServerGuiElement(int ID, EntityPlayer entityplayer, World world, int i, int j, int k, CallbackInfoReturnable<Object> cir) {
        if ( ID == 53 ) {
            System.out.println("Replacing LOTRContainerAnvil in server CommonProxy.");
            cir.setReturnValue( new LOTRAddonContainerAnvil( entityplayer, i, j, k));
        } else if (ID == 54) {
            Entity entity = world.getEntityByID(i);
            if (entity instanceof LOTREntityNPC) {
                System.out.println("Replacing LOTRContainerAnvil in server CommonProxy.");
                cir.setReturnValue( new LOTRAddonContainerAnvil( entityplayer , (LOTREntityNPC) entity ) );
            }
        }
    }
}
