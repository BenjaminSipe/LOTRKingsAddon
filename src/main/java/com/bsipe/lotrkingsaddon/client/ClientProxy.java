package com.bsipe.lotrkingsaddon.client;

import com.bsipe.lotrkingsaddon.Config;
import com.bsipe.lotrkingsaddon.client.modules.MoreMoneyClientModule;
import com.bsipe.lotrkingsaddon.client.modules.ToolsAndWeaponsClientModule;
import com.bsipe.lotrkingsaddon.common.CommonProxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        Config.ModuleLevelConfig configuredModules = Config.getModuleLevelConfig();

        if (configuredModules.moreMoneyModule()) modules.add(new MoreMoneyClientModule(CommonProxy.SERVER_ONLY));
        if (configuredModules.toolsAndWeaponsModuleConfig())
            modules.add(new ToolsAndWeaponsClientModule(CommonProxy.SERVER_ONLY));

        super.preInit(event);
    }

}
