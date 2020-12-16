package com.github.beastyboo.advancedjail.domain.port;

import com.github.beastyboo.advancedjail.domain.MessageType;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;

import java.util.Map;

/**
 * Created by Torbie on 08.12.2020.
 */

@ConfHeader({"This plugin is created a managed by BeastCraft3/BeastyBoo", "Github for project: https://github.com/BeastyBoo/AdvancedJail \n"})
public interface MessagePort {

    @ConfDefault.DefaultMap({"player_not_found", "&6Player not found!",
            "player_outside_cell","&6You're outside the jail",
            "player_already_jailed","&6Player is already jailed",
            "player_already_handcuffed","&6Player is already handcuffed",
            "player_handcuffed_added","&6Handcuffs added to player",
            "player_handcuffed_removed","&6Handcuffs removed from player",
            "player_arrest","&6Arrested someone",
            "player_release","&6Released someone",
            "player_not_handcuffed","&6Player is not handcuffed",
            "player_wrong_key","&6Wrong key on these handcuffs.",

            "target_handcuff_added","&6You've been handcuffed",
            "target_handcuff_removed","&6Your handcuffs are removed",
            "target_arrest","&6You've been arrested",
            "target_release","&6You've been released",

            "jail_not_found","&6Jail not found",
            "jail_already_exist","&6Jail already exist",
            "jail_regionname_already_exist","&6A region with this jail name already exist",
            "jail_created","&6Jail created",
            "jail_deleted","&6Jail deleted",
            "jail_release_point_created","&6Release point created",
            "jail_list_empty","&6No jails created",
            "jail_cell_list_empty","&6No cells in this jail yet",
            "jail_inmate_list_empty","&6No inmates in this jail yet.",

            "cell_name_taken","&6Cell name taken.",
            "cell_size_invalid","&6Cell size is invalid",
            "cell_created","&6Cell created",
            "cell_not_found","&6Cell not found",
            "cell_is_full","&6Cell is full",
            "cell_deleted","&6Cell deleted",

            "handcuff_not_found","&6Handcuffs not found",
            "handcuff_given","&6Handcuffs given.",

            "inmate_not_found","&6Inmate not found",
            "inmate_already_exist","&6Inmate already exist",
            "inmate_bill_not_loaded","&6Bill item not loaded. Tell admin!",
            "inmate_broadcast_not_loaded","&6Broadcast item not loaded. Tell admin!",
            "inmate_not_enough_money","&6Not enough money!",
            "inmate_still_in_cooldown","&6You can't use this feature yet. ",
            "inmate_broadcast_item_used","&6Player used broadcast item!",
            "inmate_broadcast_sent","&6You sent a broadcast!",
            "inmate_release_broadcast","&6Player was released from jail!",
            "inmate_escape_broadcast","&6Player has escaped from jail!",

            "key_not_found","&6Key not found",
            "key_given","&6Key given"
    })
    Map<MessageType, String> messages();

}
