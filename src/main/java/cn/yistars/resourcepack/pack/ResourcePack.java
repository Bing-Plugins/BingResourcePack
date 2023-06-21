package cn.yistars.resourcepack.pack;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.config.LangManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourcePack {
    private final String id, url;
    private String hash;
    private final Boolean showForce, showActionBar, useHash;
    private ResourcePackInfo.Builder packBuilder;

    public ResourcePack(String id, String url, Boolean showForce, Boolean showActionBar, Boolean useHash) {
        this.id = id;
        this.url = url;
        this.showForce = showForce;
        this.showActionBar = showActionBar;
        this.useHash = useHash;

        refreshPack();
    }

    // 刷新数据包
    public void refreshPack() {
        this.packBuilder = BingResourcePack.instance.server.createResourcePackBuilder(url);

        if (useHash) {
            byte[] digest = getHash();
            this.hash = Hex.encodeHexString(digest);

            packBuilder.setHash(digest);
        }

        packBuilder.setShouldForce(showForce);

        // 1.17+ 资源包提示信息
        if (ConfigManager.lang_config.contains("pack-prompt-" + id)) {
            packBuilder.setPrompt(LangManager.getLang("pack-prompt-" + id));
        } else {
            packBuilder.setPrompt(LangManager.getLang("pack-prompt-default"));
        }
    }

    public void sendPack(Player player) {
        player.sendResourcePackOffer(packBuilder.build());

        // 如果显示 action-bar 则发出提示语句
        if (showActionBar) {
            player.sendActionBar(LangManager.getLang("pack-action-bar", getName()));
        }
    }

    // 获取在线哈希值
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

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getHashString() {
        return hash;
    }

    public Boolean getShowForce() {
        return showForce;
    }

    public Boolean getShowActionBar() {
        return showActionBar;
    }

    public String getName() {
        if (ConfigManager.lang_config.contains("pack-name-" + id)) {
            return LangManager.getString("pack-name-" + id);
        } else {
            return LangManager.getString("pack-name-default");
        }
    }

    public void setHash(String hash) {
        this.hash = hash;

        // 将 hash 字符串转换为字节数组
        byte[] digest = new byte[hash.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = hash.substring(i * 2, i * 2 + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }

        this.packBuilder.setHash(digest);
    }
}
