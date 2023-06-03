package cn.yistars.resourcepack.command;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.config.LangManager;
import cn.yistars.resourcepack.config.StringUtil;
import cn.yistars.resourcepack.pack.PackManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.PluginContainer;
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
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Component.text("BingResourcePack v%version% by Bing_Yanchi".replace("%version%", getPluginVersion())));

            if (source.hasPermission("BingResourcePack.admin")) {
                source.sendMessage(LangManager.getLang("main-get-help"));
            }

            return;
        }

        if (!source.hasPermission("BingResourcePack.admin")) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                ConfigManager.reloadConfig();
                source.sendMessage(LangManager.getLang("reload-success"));
                break;
            case "help":
                for (String msg : LangManager.getString("main-command-help").split("\n")) {
                    source.sendMessage(Component.text(msg));
                }
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
            case 2:
                switch (args[0]) {
                    case "info":
                        ArrayList<String> packNames = new ArrayList<>(PackManager.packs.keySet());
                        StringUtil.copyPartialMatches(args[1], packNames, completions);
                        break;
                    case "resend":
                        String[] resendType = new String[]{"all", "server", "match-rule", "player"};
                        // 通过开头判断
                        StringUtil.copyPartialMatches(args[1], Arrays.asList(resendType), completions);
                        break;
                    default:
                        return CompletableFuture.completedFuture(new ArrayList<>());
                }
            case 3:
                if (args[0].equals("resend")) {
                    ArrayList<String> resendParameter = new ArrayList<>();
                    resendParameter.add("all");
                    resendParameter.addAll(PackManager.packs.keySet());

                    for (RegisteredServer server : BingResourcePack.instance.server.getAllServers()) {
                        resendParameter.add(server.getServerInfo().getName());
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
