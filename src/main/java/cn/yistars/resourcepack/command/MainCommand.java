package cn.yistars.resourcepack.command;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.config.LangManager;
import cn.yistars.resourcepack.config.StringUtil;
import cn.yistars.resourcepack.pack.PackManager;
import cn.yistars.resourcepack.pack.ResourcePack;
import cn.yistars.resourcepack.pack.choose.ChooseType;
import cn.yistars.resourcepack.redis.RedisAddon;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Component.text("BingResourcePack v%version% by Bing_Yanchi".replace("%version%", getPluginVersion())));

            if (source.hasPermission("BingResourcePack.admin")) {
                source.sendMessage(LangManager.getLang("main-get-help"));
            }

            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!source.hasPermission("BingResourcePack.admin")) return;
                if (BingResourcePack.instance.hasRedis) {
                    RedisAddon.sendReload();
                } else {
                    ConfigManager.reloadConfig();
                }
                source.sendMessage(LangManager.getLang("reload-success"));
                break;
            case "help":
                if (!source.hasPermission("BingResourcePack.admin")) return;
                for (String msg : LangManager.getString("main-command-help").split("\n")) {
                    source.sendMessage(Component.text(msg));
                }
                break;
            case "get":
                if (!(source instanceof Player)) return;

                Player player = (Player) source;

                PackManager.resendPack(ChooseType.PLAYER, player.getUsername());
                break;
            case "resend":
                if (!source.hasPermission("BingResourcePack.admin")) return;
                if (args.length < 2) {
                    source.sendMessage(LangManager.getLang("need-parameter"));
                    return;
                }
                if (!args[1].equalsIgnoreCase("all") && args.length < 3) {
                    source.sendMessage(LangManager.getLang("need-parameter"));
                    return;
                }

                switch (args[1].toLowerCase()) {
                    case "all":
                        if (BingResourcePack.instance.hasRedis) {
                            RedisAddon.sendResend(ChooseType.ALL);
                        } else {
                            PackManager.resendPack();
                        }

                        source.sendMessage(LangManager.getLang("success-resend-all"));
                        break;
                    case "server":
                        if (BingResourcePack.instance.hasRedis) {
                            RedisAddon.sendResend(ChooseType.SERVER, args[2]);
                        } else {
                            PackManager.resendPack(ChooseType.SERVER, args[2]);
                        }

                        source.sendMessage(LangManager.getLang("success-resend-server", args[2]));
                        break;
                    case "match-rule": case "match_rule":
                        if (BingResourcePack.instance.hasRedis) {
                            RedisAddon.sendResend(ChooseType.MATCH_RULE, args[2]);
                        } else {
                            PackManager.resendPack(ChooseType.MATCH_RULE, args[2]);
                        }

                        source.sendMessage(LangManager.getLang("success-resend-match-rule", args[2]));
                        break;
                    case "player":
                        if (BingResourcePack.instance.hasRedis) {
                            RedisAddon.sendResend(ChooseType.PLAYER, args[2]);
                        } else {
                            PackManager.resendPack(ChooseType.PLAYER, args[2]);
                        }

                        source.sendMessage(LangManager.getLang("success-resend-player", args[2]));
                        break;
                    default:
                        source.sendMessage(LangManager.getLang("unknown-parameter", args[1]));
                        return;
                }
                break;
            case "info":
                if (!source.hasPermission("BingResourcePack.admin")) return;

                if (args.length < 2) {
                    source.sendMessage(LangManager.getLang("need-parameter"));
                    return;
                }

                ResourcePack pack = PackManager.packs.get(args[1]);

                if (pack == null) {
                    source.sendMessage(LangManager.getLang("unknown-pack", args[1]));
                    return;
                }

                String msg = LangManager.getString(
                        "pack-info",
                        pack.getId(),
                        pack.getName(),
                        pack.getUrl(),
                        pack.getHashString(),
                        pack.getShowForce() ? LangManager.getString("true") : LangManager.getString("false"),
                        pack.getShowActionBar() ? LangManager.getString("true") : LangManager.getString("false")
                );

                for (String line : msg.split("\n")) {
                    source.sendMessage(Component.text(line));
                }

                break;
            default:
                source.sendMessage(LangManager.getLang("unknown-command", args[0]));
                break;
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        if (!invocation.source().hasPermission("BingResourcePack.admin")) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        final List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                String[] Commands = new String[]{"help", "reload", "get", "resend", "info"};
                // 通过开头判断
                StringUtil.copyPartialMatches(args[0], Arrays.asList(Commands), completions);
                break;
            case 2:
                switch (args[0]) {
                    case "info":
                        ArrayList<String> packNames = new ArrayList<>(PackManager.packs.keySet());
                        StringUtil.copyPartialMatches(args[1], packNames, completions);
                        break;
                    case "resend":
                        String[] resendType = new String[]{"all", "server", "match-rule", "player"};
                        StringUtil.copyPartialMatches(args[1], Arrays.asList(resendType), completions);
                        break;
                    default:
                        return CompletableFuture.completedFuture(new ArrayList<>());
                }
                break;
            case 3:
                if (args[0].equals("resend")) {
                    ArrayList<String> resendParameter = new ArrayList<>();

                    switch (args[1]) {
                        case "server":
                            for (RegisteredServer server : BingResourcePack.instance.server.getAllServers()) {
                                resendParameter.add(server.getServerInfo().getName());
                            }
                            break;
                        case "match-rule":
                            resendParameter.addAll(ConfigManager.config.getSection("match-rules").getKeys());
                            break;
                        case "player":
                            for (Player player : BingResourcePack.instance.server.getAllPlayers()) {
                                resendParameter.add(player.getUsername());
                            }
                            break;
                        default:
                            return CompletableFuture.completedFuture(new ArrayList<>());
                    }

                    StringUtil.copyPartialMatches(args[2], resendParameter, completions);
                }
        }

        // 排序
        Collections.sort(completions);
        // 返回
        return CompletableFuture.completedFuture(completions);
    }

    private static String getPluginVersion() {
        PluginContainer pluginContainer = BingResourcePack.instance.server.getPluginManager().fromInstance(BingResourcePack.instance).orElseThrow(
                () -> new IllegalArgumentException("The provided instance is not a plugin"));
        return pluginContainer.getDescription().getVersion().orElse("Unknown");
    }
}
