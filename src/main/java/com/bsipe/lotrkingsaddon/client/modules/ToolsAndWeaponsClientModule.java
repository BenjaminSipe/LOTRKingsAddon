package com.bsipe.lotrkingsaddon.client.modules;

import com.bsipe.lotrkingsaddon.client.render.LOTRKingsAddonItemRendererManager;
import com.bsipe.lotrkingsaddon.common.modules.ToolsAndWeaponsModule;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ToolsAndWeaponsClientModule extends ToolsAndWeaponsModule {

    public ToolsAndWeaponsClientModule(boolean SERVER_ONLY) {
        super(SERVER_ONLY);
    }

    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        if (config.loreWeapons()) {
            LOTRKingsAddonItemRendererManager.load();
        }
    }
}
