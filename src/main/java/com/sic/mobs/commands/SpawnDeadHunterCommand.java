package com.sic.mobs.commands;

import com.sic.mobs.mobs.DeadHunter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnDeadHunterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        DeadHunter.spawn(location);
        player.sendMessage("Spawned a Dead Hunter at your location!");
        return true;
    }
}
