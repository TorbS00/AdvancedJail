package com.github.beastyboo.advancedjail.adapter.event;

import com.github.beastyboo.advancedjail.application.AJail;
import com.github.beastyboo.advancedjail.config.JailConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

/**
 * Created by Torbie on 15.12.2020.
 */
public class HandcuffedEvents implements Listener{

    private final AJail core;
    private final JailConfiguration api;

    public HandcuffedEvents(AJail core) {
        this.core = core;
        api = core.getAPI();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(api.getAllHandcuffedPlayers().containsKey(event.getWhoClicked().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if(api.getAllHandcuffedPlayers().containsKey(event.getWhoClicked().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        if(api.getAllHandcuffedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        if(api.getAllHandcuffedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerJoinEvent event) {
        if(api.getAllHandcuffedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            //Have to add the effect 1 second after the player joining because somehow it gets removed when I join.
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(core.getPlugin(), () -> event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2)), 20L);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleExit(final EntityDismountEvent event) {
        if(api.getAllHandcuffedPlayers().containsKey(event.getEntity().getUniqueId())){
            if(event.getDismounted().isValid()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {
        if(!e.getRightClicked().getType().equals(EntityType.PLAYER)) {
            return;
        }

        if(!e.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        Player p = e.getPlayer();
        Player target = (Player) e.getRightClicked();

        if(target == null) {
            return;
        }

        if(api.getAllHandcuffedPlayers().containsKey(target.getUniqueId())) {
            api.removeHandcuffTarget(p, target);
        } else {
            api.addHandcuffTarget(p, target);
        }
    }

}
