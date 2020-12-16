package com.github.beastyboo.advancedjail.adapter.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.beastyboo.advancedjail.application.AJail;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Torbie on 16.12.2020.
 */

@CommandAlias("release|scarcera")
@CommandPermission("jail.release")
@Description("Release command for Advanced Jail")
public class CmdRelease extends BaseCommand{

    private final AJail core;

    public CmdRelease(AJail core) {
        this.core = core;
    }

    @Default
    public void onDefault(Player player, @Name("target") OnlinePlayer target) {
        core.getAPI().releasePlayer(Optional.of(player), target.getPlayer(), false);
    }

}
