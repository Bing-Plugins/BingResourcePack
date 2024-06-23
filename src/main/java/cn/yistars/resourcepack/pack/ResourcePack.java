package cn.yistars.resourcepack.pack;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.ConfigManager;
import cn.yistars.resourcepack.config.LangManager;
import com.imaginarycode.minecraft.redisbungee.internal.json.JSONObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourcePack {
    @Getter
    private final String id, url, hashUrl;
    private String hash;
    @Getter
    private final Boolean showForce, showActionBar, useHash;
    private ResourcePackInfo.Builder packBuilder;

    public ResourcePack(String id, String url, String hashUrl, Boolean showForce, Boolean showActionBar, Boolean useHash) {
        this.id = id;
        this.url = url;
        this.hashUrl = hashUrl;
        this.showForce = showForce;
        this.showActionBar = showActionBar;
        this.useHash = useHash;

        refreshPack();
    }

    // 刷新数据包
    public void refreshPack() {
        this.packBuilder = BingResourcePack.instance.server.createResourcePackBuilder(url);

        if (useHash) {
            if (hashUrl != null) this.hash = getHash(hashUrl);

            if (this.hash != null) {
                setHash(this.hash);
            } else {
                byte[] digest = getHash();
                this.hash = Hex.encodeHexString(digest);
                packBuilder.setHash(getHash());
            }
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

    // 根据接口获取哈希值
    private String getHash(String hashUrl) {
        try {
            URL url = new URL(hashUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.connect();

            if (conn.getResponseCode() == 200) {
                try (InputStream is = conn.getInputStream()) {
                    // 读取响应
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // 解析 JSON 响应
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.has("hash")) {
                        return jsonResponse.getString("hash");
                    } else if (jsonResponse.has("error")) {
                        String error = jsonResponse.getString("error");
                        BingResourcePack.instance.logger.warning("Error getting " + id + " resource pack: " + error);
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
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

    public String getName() {
        if (ConfigManager.lang_config.contains("pack-name-" + id)) {
            return LangManager.getString("pack-name-" + id);
        } else {
            return LangManager.getString("pack-name-default");
        }
    }

    public String getHashString() {
        return hash;
    }
}
