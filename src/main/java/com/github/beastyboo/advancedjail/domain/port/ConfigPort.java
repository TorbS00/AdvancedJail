package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.entity.Crime;
import com.github.beastyboo.advancedjail.domain.entity.Handcuff;
import com.github.beastyboo.advancedjail.domain.entity.Key;
import com.github.beastyboo.advancedjail.domain.output.CrimeValueSerialiser;
import com.github.beastyboo.advancedjail.domain.output.HandcuffValueSerialiser;
import com.github.beastyboo.advancedjail.domain.output.ItemStackValueSerialiser;
import com.github.beastyboo.advancedjail.domain.output.KeyValueSerialiser;
import org.bukkit.inventory.ItemStack;
import space.arim.dazzleconf.annote.*;

import java.util.Map;

/**
 * Created by Torbie on 08.12.2020.
 */
@ConfHeader({"This plugin is created a managed by BeastCraft3/BeastyBoo", "Github for project: https://github.com/BeastyBoo/AdvancedJail \n"})
@ConfSerialisers({ItemStackValueSerialiser.class, KeyValueSerialiser.class, CrimeValueSerialiser.class, HandcuffValueSerialiser.class})
public interface ConfigPort {

    @ConfDefault.DefaultString("RoyalCity")
    @ConfKey("book-signature")
    @ConfComments({"Represent the signature of the arrest book."})
    String signature();

    @ConfDefault.DefaultBoolean(true)
    @ConfKey("return-inventory-escape")
    @ConfComments({"Represent if a player should get their inventory back when they escape a prison."})
    boolean returnInventoryEscape();

    @ConfDefault.DefaultLong(60)
    @ConfKey("call-cops-cooldown-in-seconds")
    @ConfComments({"Represent the time in seconds for when a jailed player can use broadcast item"})
    long useBroadcastItem();

    @ConfDefault.DefaultObject("com.github.beastyboo.advancedjail.util.BasicUtil.callCopsItem")
    @ConfKey("broadcast-item")
    @ConfComments({"Represent the broadcasting item for jailed players"})
    ItemStack broadcastItem();

    @ConfDefault.DefaultObject("com.github.beastyboo.advancedjail.util.BasicUtil.billItem")
    @ConfKey("bill-item")
    @ConfComments({"Represent the bill item for jailed players"})
    ItemStack billItem();

    @ConfDefault.DefaultObject("com.github.beastyboo.advancedjail.util.BasicUtil.defaultCrimes")
    @ConfKey("crimes")
    @ConfComments({"Represent the bill item for jailed players"})
    Map<String, Crime> crimes();

    @ConfDefault.DefaultObject("com.github.beastyboo.advancedjail.util.BasicUtil.defaultKeys")
    @ConfKey("keys")
    @ConfComments({"Represent the keys"})
    Map<String, Key> keys();

    @ConfDefault.DefaultObject("com.github.beastyboo.advancedjail.util.BasicUtil.defaultHandcuffs")
    @ConfKey("handcuffs")
    @ConfComments({"Represent the handcuffs"})
    Map<String, Handcuff> handcuffs();

}
