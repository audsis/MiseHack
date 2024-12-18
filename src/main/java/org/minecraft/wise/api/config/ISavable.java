package org.minecraft.wise.api.config;

import java.util.Map;

public interface ISavable {
    void load(Map<String, Object> var1);

    Map<String, Object> save();

    String getFileName();

    String getDirName();
}