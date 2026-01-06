package com.alexanderdidio.customdecentholograms.commands;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.alexanderdidio.customdecentholograms.CustomDecentHolograms;
import com.alexanderdidio.customdecentholograms.utils.Database;
import com.alexanderdidio.customdecentholograms.utils.HologramFileManager;
import com.alexanderdidio.customdecentholograms.utils.Message;

import eu.decentsoftware.holograms.api.holograms.Hologram;

public class HologramDelete implements CommandExecutor {
    private final CustomDecentHolograms plugin;

    public HologramDelete(CustomDecentHolograms plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Database database = plugin.getDatabase();
        Message message = plugin.getMessage();
        boolean permission = sender.hasPermission("cdh.delete");
        boolean console = sender instanceof ConsoleCommandSender;

        if (!permission || console) {
            message.send(sender, "noPermission");
            return true;
        }

        if (args.length != 2) {
            message.send(sender, "usageDelete");
            return true;
        }

        Player player = Bukkit.getPlayer(sender.getName());
        if (player == null) {
            message.send(sender, "invalidSender");
            return true;
        }

        UUID uuid = player.getUniqueId();
        int hologramAmount = database.countHolograms(uuid);

        if (hologramAmount == 0) {
            message.send(sender, "invalidList");
            return true;
        }

        String hologramId = args[1];
        if (!hologramId.matches("\\d+")) {
            message.send(sender, "invalidHologram");
            return true;
        }

        int index = Integer.parseInt(hologramId) - 1;
        if (!database.validateHologram(uuid, index)) {
            message.send(sender, "invalidHologram");
            return true;
        }

        Hologram hologram = database.getHologram(uuid, index);
        if (hologram != null) {
            hologram.delete();
            HologramFileManager.deleteHologramFiles(plugin, hologram.getName());
        }

        try {
            database.deleteHologram(uuid, index);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        message.send(sender, "hologramDelete", hologramId);
        return true;
    }
}
