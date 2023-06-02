package cn.yistars.resourcepack.pack;

import cn.yistars.resourcepack.config.ConfigManager;

import java.util.HashMap;

public class PackManager {
    public static HashMap<String, ResourcePack> packs = new HashMap<>();

    public static void reloadPack() {
        packs.clear();

        for (String key : ConfigManager.config.getSection("resource-packs").getKeys()) {
            packs.put(key, new ResourcePack(
                    key,
                    ConfigManager.config.getString("resource-packs." + key + ".url"),
                    ConfigManager.config.getBoolean("resource-packs." + key + ".force", false),
                    ConfigManager.config.getBoolean("resource-packs." + key + ".show-action-bar", true)
            ));
        }
    }
}
