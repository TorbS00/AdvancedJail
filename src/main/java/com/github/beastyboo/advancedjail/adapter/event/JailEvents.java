package com.github.beastyboo.advancedjail.adapter.event;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import org.bukkit.event.Listener;

/**
 * Created by Torbie on 15.12.2020.
 */
public class JailEvents implements Listener {

    private final AJail core;
    private final JailConfiguration api;

    public JailEvents(AJail core) {
        this.core = core;
        api = core.getAPI();
    }


}
