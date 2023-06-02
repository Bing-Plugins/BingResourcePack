package cn.yistars.resourcepack.pack;

import cn.yistars.resourcepack.config.ConfigManager;

import java.util.HashSet;

public class PackManager {
    public static HashSet<ResourcePack> packs = new HashSet<>();

    public static void reloadPack() {
        packs.clear();

        for (String key : ConfigManager.config.getSection("resource-packs").getKeys()) {
            packs.add(new ResourcePack(
                    key,
                    ConfigManager.config.getString("resource-packs." + key + ".url"),
                    ConfigManager.config.getBoolean("resource-packs." + key + ".force", false)
            ));
        }
    }
}
