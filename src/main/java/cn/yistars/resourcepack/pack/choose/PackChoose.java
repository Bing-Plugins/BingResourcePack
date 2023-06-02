package cn.yistars.resourcepack.pack.choose;

import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.pack.PackManager;
import cn.yistars.resourcepack.pack.ResourcePack;

public class PackChoose {
    public static ResourcePack getPack(String serverName) {

        // 先判断是否属于指定服务器名称的匹配组
        for (String key : ConfigManager.config.getSection("match-rules").getKeys()) {
            if (!PackManager.packs.containsKey(ConfigManager.config.getString("match-rules." + key + ".pack"))) continue;

            if (ConfigManager.config.contains("match-rules." + key + ".servers")) {
                if (ConfigManager.config.getStringList("match-rules." + key + ".servers").contains(serverName)) {
                    return PackManager.packs.get(ConfigManager.config.getString("match-rules." + key + ".pack"));
                }
            }
        }

        // 再判断是否符合正则表达式
        for (String key : ConfigManager.config.getSection("match-rules").getKeys()) {
            if (!PackManager.packs.containsKey(ConfigManager.config.getString("match-rules." + key + ".pack"))) continue;

            if (ConfigManager.config.contains("match-rules." + key + ".regex")) {
                if (serverName.matches(ConfigManager.config.getString("match-rules." + key + ".regex"))) {
                    return PackManager.packs.get(ConfigManager.config.getString("match-rules." + key + ".pack"));
                }
            }
        }

        // 判断默认包是否存在
        if (!PackManager.packs.containsKey(ConfigManager.config.getString("empty-resource-pack-name"))) {
            return null;
        }

        return PackManager.packs.get(ConfigManager.config.getString("empty-resource-pack-name"));
    }
}
