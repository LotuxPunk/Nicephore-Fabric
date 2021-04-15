package com.vandendaelen.nicephore;

import com.vandendaelen.nicephore.config.NicephoreConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class Nicephore implements ModInitializer {
    public static final String MOD_ID = "nicephore";

    @Override
    public void onInitialize() {
        AutoConfig.register(NicephoreConfig.class, GsonConfigSerializer::new);
    }
}
