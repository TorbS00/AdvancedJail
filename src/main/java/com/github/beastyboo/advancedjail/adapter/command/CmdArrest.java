package com.github.beastyboo.advancedjail.adapter.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.beastyboo.advancedjail.application.AJail;
import org.bukkit.entity.Player;

/**
 * Created by Torbie on 16.12.2020.
 */

@CommandAlias("arrest|arresta")
@CommandPermission("jail.arrest")
@Description("Arrest command for Advanced Jail")
public class CmdArrest extends BaseCommand{

    private final AJail core;

    public CmdArrest(AJail core) {
        this.core = core;
    }

    @Default
    public void onDefault(Player player, @Name("target") OnlinePlayer target, @Name("jail") String jail, @Name("cell") String cell) {
        core.getAPI().openArrestInventory(player, target.getPlayer(), jail, cell);
    }

}
