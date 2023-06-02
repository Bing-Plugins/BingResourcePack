package cn.yistars.resourcepack.group;

import cn.yistars.resourcepack.BingResourcePack;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import jakarta.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourcePack {
    private final String id, url;
    private String hash;
    private ResourcePackInfo.Builder packBuilder;

    public ResourcePack(String id, String url) {
        this.id = id;
        this.url = url;

        refreshPack();
    }

    // 刷新数据包
    public void refreshPack() {
        byte[] digest = getHash();

        this.hash = DatatypeConverter.printHexBinary(digest);
        this.packBuilder = BingResourcePack.instance.server.createResourcePackBuilder(url);
        packBuilder.setHash(digest);
    }

    public void sendPack(Player player) {
        player.sendResourcePackOffer(packBuilder.build());
    }

    /*
    获取在线哈希值
     */
    private byte[] getHash() {
        try {
            URL url = new URL(this.url);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            try (InputStream is = url.openStream()) {
                byte[] buffer = new byte[1024];
                for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
                    md.update(buffer, 0, len);
                }
            }

            return md.digest();

        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
