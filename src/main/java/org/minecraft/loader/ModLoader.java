package org.minecraft.loader;

import lombok.extern.slf4j.Slf4j;
import org.minecraft.wise.impl.WiseMod;
import net.fabricmc.api.ModInitializer;

import java.io.File;

@Slf4j
public class ModLoader implements ModInitializer {
    public static final File MAIN_FOLDER = new File(System.getProperty("user.dir") + File.separator + "Wisehack");
    WiseMod wise;

    public ModLoader() {
        if (!MAIN_FOLDER.exists()) {
            MAIN_FOLDER.mkdirs();
        }
    }


    @Override
    public void onInitialize() {
        wise = new WiseMod();
        wise.init();
    }


}