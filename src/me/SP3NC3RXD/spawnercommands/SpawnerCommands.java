package me.SP3NC3RXD.spawnercommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Spencer on 7/2/2016.
 */
@SuppressWarnings("deprecation")
public class SpawnerCommands extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
    }

    String debug = ChatColor.BOLD + "" + ChatColor.BLUE + "DEBUG> " + ChatColor.GRAY + "";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        System.out.println("block broken by: " + p);
        p.sendMessage("waddup");
        if (!this.getConfig().getBoolean("debugMode"))
        {
            return;
        } else {
            p.sendMessage(debug + "Block Broken");
            return;
        }
    }
}
