package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.materials.world.OreGenerator;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.setup.AllomancyConfig;
import com.legobmw99.allomancy.setup.AllomancySetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Allomancy.MODID)
public class Allomancy {

    public static final String MODID = "allomancy";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Allomancy instance;

    public Allomancy() {
        instance = this;
        // Register our setup events on the necessary buses
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AllomancySetup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AllomancySetup::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AllomancyConfig::refresh);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PowersClientSetup::registerParticle);
        MinecraftForge.EVENT_BUS.addListener(AllomancySetup::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, OreGenerator::registerGeneration);

        // Config init
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AllomancyConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AllomancyConfig.CLIENT_CONFIG);


        // Register all Registries
        PowersSetup.register();
        CombatSetup.register();
        ConsumeSetup.register();
        MaterialsSetup.register();
        ExtrasSetup.register();

    }

}
