package com.enovak.lotrmoremobs;

import com.enovak.lotrmoremobs.entity.animal.LOTREntityMumakil;
import com.enovak.lotrmoremobs.render.entity.LOTRRenderMumakil;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import lotr.client.render.entity.LOTRRenderRhino;
import lotr.common.entity.LOTREntities;
import lotr.common.entity.animal.LOTREntityRhino;


@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION )
public class Main {

    public static final String MODID = "lotrmoremobs";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "LOTR More Mobs File";


    @EventHandler
    public void preInit( FMLPreInitializationEvent event) {


    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {

        // {
        // CODE THAT SHOULD ONLY BE RUN ON THE SERVER
        LOTREntities.registerCreature(LOTREntityMumakil.class, "Mumakil", 811, 6118481, 12171165);


        // CODE THAT SHOULD ONLY BE RUN ON THE CLIENT
        RenderingRegistry.registerEntityRenderingHandler(LOTREntityMumakil.class, new LOTRRenderMumakil());
        // } CODE THAT IS CURRENTLY RUNNING ON BOTH.
    }

    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {

    }
}