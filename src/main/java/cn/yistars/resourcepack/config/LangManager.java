package cn.yistars.resourcepack.config;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class LangManager {

    public static void sendMessage(Player player, String key, String... args) {
        String msg = ConfigManager.lang_config.getString(key);

        if (!(msg == null || msg.length() == 0)) {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            Component message = Component.text(msg);
            player.sendMessage(message);
        }
    }

    public static Component getLang(String key, String... args) {
        return Component.text(getString(key, args));
    }

    public static String getString(String key, String... args) {
        String msg = ConfigManager.lang_config.getString(key);

        switch (key) {
            case "pack-action-bar":
                msg = msg.replace("%pack-name%", args[0]);
                break;
        }

        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }
}
