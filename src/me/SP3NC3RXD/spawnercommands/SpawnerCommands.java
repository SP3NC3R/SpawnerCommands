package me.SP3NC3RXD.spawnercommands;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

/**
 * Created by Spencer on 7/2/2016.
 */
@SuppressWarnings("deprecation")
public class SpawnerCommands extends JavaPlugin implements Listener {

    public static Economy econ = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents((this), this);

        if (this.getConfig().getBoolean("disablePlugin")) {
            getLogger().info("Disabled is set to true in the config.");
            pm.disablePlugin(this);
        }

        if (!this.getConfig().getBoolean("chargeMoney")) {
            return;
        } else {
            setupEconomy();
        }
    }

    String debugPrefix = ChatColor.BOLD + "" + ChatColor.BLUE + "DEBUG> " + ChatColor.GRAY + "";
    String error = ChatColor.BOLD + "" + ChatColor.RED + "ERROR> " + ChatColor.GRAY + "";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();
        debug(p, "Testing if the block broken is a mob spawner...");

        Block block = e.getBlock();

        if (block.getType() == Material.MOB_SPAWNER) {

            debug(p, "Success! Testing for spawner type..");
            CreatureSpawner cs = (CreatureSpawner) e.getBlock().getState();




            if (cs.getSpawnedType() == EntityType.COW) {
                debug(p, "Spawner type identified. Type: Pig.");
                debug(p, "Testing permission..");
                if (!testPermission(p, "spawnercommands.pig")) {
                    return;
                }
                debug(p, "Testing for chargeMoney boolean..");
                if (!chargeMoney(p, "spawners.pig.charge", "PIG", "spawners.pig.chargeMessage")) {
                    return;
                }
                debug(p, "Executing commands...");
                dispatchCommands(p, "spawners.pig.commands");
                debug(p, "Executing messages..");
                dispatchMessages(p, "spawners.pig.messages");
            } else {
                debug(p, error + "Spawner type couldn't be identified.");
                p.sendMessage(error + "Spawner type couldn't be identified. Please try updating the plugin " +
                        "or try reporting the problem to the developer.");
                return;
            }




        } else {
            debug(p, "Not a mob spawner. Returning.");
                    return;
        }
    }

    /*
    =*=*=*= METHODS =*=*=*=
     */

    public void debug(Player p, String msg) {
        if (!this.getConfig().getBoolean("debugMode"))
        {
            return;
        } else {
            p.sendMessage(debugPrefix + msg);
            return;
        }
    }

    public void dispatchMessages(Player p, String string) {
        for (String chocolate : this.getConfig().getStringList(string)) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', chocolate.replaceAll("%player%", p.getName())));
        }
    }

    public void dispatchCommands(Player p, String string) {
        for (String chocolate : this.getConfig().getStringList(string)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), chocolate.replaceAll("%player%", p.getName()));
        }

    }
    public boolean testPermission(Player p, String perm) {
        if (!p.hasPermission(perm) || !p.isOp()) {
            debug(p, "Permission denied. You are not op, nor do you have the permission node. Returning.");
            return false;
        } else {
            if (p.hasPermission(perm) || p.isOp()) {
                debug(p, "Permission granted! You either have the permission node or you're op.");
                return true;
            }
        }
        return true;
    }
    public boolean chargeMoney(Player p, String intPath, String type, String mPath) {
        if (!this.getConfig().getBoolean("chargeMoney")) {
            debug(p, " Charge money is set to false in the config. Returning.");
            return true;
        } else {
            debug(p, "Charge money boolean set to true. Using vault currency..");
            EconomyResponse withdraw = econ.withdrawPlayer(p.getName(), this.getConfig().getInt(intPath));

            if (withdraw.transactionSuccess()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(mPath)
                        .replaceAll("%player%", p.getName())
                        .replaceAll("%amount%", String.valueOf(this.getConfig().getInt(intPath)))
                        .replaceAll("%spawner%", type)
                ));
                return true;
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("insufficientFunds")
                        .replaceAll("%player%", p.getName())
                        .replaceAll("%amount%", String.valueOf(this.getConfig().getInt(intPath)))
                        .replaceAll("%spawner%", type)
                ));
                return false;
            }
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("You must have Vault to charge players money!");
            return true;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Please get a vault supported economy if you want to use a currency!");
            return true;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
