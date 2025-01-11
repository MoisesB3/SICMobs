package com.sic.mobs.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.Material;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.UUID;

public class SkinManager {

    private final SkinDownloader skinDownloader;

    // Constructor to initialize SkinDownloader
    public SkinManager() {
        this.skinDownloader = new SkinDownloader();
    }

    /**
     * Sets a skin for the given player.
     * @param player The player to apply the skin to.
     * @param textureURL The URL of the skin texture.
     */
    public void setPlayerSkin(Player player, String textureURL) {
        try {
            // Download skin bytes from URL
            byte[] skinBytes = downloadSkin(textureURL);

            if (skinBytes != null) {
                // Apply skin using SkullMeta (Base64-encoded skin texture)
                SkullMeta skullMeta = (SkullMeta) player.getInventory().getItemInMainHand().getItemMeta();
                if (skullMeta != null) {
                    // Use Base64 encoded texture (Minecraft skin)
                    String base64Texture = encodeBase64(skinBytes);
                    skullMeta.setOwner("Custom Skin");
                    skullMeta.setDisplayName("Custom Skin");

                    // Set skin texture (Base64 encoded string)
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.randomUUID()));

                    // Set the item with new skullMeta
                    player.getInventory().getItemInMainHand().setItemMeta(skullMeta);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply skin to a mob.
     * @param mob The mob to apply the skin to.
     * @param textureURL The URL of the skin texture.
     */
    public void applySkin(Mob mob, String textureURL) {
        // You would apply the skin texture to the mob here.
        // In Minecraft, mobs do not directly have skins like players do,
        // so you may need to handle this with custom textures or model mechanisms.
    }

    /**
     * Download the skin bytes from the texture URL.
     * @param textureURL The URL of the skin texture.
     * @return The skin bytes, or null if the download fails.
     */
    private byte[] downloadSkin(String textureURL) {
        try {
            URL url = new URL(textureURL);
            InputStream in = url.openStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            in.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert byte array to Base64 string.
     * @param bytes The byte array of the skin.
     * @return The Base64 string representation of the skin.
     */
    private String encodeBase64(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
}
