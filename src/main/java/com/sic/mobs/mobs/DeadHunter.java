package com.sic.mobs.mobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

public class DeadHunter {

    private static final double SWITCH_DISTANCE_AXE = 7.0; // Distance to switch to axe
    private static final double SWITCH_DISTANCE_BOW = 8.0; // Minimum distance to switch to bow

    public static Skeleton spawn(Location location) {
        // Spawn the Dead Hunter mob
        Skeleton deadHunter = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        deadHunter.setCustomName("Dead Hunter");
        deadHunter.setCustomNameVisible(true);
        deadHunter.setHealth(20.0);

        // Equip the Dead Hunter with a bow in the main hand
        ItemStack bow = new ItemStack(Material.BOW);
        deadHunter.getEquipment().setItemInMainHand(bow);

        // Equip the Dead Hunter with an axe in the off-hand (representing a back item)
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        deadHunter.getEquipment().setItemInOffHand(axe);

        // Apply the custom skin to the Dead Hunter
        applyCustomSkin(deadHunter);

        // Start a repeating task to check the player's distance and switch weapons
        startWeaponSwitchTask(deadHunter);

        return deadHunter;
    }

    private static void applyCustomSkin(Skeleton skeleton) {
        // Apply the custom skin to the skeleton (Dead Hunter)
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            try {
                // Get the Base64 texture string from the PNG file inside the plugin
                String textureBase64 = getBase64FromResource("/skins/deadhunter.png");

                if (!textureBase64.isEmpty()) {
                    // Set the texture using NBT data for the skull
                    skullMeta.setOwner("MHF_Head"); // Temporary owner to satisfy the owner profile requirement
                    // Now manually set the texture using NBT
                    skullMeta.setDisplayName("Dead Hunter");

                    // Create the NBT compound to apply the texture
                    // You can use reflection to directly access the NBT data for setting the texture

                    // Create and apply the custom skin texture here using your method
                    skullMeta.setOwnerProfile(Bukkit.createProfile(UUID.randomUUID(), "Z3R0P01NT"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Set the profile to the SkullMeta
            head.setItemMeta(skullMeta);
        }
        skeleton.getEquipment().setHelmet(head);
    }

    private static String getBase64FromResource(String resourcePath) throws IOException {
        // Read the PNG file from resources and convert it to Base64
        InputStream inputStream = DeadHunter.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        // Convert InputStream to byte array
        byte[] bytes = inputStream.readAllBytes();

        // Convert byte array to Base64
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static void startWeaponSwitchTask(Skeleton deadHunter) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Find the nearest player within the world
                Player nearestPlayer = getNearestPlayer(deadHunter.getLocation());

                if (nearestPlayer != null) {
                    double distance = deadHunter.getLocation().distance(nearestPlayer.getLocation());

                    // If player is within axe range (7 blocks), switch to axe and attack
                    if (distance <= SWITCH_DISTANCE_AXE) {
                        deadHunter.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_AXE));
                        attackWithAxe(deadHunter, nearestPlayer);
                    }
                    // If player is at least 8 blocks away, switch to bow and shoot
                    else if (distance >= SWITCH_DISTANCE_BOW) {
                        deadHunter.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                        shootArrowAtPlayer(deadHunter, nearestPlayer);
                    }
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("SicMobs"), 0L, 20L); // Repeat every second (20 ticks)
    }

    private static Player getNearestPlayer(Location location) {
        Player nearestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : location.getWorld().getPlayers()) {
            double distance = location.distance(player.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                nearestPlayer = player;
            }
        }
        return nearestPlayer;
    }

    // Attack the player with an axe (melee attack)
    private static void attackWithAxe(Skeleton skeleton, Player player) {
        // Check if the skeleton is within melee range
        if (skeleton.getLocation().distance(player.getLocation()) <= 2.0) {
            // Trigger an attack using the axe (melee attack logic)
            skeleton.setTarget(player); // Set the player as the target for the skeleton
            skeleton.swingMainHand(); // This will make the skeleton swing its main hand (axe)
        }
    }

    // Shoot an arrow at the player
    private static void shootArrowAtPlayer(Skeleton skeleton, Player player) {
        // Check if the skeleton is within range to shoot an arrow
        if (skeleton.getLocation().distance(player.getLocation()) >= SWITCH_DISTANCE_BOW) {
            // Get the vector towards the player
            Vector direction = player.getLocation().toVector().subtract(skeleton.getLocation().toVector()).normalize();

            // Shoot an arrow towards the player
            skeleton.launchProjectile(org.bukkit.entity.Arrow.class, direction.multiply(1.5)); // Adjust speed if needed
        }
    }
}
