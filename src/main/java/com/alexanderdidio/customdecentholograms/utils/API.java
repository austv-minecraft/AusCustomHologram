package com.alexanderdidio.customdecentholograms.utils;

import com.alexanderdidio.customdecentholograms.CustomDecentHolograms;
import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class API {
    private final CustomDecentHolograms plugin;

    public API(CustomDecentHolograms plugin) {
        this.plugin = plugin;
    }

    public boolean checkLocation(Player player) {
        String apiConfig = plugin.getAPIConfig();
        // If no API is configured, allow placement anywhere
        if (apiConfig == null || apiConfig.trim().isEmpty()) {
            return true;
        }
        switch (apiConfig.toLowerCase()) {
            case "griefprevention":
                return getGriefPrevention(player);
            case "griefdefender":
                return getGriefDefender(player);
            case "worldguard":
                return getWorldGuard(player);
            default:
                return false;
        }
    }

    public boolean validateAPI() {
        String apiConfig = plugin.getAPIConfig();
        // Allow empty API config (no protection plugin)
        if (apiConfig == null || apiConfig.trim().isEmpty()) {
            return true;
        }
        List<String> apis = new ArrayList<>();
        apis.add("griefprevention");
        apis.add("griefdefender");
        apis.add("worldguard");
        for (String api : apis) {
            if (api.equalsIgnoreCase(apiConfig)) {
                return true;
            }
        }
        return false;
    }

    private boolean getGriefPrevention(Player player) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, false, null);
        return claim != null && claim.hasExplicitPermission(player, ClaimPermission.Build);
    }

    private boolean getGriefDefender(Player player) {
        Location location = player.getLocation();
        Core core = GriefDefender.getCore();
        com.griefdefender.api.claim.Claim claim = core.getClaimAt(location);
        com.griefdefender.api.User user = core.getUser(player.getUniqueId());
        if (claim == null) {
            return false;
        }
        if (user == null) {
            return false;
        }
        return user.canBreak(location);
    }

    private boolean getWorldGuard(Player player) {
        int x = (int) player.getLocation().getX();
        int y = (int) player.getLocation().getY();
        int z = (int) player.getLocation().getZ();
        String region = plugin.getRegionConfig();
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(world);
        if (regions == null) {
            return false;
        }
        if (!regions.hasRegion(region)) {
            return false;
        }
        ProtectedRegion protectedRegion = regions.getRegion(region);
        if (protectedRegion == null) {
            return false;
        }
        return protectedRegion.contains(x, y, z);
    }
}
