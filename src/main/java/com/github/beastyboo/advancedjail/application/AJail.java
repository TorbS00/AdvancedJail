package com.github.beastyboo.advancedjail.application;

import co.aikar.commands.PaperCommandManager;
import com.github.beastyboo.advancedjail.adapter.command.CmdArrest;
import com.github.beastyboo.advancedjail.adapter.command.CmdCell;
import com.github.beastyboo.advancedjail.adapter.command.CmdJail;
import com.github.beastyboo.advancedjail.adapter.command.CmdRelease;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import com.github.beastyboo.advancedjail.domain.MessageType;
import com.github.beastyboo.advancedjail.domain.port.ConfigPort;
import com.github.beastyboo.advancedjail.domain.port.MessagePort;
import com.github.beastyboo.advancedjail.config.YamlPortConfiguration;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by Torbie on 08.12.2020.
 */
public class AJail {


    /**
     * TODO:
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

        this.registerCommands(manager);


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

    private void registerCommands(PaperCommandManager manager) {
        manager.enableUnstableAPI("help");

        manager.registerCommand(new CmdJail(this));
        manager.registerCommand(new CmdCell(this));
        manager.registerCommand(new CmdArrest(this));
        manager.registerCommand(new CmdRelease(this));

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            plugin.getLogger().warning("Error occured while executing command: " + command.getName());
            return false;
        });
    }

    public void message(Player player, MessageType type) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.messages().get(type)));
    }

    public void broadcast(MessageType type) {
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message.messages().get(type)), "jail.broadcast.release");
    }

    public Economy getEcon() {
        return econ;
    }

    public WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

}
