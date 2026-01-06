package com.alexanderdidio.customdecentholograms.events;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import com.alexanderdidio.customdecentholograms.CustomDecentHolograms;
import com.alexanderdidio.customdecentholograms.utils.HologramFileManager;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.event.Event;
import com.griefdefender.api.event.EventManager;
import com.griefdefender.api.event.RemoveClaimEvent;
import com.griefdefender.api.event.TransferClaimEvent;
import com.griefdefender.lib.kyori.event.EventBus;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

public class GriefDefenderEvents {
    private final CustomDecentHolograms plugin;

    public GriefDefenderEvents(CustomDecentHolograms plugin) {
        this.plugin = plugin;
        subscribe();
    }

    public void subscribe() {
        EventManager eventManager = GriefDefender.getEventManager();
        EventBus<Event> eventBus = eventManager.getBus();
        eventBus.subscribe(RemoveClaimEvent.class, this::onRemoveClaim);
        eventBus.subscribe(TransferClaimEvent.class, this::onTransferClaim);
    }

    public void onRemoveClaim(RemoveClaimEvent event) {
        Claim claim = event.getClaim();
        UUID uuid = claim.getOwnerUniqueId();
        List<Hologram> holograms = plugin.getDatabase().listHolograms(uuid);
        if (holograms.size() > 0) {
            for (Hologram hologram : holograms) {
                Location location = hologram.getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                if (claim.contains(x, y, z)) {
                    DHAPI.moveHologram(hologram.getName(), plugin.getLocation());
                    HologramFileManager.ensurePlayersFolderLater(plugin, hologram.getName(), 1L);
                    plugin.getMessage().send(uuid, "hologramRelocated");
                }
            }
        }
    }

    public void onTransferClaim(TransferClaimEvent event) {
        Claim claim = event.getClaim();
        UUID oldOwner = event.getOriginalOwner();
        List<Hologram> holograms = plugin.getDatabase().listHolograms(oldOwner);
        if (holograms.size() > 0) {
            for (Hologram hologram : holograms) {
                Location location = hologram.getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                if (claim.contains(x, y, z)) {
                    DHAPI.moveHologram(hologram.getName(), plugin.getLocation());
                    HologramFileManager.ensurePlayersFolderLater(plugin, hologram.getName(), 1L);
                    plugin.getMessage().send(oldOwner, "hologramRelocated");
                }
            }
        }
    }
}
