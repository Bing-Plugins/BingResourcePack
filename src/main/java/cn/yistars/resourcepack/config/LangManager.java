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
            case "pack-action-bar": case "unknown-pack":
                msg = msg.replace("%pack-name%", args[0]);
                break;
            case "unknown-parameter":
                msg = msg.replace("%parameter%", args[0]);
                break;
            case "success-resend-server":
                msg = msg.replace("%server-name%", args[0]);
                break;
            case "success-resend-match-rule":
                msg = msg.replace("%match-rule%", args[0]);
                break;
            case "success-resend-player":
                msg = msg.replace("%player%", args[0]);
                break;
            case "pack-info":
                msg = msg.replace("%pack-id%", args[0]);
                msg = msg.replace("%pack-name%", args[1]);
                msg = msg.replace("%pack-url%", args[2]);
                msg = msg.replace("%pack-hash%", args[3]);
                msg = msg.replace("%is-force%", args[4]);
                msg = msg.replace("%is-show-action-bar%", args[5]);
                break;
        }

        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }
}
