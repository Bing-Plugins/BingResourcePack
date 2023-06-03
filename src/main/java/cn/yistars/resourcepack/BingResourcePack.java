package cn.yistars.resourcepack;

import cn.yistars.resourcepack.command.MainCommand;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.listener.ServerSwitchListener;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "bingresourcepack", name = "BingResourcePack", version = "1",
        url = "https://www.yistars.net/", description = "Bing Resource Pack", authors = {"BingYanchi"})
public class BingResourcePack {
    public static BingResourcePack instance;
    public final ProxyServer server;
    public final Logger logger;
    public final Path dataDirectory;

    @Inject
    public BingResourcePack(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;

        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        ConfigManager.reloadConfig();

        logger.info("Enabled successfully.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new ServerSwitchListener());

        CommandManager commandManager = server.getCommandManager();

        CommandMeta mainCommandMeta = commandManager.metaBuilder("bingresourcepack")
                .plugin(this)
                .aliases("bsp")
                .build();

        SimpleCommand mainCommand = new MainCommand();

        commandManager.register(mainCommandMeta, mainCommand);
    }
}