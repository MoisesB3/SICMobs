package com.sic.mobs.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class SkinDownloader {

    private static final String SKINS_FOLDER_PATH = "plugins/SicMobs/skins/";

    /**
     * Checks if the skin exists in the specified directory. If not, it downloads it.
     * @param skinName The name of the skin (without extension).
     * @param skinUrl  The URL where the skin is hosted.
     */
    public static void checkAndDownloadSkin(String skinName, String skinUrl) {
        File skinsDir = new File(SKINS_FOLDER_PATH);
        if (!skinsDir.exists()) {
            skinsDir.mkdirs(); // Create the skins directory if it doesn't exist
        }

        // Define the file path where the skin will be stored
        File skinFile = new File(skinsDir, skinName + ".png");

        // Check if the skin file already exists
        if (!skinFile.exists()) {
            Bukkit.getLogger().info("[SIC] Skin " + skinName + ".png not found. Downloading...");
            downloadSkin(skinUrl, skinFile, skinName);
        } else {
            Bukkit.getLogger().info("[SIC] Skin " + skinName + ".png found, no need to download.");
        }
    }

    /**
     * Downloads the skin from the provided URL and saves it to the specified file.
     * @param skinUrl The URL of the skin image.
     * @param skinFile The file to save the skin.
     * @param skinName The name of the skin (used for renaming the downloaded file).
     */
    private static void downloadSkin(String skinUrl, File skinFile, String skinName) {
        try (InputStream in = new URL(skinUrl).openStream()) {
            // Download the file and write it to the skinFile
            Files.copy(in, skinFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Bukkit.getLogger().info("[SIC] Skin " + skinName + ".png downloaded successfully.");
        } catch (IOException e) {
            Bukkit.getLogger().warning("[SIC] Failed to download skin " + skinName + ".png: " + e.getMessage());
        }
    }

    /**
     * Renames the downloaded PNG to the desired name if necessary.
     * @param oldName The original downloaded file name.
     * @param newName The new name for the skin file.
     */
    public static void renameSkinFile(String oldName, String newName) {
        File oldFile = new File(SKINS_FOLDER_PATH + oldName + ".png");
        File newFile = new File(SKINS_FOLDER_PATH + newName + ".png");

        if (oldFile.exists() && !newFile.exists()) {
            boolean renamed = oldFile.renameTo(newFile);
            if (renamed) {
                Bukkit.getLogger().info("[SIC] Skin renamed to " + newName + ".png.");
            } else {
                Bukkit.getLogger().warning("[SIC] Failed to rename the skin to " + newName + ".png.");
            }
        }
    }
}
