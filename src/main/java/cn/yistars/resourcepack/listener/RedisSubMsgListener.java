package cn.yistars.resourcepack.listener;

import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.pack.PackManager;
import cn.yistars.resourcepack.pack.ResourcePack;
import cn.yistars.resourcepack.pack.choose.ChooseType;
import cn.yistars.resourcepack.redis.RedisAddon;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;

public class RedisSubMsgListener {
    @Subscribe
    public void onPluginMessage(PubSubMessageEvent event) {
        String[] args = event.getMessage().split(RedisAddon.DELIMITER);

        switch (event.getChannel()) {
            case RedisAddon.RELOAD_CHANNEL:
                ConfigManager.reloadConfig();
                break;
            case RedisAddon.RESEND_CHANNEL:
                ChooseType chooseType = ChooseType.valueOf(args[0]);

                if (chooseType.equals(ChooseType.ALL)) {
                    PackManager.resendPack();
                } else {
                    PackManager.resendPack(chooseType, args[1]);
                }
                break;
            case RedisAddon.HASH_CHANNEL:
                for (ResourcePack pack : PackManager.packs.values()) {
                    if (!pack.getUrl().equals(args[0])) continue;

                }
        }
    }
}
