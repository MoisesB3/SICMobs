package com.sic.mobs;

import com.sic.mobs.utils.SkinDownloader;  // Import SkinDownloader
import com.sic.mobs.utils.SkinManager;
import org.bukkit.entity.Player;  // Import Player
import org.bukkit.entity.Mob;  // Import Mob
import org.bukkit.plugin.java.JavaPlugin;

public class SicMobsPlugin extends JavaPlugin {

    private SkinManager skinManager;  // Declare SkinManager instance

    @Override
    public void onEnable() {
        getLogger().info("Sic Mobs Plugin Enabled!");

        // Initialize SkinManager without passing the plugin instance
        skinManager = new SkinManager(); // Assuming the constructor doesn't require parameters

        // Register commands, events, and load configurations here
        registerCommands();
        registerEvents();
        loadConfiguration();

        // Check and download skins if missing
        checkAndDownloadSkins();
    }

    @Override
    public void onDisable() {
        getLogger().info("Sic Mobs Plugin Disabled!");
    }

    private void registerCommands() {
        // Register commands
        getCommand("spawnDeadHunter").setExecutor(new com.sic.mobs.commands.SpawnDeadHunterCommand());
    }

    private void registerEvents() {
        // Register events
        getServer().getPluginManager().registerEvents(new com.sic.mobs.events.MobSpawnListener(), this);
    }

    private void loadConfiguration() {
        saveDefaultConfig(); // Save the default configuration file if not present
        getConfig().options().copyDefaults(true);
    }

    /**
     * Check and download skins if not found.
     */
    private void checkAndDownloadSkins() {
        // Define your skins and URLs here (add more as needed)
        String[] skinNames = {"deadhunter", "zombiekiller"};  // Example skin names
        String[] skinUrls = {
                "https://www.minecraftskins.com/uploads/skins/2025/01/10/deadhunter-22988728.png?v737",  // URL for deadhunter skin
                "https://www.minecraftskins.com/uploads/skins/2025/01/10/deadhunter-22988728.png?v737"  // URL for zombiekiller skin
        };

        for (int i = 0; i < skinNames.length; i++) {
            SkinDownloader.checkAndDownloadSkin(skinNames[i], skinUrls[i]);
        }
    }

    // Example method where you might want to use SkinManager
    public void applySkinToPlayer(Player player, String textureURL) {
        // Use SkinManager to set the skin for a player
        skinManager.setPlayerSkin(player, textureURL);
    }

    // Example method to apply a skin to a mob
    public void applySkinToMob(Mob mob, String textureURL) {
        // Use SkinManager to apply skin to a mob
        skinManager.applySkin(mob, textureURL);
    }
}
