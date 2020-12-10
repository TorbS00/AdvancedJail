package com.github.beastyboo.advancedjail.application;

import co.aikar.commands.PaperCommandManager;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import com.github.beastyboo.advancedjail.domain.port.ConfigPort;
import com.github.beastyboo.advancedjail.domain.port.MessagePort;
import com.github.beastyboo.advancedjail.config.YamlPortConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Torbie on 08.12.2020.
 */
public class AJail {

    /**
     * TODO:
     *
     * 1. Jail (Create)
     *
     *  /Create
     *  /Delete
     *  /Jail list
     *  /Create release-point.
     *
     *  createJail(name)
     *  deleteJail(name)
     *  getJail(Name)
     *  getJail(Inmate)
     *  jail-releaseTask.
     *  createReleasePoint(player)
     *  getCells
     *  getPlayers
     *
     * 2. Cell (Create)
     * createCell
     * deleteCell
     * getCell(Inmate)
     * cellList
     *
     * 1. Handcuffed & Key. (Created in config) (In-game)
     *
     *
     * 3. Arrest (Inmate object)
     *
     */

    private final JavaPlugin plugin;
    private final PaperCommandManager manager;
    private final YamlPortConfiguration<ConfigPort> configManager;
    private final YamlPortConfiguration<MessagePort> messageManager;
    private final JailConfiguration api;
    private ConfigPort config;
    private MessagePort message;

    public AJail(JavaPlugin plugin) {
        this.plugin = plugin;
        manager = new PaperCommandManager(plugin);
        configManager = YamlPortConfiguration.create(plugin.getDataFolder().toPath(), "config.yml", ConfigPort.class);
        messageManager = YamlPortConfiguration.create(plugin.getDataFolder().toPath(), "message.yml", MessagePort.class);
        api = new JailConfiguration(this);
    }

    void load() {
        configManager.reloadConfig();
        messageManager.reloadConfig();
        config = configManager.getConfigData();
        message = messageManager.getConfigData();

        api.load();
        api.getJailRepository().releaseTask();
    }

    void close() {
        api.close();
    }


    public JavaPlugin getPlugin() {
        return plugin;
    }

    public ConfigPort getConfig() {
        return config;
    }

    public MessagePort getMessage() {
        return message;
    }

    public JailConfiguration getAPI() {
        return api;
    }
}
