package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.entity.Handcuff;
import com.github.beastyboo.advancedjail.domain.entity.Key;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Torbie on 12.12.2020.
 */

public interface HandcuffRepository {

    void load();

    boolean addHandcuffTarget(Player player, Player target);

    boolean removeHandcuffTarget(Player player, Player target);

    boolean giveHandcuff(Player player,  String name);

    boolean giveKey(Player player, String name);

    Optional<Key> getKeyByName(String name);

    Optional<Key> getKeyByItemStack(ItemStack itemStack);

    Optional<Handcuff> getHandcuffByName(String name);

    Optional<Handcuff> getHandcuffByItemStack(ItemStack itemStack);

    Set<Key> getAllKeys();

    Set<Handcuff> getAllHandcuffs();

    Map<UUID, Handcuff> getAllHandcuffedPlayers();

}
