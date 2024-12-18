package org.minecraft.wise.api.management;

import org.minecraft.loader.ModLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.minecraft.wise.api.config.ISavable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SavableManager {
    public static SavableManager INSTANCE;
    final Yaml yaml;
    final List<ISavable> savables = new ArrayList<>();

    public SavableManager() {
        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    public List<ISavable> getSavables() {
        return savables;
    }

    public void load() {
        File spammer = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + "spammer");

        if (!spammer.exists()) {
            spammer.mkdirs();
        }

        for (ISavable savable : this.getSavables()) {
            try {
                File file;
                File dir = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName());

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (!(file = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName() + File.separator + savable.getFileName())).exists()) {
                    file.createNewFile();
                    continue;
                }

                FileInputStream inputStream = new FileInputStream(file);
                Map map = this.yaml.load(inputStream);
                savable.load(map);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void save() throws IOException {
        System.out.println("Saving your config");
        File spammer = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + "spammer");

        if (!spammer.exists()) {
            spammer.mkdirs();
        }

        for (ISavable savable : this.getSavables()) {
            File file;
            File dir = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName());

            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (!(file = new File(ModLoader.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName() + File.separator + savable.getFileName())).exists()) {
                file.createNewFile();
            }

            try {
                yaml.dump(savable.save(), new FileWriter(file));
            } catch (Throwable exception) {
                exception.printStackTrace();
            }
        }
    }
}