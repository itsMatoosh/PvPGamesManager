package pl.glonojad.pvpgamesmanager.player.listeners;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.StatsManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.WeaponsManager;

public class PlayerExpGet implements Listener{

	@EventHandler
	public void onPlayerExpGet(PlayerExpChangeEvent event) {
		final GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
		final int playerLevel = p.getLevel();
		Bukkit.getScheduler().scheduleSyncDelayedTask(PvPGamesManager.instance, new Runnable() {
			public void run() {
				if(p.getLevel() != playerLevel) {
					//Player gained a level!
					//Broadcasting a message.
					ChatManager.broadcastMessage("PLAYER_Level-Up", 
						"{0}" + ChatColor.GOLD + " reached level " + ChatColor.YELLOW + "{1}" + ChatColor.GOLD + "!", p.getVanillaPlayer().getDisplayName(), p.getLevel());
					//Checking which weapons did player unlock.
					ArrayList<String> newGuns = WeaponsManager.checkPlayerNewUpgrades(p);
					if(newGuns.size() > 0) {
						//Player unlocked some new guns!
						//Let's tell him this.
						for(String gun : newGuns) {
							ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), 
								"WEAPON_New-Unlocked", ChatColor.YELLOW + "You have just unlocked " + ChatColor.GREEN + "{0}", gun));
						}
					}
				}
			}
		}, 2);
		//Updating player stats.
		StatsManager.savePlayerStatsToDisk(p);
	}
}
