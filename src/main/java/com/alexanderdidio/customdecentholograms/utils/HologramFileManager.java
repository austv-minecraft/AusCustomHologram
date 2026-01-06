package com.alexanderdidio.customdecentholograms.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;

import com.alexanderdidio.customdecentholograms.CustomDecentHolograms;

public final class HologramFileManager {
    private static final String HOLOGRAM_FOLDER_NAME = "hologramas_players";

    private HologramFileManager() {
    }

    public static void ensurePlayersFolderLater(CustomDecentHolograms plugin, String hologramName, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> ensurePlayersFolder(plugin, hologramName), delayTicks);
    }

    public static void ensurePlayersFolder(CustomDecentHolograms plugin, String hologramName) {
        try {
            File pluginsDir = plugin.getDataFolder().getParentFile();
            File hologramsRootDir = new File(new File(pluginsDir, "DecentHolograms"), "holograms");
            File hologramsPlayersDir = new File(hologramsRootDir, HOLOGRAM_FOLDER_NAME);
            //noinspection ResultOfMethodCallIgnored
            hologramsPlayersDir.mkdirs();

            Path source = new File(hologramsRootDir, hologramName + ".yml").toPath();
            Path target = new File(hologramsPlayersDir, hologramName + ".yml").toPath();

            if (Files.exists(source)) {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
            // Best-effort.
        }
    }
}
