package cn.yistars.resourcepack.listener;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.pack.PackManager;
import cn.yistars.resourcepack.pack.ResourcePack;
import cn.yistars.resourcepack.pack.choose.PackChoose;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.concurrent.TimeUnit;

public class ServerSwitchListener {
    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent event) {
        BingResourcePack.instance.server.getScheduler().buildTask(BingResourcePack.instance, () -> {

            RegisteredServer server = event.getServer();

            ResourcePack pack = PackChoose.getPack(server.getServerInfo().getName());

            if (pack == null) return;

            if (!event.getPreviousServer().isPresent()) {
                pack.sendPack(event.getPlayer());
            } else {
                RegisteredServer previousServer = event.getPreviousServer().get();

                ResourcePack previousPack = PackChoose.getPack(previousServer.getServerInfo().getName());

                // 如果前一个服无资源包获取到或者与上一个资源包不相同则发送资源包
                if (previousPack == null || !previousPack.equals(pack)) {
                    pack.sendPack(event.getPlayer());
                }
            }

        }).delay(ConfigManager.config.getInt("global-send-delay"), TimeUnit.SECONDS).schedule();
    }
}
