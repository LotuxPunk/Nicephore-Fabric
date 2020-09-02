package com.vandendaelen.nicephore;

import com.vandendaelen.nicephore.config.NicephoreConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class Nicephore implements ModInitializer {
    public static final String MOD_ID = "nicephore";

    @Override
    public void onInitialize() {
        AutoConfig.register(NicephoreConfig.class, GsonConfigSerializer::new);
    }
}
