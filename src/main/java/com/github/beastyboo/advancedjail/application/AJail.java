package com.github.beastyboo.advancedjail.application;

import co.aikar.commands.PaperCommandManager;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import com.github.beastyboo.advancedjail.domain.port.ConfigPort;
import com.github.beastyboo.advancedjail.domain.port.MessagePort;
import com.github.beastyboo.advancedjail.config.YamlPortConfiguration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by Torbie on 08.12.2020.
 */
public class AJail {


    /**
     * TODO:
     * Finish method(API) logics. InmateMemory left
     * Event handlers
     * Commands
     * Message system
     * Saving features, (Dazzle for YML and GSON for data)
     */

    private final JavaPlugin plugin;
    private final PaperCommandManager manager;
    private final YamlPortConfiguration<ConfigPort> configManager;
    private final YamlPortConfiguration<MessagePort> messageManager;
    private final JailConfiguration api;
    private ConfigPort config;
    private MessagePort message;
    private Economy econ = null;

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

        if (!setupEconomy() ) {
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getDescription().getName()));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }


        api.load();
        api.getJailRepository().releaseTask();
    }

    void close() {
        api.close();
    }


    public void log(String log) {
        plugin.getLogger().log(Level.INFO, log);
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

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEcon() {
        return econ;
    }
}
