package com.github.beastyboo.advancedjail.adapter.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.github.beastyboo.advancedjail.application.AJail;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Torbie on 15.12.2020.
 */
@CommandAlias("jail")
@Description("Jail commands for Advanced Jail.")
public class CmdJail extends BaseCommand{

    private final AJail core;

    public CmdJail(AJail core) {
        this.core = core;
    }

    @HelpCommand
    @Private
    public void cmdHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Description("Creates a jail")
    @CommandPermission("jail.create")
    public void create(Player player, @Name("name") String name){

        LocalSession session = core.getWorldEdit().getSession(player);
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());

        if(session == null) {
            player.sendMessage("no selection..");
            return;
        }
        Region selection;
        try {
            selection = session.getSelection(weWorld);
        } catch (IncompleteRegionException ex) {
            player.sendMessage("no selection..");
            return;
        }
        if (!(selection instanceof CuboidRegion)) {
            player.sendMessage(ChatColor.RED + "Only cuboid regions are supported.");
            return;
        }

        core.getAPI().createJail(name, player, (CuboidRegion) selection);
    }

    @Subcommand("delete")
    @Description("Delete's a jail")
    @CommandPermission("jail.delete")
    public void delete(Player player, @Name("jail") String name){
        core.getAPI().deleteJail(name, player);
    }

    @Subcommand("item")
    @Description("Give a player arrest item")
    @CommandPermission("jail.item")
    public void item(Player player, @Name("key/handcuff") String var, @Name("name") String name){
        if(var.equalsIgnoreCase("key")) {
            core.getAPI().giveKey(player, name);
        }
        else if(var.equalsIgnoreCase("handcuff")) {
            core.getAPI().giveHandcuff(player, name);
        }
        else {
            player.sendMessage("Invalid argument. (key/handcuff)");
        }
    }

    @Subcommand("list")
    @Description("Send jail list")
    @CommandPermission("jail.list")
    public void list(Player player) {
        core.getAPI().sendJailList(player);
    }

    @Subcommand("releasepoint")
    @CommandAlias("rp")
    @Description("Create a release point for a jail")
    @CommandPermission("jail.release-point")
    public void releasePoint(Player player, @Name("jail") String jail){
        core.getAPI().createReleasePoint(jail, player);
    }

}
