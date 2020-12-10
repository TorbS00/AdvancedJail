package com.github.beastyboo.advancedjail.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Torbie on 10.12.2020.
 */
public class BasicUtil {

    private BasicUtil() {
        throw new AssertionError();
    }

    public static boolean isPlayerInJail(World world, String regionID, Location loc) {
        RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));

        if(regions == null) {
            return false;
        }

        ProtectedRegion region = regions.getRegion(regionID);
        if(region == null) {
            return false;
        }
        if(!(region instanceof ProtectedCuboidRegion)) {
            return false;
        }

        if(region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
            return true;
        }

        return false;
    }

}
