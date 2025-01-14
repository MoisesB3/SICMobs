package com.sic.mobs.mobs;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class DeadHunter {

    private static final double SWITCH_DISTANCE_AXE = 7.0; // Distance to switch to axe
    private static final double SWITCH_DISTANCE_BOW = 8.0; // Minimum distance to shoot arrows
    private static final Random RANDOM = new Random();

    public static Zombie spawn(Location location) {
        // Spawn the Dead Hunter mob
        Zombie deadHunter = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        deadHunter.setCustomName("Dead Hunter");
        deadHunter.setCustomNameVisible(true);
        deadHunter.setHealth(20.0);
        deadHunter.setBaby(false); // Ensure it's an adult zombie

        // Equip the Dead Hunter with random equipment
        equipDeadHunter(deadHunter);

        // Apply the custom skin to the Dead Hunter
        applyCustomSkin(deadHunter);

        // Start a repeating task to check the player's distance and switch weapons
        startWeaponSwitchTask(deadHunter);

        return deadHunter;
    }

    private static void equipDeadHunter(Zombie deadHunter) {
        // Randomize armor
        if (RANDOM.nextDouble() < 0.5) {
            deadHunter.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        }
        if (RANDOM.nextDouble() < 0.3) {
            deadHunter.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        }
        if (RANDOM.nextDouble() < 0.4) {
            deadHunter.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        }
        if (RANDOM.nextDouble() < 0.2) {
            deadHunter.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        }

        // Randomize weapon
        ItemStack weapon;
        if (RANDOM.nextDouble() < 0.6) {
            weapon = new ItemStack(Material.WOODEN_AXE);
            if (RANDOM.nextDouble() < 0.5) {
                weapon.addEnchantment(Enchantment.SHARPNESS, 5);
            }
        } else {
            weapon = new ItemStack(Material.IRON_AXE);
            if (RANDOM.nextDouble() < 0.3) {
                weapon.addEnchantment(Enchantment.SHARPNESS, 3);
            }
        }
        deadHunter.getEquipment().setItemInMainHand(weapon);
    }

    private static void applyCustomSkin(Zombie zombie) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            try {
                String textureBase64 = getBase64FromResource("/skins/deadhunter.png");
                if (!textureBase64.isEmpty()) {
                    skullMeta.setDisplayName("Dead Hunter");
                    skullMeta.setOwnerProfile(Bukkit.createProfile(UUID.randomUUID(), "CustomHunter"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            head.setItemMeta(skullMeta);
        }
        zombie.getEquipment().setHelmet(head);
    }

    private static String getBase64FromResource(String resourcePath) throws IOException {
        InputStream inputStream = DeadHunter.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        byte[] bytes = inputStream.readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static void startWeaponSwitchTask(Zombie deadHunter) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player nearestPlayer = getNearestPlayer(deadHunter.getLocation());
                if (nearestPlayer != null && nearestPlayer.getGameMode() == GameMode.SURVIVAL) {
                    double distance = deadHunter.getLocation().distance(nearestPlayer.getLocation());
                    if (distance <= SWITCH_DISTANCE_AXE) {
                        deadHunter.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_AXE));
                        attackWithAxe(deadHunter, nearestPlayer);
                    } else if (distance >= SWITCH_DISTANCE_BOW) {
                        deadHunter.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                        shootArrowAtPlayer(deadHunter, nearestPlayer);
                    }
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("SicMobs"), 0L, 20L);
    }

    private static Player getNearestPlayer(Location location) {
        Player nearestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : location.getWorld().getPlayers()) {
            if (player.getGameMode() != GameMode.SURVIVAL) continue; // Ignore non-Survival players
            double distance = location.distance(player.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                nearestPlayer = player;
            }
        }
        return nearestPlayer;
    }

    private static void attackWithAxe(Zombie zombie, Player player) {
        if (zombie.getLocation().distance(player.getLocation()) <= 2.0) {
            zombie.setTarget(player);
        }
    }

    private static void shootArrowAtPlayer(Zombie zombie, Player player) {
        if (zombie.getLocation().distance(player.getLocation()) >= SWITCH_DISTANCE_BOW) {
            Vector direction = player.getLocation().toVector().subtract(zombie.getLocation().toVector()).normalize();
            zombie.getWorld().spawnArrow(zombie.getEyeLocation(), direction, 1.5f, 0.5f);
        }
    }
}
