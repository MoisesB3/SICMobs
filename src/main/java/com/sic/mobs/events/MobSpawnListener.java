package com.sic.mobs.events;

import com.sic.mobs.mobs.DeadHunter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class MobSpawnListener implements Listener {

    private static final Random random = new Random();

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        Location location = event.getLocation();
        World world = location.getWorld();

        if (world != null && location.getBlock().getBiome() == Biome.FOREST) {
            if (random.nextInt(100) < 10) { // 10% chance to spawn a Dead Hunter
                DeadHunter.spawn(location);
                event.setCancelled(true); // Cancel the default spawn
            }
        }
    }
}
