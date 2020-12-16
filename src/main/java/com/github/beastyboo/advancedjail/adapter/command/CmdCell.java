package com.github.beastyboo.advancedjail.adapter.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.github.beastyboo.advancedjail.application.AJail;
import org.bukkit.entity.Player;

/**
 * Created by Torbie on 15.12.2020.
 */
@CommandAlias("cell")
@Description("Cell commands for Advanced Jail.")
public class CmdCell extends BaseCommand {

    private final AJail core;

    public CmdCell(AJail core) {
        this.core = core;
    }

    @HelpCommand
    @Private
    public void cmdHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create")
    @Description("Creates a cell")
    @CommandPermission("jail.cell.create")
    public void create(Player player, @Name("jail") String jail, @Name("name") String name, @Name("limit") int limit){
        core.getAPI().createCell(jail, name, player, limit);
    }

    @Subcommand("delete")
    @Description("delete's a cell")
    @CommandPermission("jail.cell.delete")
    public void delete(Player player, @Name("jail") String jail, @Name("name") String name) {
        core.getAPI().deleteCell(jail, name, player);
    }

    @Subcommand("list")
    @Description("list all cells in a jail")
    @CommandPermission("jail.cell.list")
    public void list(Player player, @Name("jail") String jail) {
        core.getAPI().sendCellsList(jail, player);
    }

}
