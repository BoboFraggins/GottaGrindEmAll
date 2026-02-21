package com.gottagrindemall;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.gottagrindemall.config.ServerConfig;
import com.gottagrindemall.events.MobFeedInteractEvent;
import com.gottagrindemall.events.MobFeedRegistry;
import com.gottagrindemall.events.MobTransformEvent;
import com.gottagrindemall.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(GottaGrindEmAll.MOD_ID)
public class GottaGrindEmAll {
  public static final String MOD_ID = "gottagrindemall";

  public GottaGrindEmAll(IEventBus modEventBus, ModContainer container) {
    ModBlocks.init(modEventBus);
    ModItems.init(modEventBus);
    ModCreativeTab.init(modEventBus);

    container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);

    modEventBus.addListener(ModNetwork::register);

    MobFeedRegistry.register(PokemonEntity.class, ModItems.POCKET_CHOW);

    IEventBus neoBus = NeoForge.EVENT_BUS;
    neoBus.register(new MobFeedInteractEvent());
    neoBus.register(new MobTransformEvent());
  }
}
