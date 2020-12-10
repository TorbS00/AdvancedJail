package com.github.beastyboo.advancedjail.application;

import org.bukkit.plugin.java.JavaPlugin;

public final class AJailPlugin extends JavaPlugin {

    private AJail core;

    @Override
    public void onEnable() {
        core = new AJail(this);
        core.load();

    }

    @Override
    public void onDisable() {
        core.close();
        core = null;
    }

}
