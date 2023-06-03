package cn.yistars.resourcepack.pack;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.pack.choose.ChooseType;
import cn.yistars.resourcepack.pack.choose.PackChoose;
import com.velocitypowered.api.proxy.Player;

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

    // 全部重新发送资源包
    public static void resendPack() {
        for (Player player : BingResourcePack.instance.server.getAllPlayers()) {
            if (!player.getCurrentServer().isPresent()) continue;
            ResourcePack pack = PackChoose.getPack(player.getCurrentServer().get().getServerInfo().getName());
            if (pack == null) continue;

            pack.sendPack(player);
        }
    }

    // 根据规则重发资源包
    public static void resendPack(ChooseType type, String value) {
        switch (type) {
            case SERVER:
                for (Player player : BingResourcePack.instance.server.getAllPlayers()) {
                    if (!player.getCurrentServer().isPresent()) continue;
                    if (!player.getCurrentServer().get().getServerInfo().getName().equals(value)) continue;

                    ResourcePack pack = PackChoose.getPack(player.getCurrentServer().get().getServerInfo().getName());
                    if (pack == null) continue;

                    pack.sendPack(player);
                }
                break;
            case MATCH_RULE:
                for (Player player : BingResourcePack.instance.server.getAllPlayers()) {
                    if (!player.getCurrentServer().isPresent()) continue;
                    ResourcePack pack = PackChoose.getPack(player.getCurrentServer().get().getServerInfo().getName());
                    if (pack == null) continue;
                    if (pack.getId().equals(value)) continue;

                    pack.sendPack(player);
                }
                break;
            case PLAYER:
                for (Player player : BingResourcePack.instance.server.getAllPlayers()) {
                    if (!player.getCurrentServer().isPresent()) continue;
                    if (!player.getUsername().equals(value)) continue;

                    ResourcePack pack = PackChoose.getPack(player.getCurrentServer().get().getServerInfo().getName());
                    if (pack == null) continue;

                    pack.sendPack(player);
                }
                break;
        }
    }
}
