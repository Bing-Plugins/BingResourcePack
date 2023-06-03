package cn.yistars.resourcepack.redis;

import cn.yistars.resourcepack.pack.choose.ChooseType;
import com.imaginarycode.minecraft.redisbungee.AbstractRedisBungeeAPI;

import java.util.Objects;

public class RedisAddon {
    public static AbstractRedisBungeeAPI redisAPI;
    public static final String DELIMITER = "§&§";
    public static final String RESEND_CHANNEL = "bing-resource-pack-resend";
    public static final String RELOAD_CHANNEL = "bing-resource-pack-reload";

    public static void initRedis() {
        redisAPI = AbstractRedisBungeeAPI.getAbstractRedisBungeeAPI();

        redisAPI.registerPubSubChannels(RESEND_CHANNEL);
        redisAPI.registerPubSubChannels(RELOAD_CHANNEL);
    }

    public static void sendReload() {
        redisAPI.sendChannelMessage(RELOAD_CHANNEL, "reload");
    }

    public static void sendSendServer(ChooseType chooseType, String... value) {
        if (Objects.requireNonNull(chooseType) == ChooseType.ALL) {
            redisAPI.sendChannelMessage(RESEND_CHANNEL, chooseType.toString());
        } else {
            String sendMsg = chooseType + DELIMITER + value[0];

            redisAPI.sendChannelMessage(RESEND_CHANNEL, sendMsg);
        }
    }
}
