package cn.yistars.resourcepack.config;

import cn.yistars.resourcepack.BingResourcePack;
import cn.yistars.resourcepack.config.lib.Configuration;
import cn.yistars.resourcepack.config.lib.ConfigurationProvider;
import cn.yistars.resourcepack.config.lib.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    public static Configuration config, lang_config;

    public static void reloadConfig() {
        checkConfig();
        loadConfig();
    }

    private static void checkConfig() {

        Path path = Paths.get(BingResourcePack.instance.dataDirectory.toString().replace("bingresourcepack", "BingResourcePack"));

        boolean isSuccess = path.toFile().mkdir();

        File file = new File(path.toFile(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("Proxy-Config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File msg_file = new File(path.toFile(), "Lang.yml");
        if (!msg_file.exists()) {
            try (InputStream in = getResourceAsStream("Proxy-Lang.yml")) {
                Files.copy(in, msg_file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadConfig() {
        try {
            Path path = Paths.get(BingResourcePack.instance.dataDirectory.toString().replace("bingresourcepack", "BingResourcePack"));

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(path.toFile(), "config.yml"));

            lang_config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(path.toFile(), "Lang.yml"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load configuration file", e);
        }
    }

    private static InputStream getResourceAsStream(String name) {
        return BingResourcePack.instance.getClass().getClassLoader().getResourceAsStream( name );
    }
}
