package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.entity.Crime;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Torbie on 12.12.2020.
 */
public interface CrimeRepository {

    void load();

    boolean openArrestInventory(Player player, Player target, String jailName, String cellName);

    Optional<Crime> getCrimeByName(String name);

}
