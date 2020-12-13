package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Inmate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 13.12.2020.
 */
public interface InmateRepository {

    void load();

    void close();

    boolean clickBillItem(Player player);

    boolean clickBroadcastItem(Player player);

    boolean arrestPlayer(Player player, Player target, String jailName, String cellName, Set<Crime> selectedCrimes);

    boolean releasePlayer(Optional<CommandSender> sender, Player target);

    Optional<ItemStack> getBillItem(UUID uuid);

    Optional<ItemStack> getBroadcastItem(UUID uuid);

    Optional<Inmate> getInmateByUUID(UUID uuid);

    Set<Inmate> getAllInmates();

}
