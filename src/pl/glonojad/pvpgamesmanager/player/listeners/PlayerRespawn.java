package pl.glonojad.pvpgamesmanager.player.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class PlayerRespawn implements Listener{
	private HashMap<String, Integer> invincibilityCountdownIDS = new HashMap<String, Integer>();
	private int invincibilitySeconds = 5;
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
		if(GameTeam.getPlayerTeam(p).getName().equals("Spectators")) {
			//Player is in Specators! Respawning him in the map spawn.
			event.setRespawnLocation(MapManager.mapLobbyLocation);
		}
		else {
			//Setting invincibility.
			invincibilityCountdownIDS.put(p.getVanillaPlayer().getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() {
				int seconds = invincibilitySeconds;
				public void run() {
					if(seconds > 0) {
						ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PLAYER_Invincibility-Seconds", 
								ChatColor.GRAY + "You are invincible for " + ChatColor.GREEN + "{0}" + ChatColor.GRAY + " seconds!", seconds));
							p.setInvincible(true);
							seconds--;	
					}
					else {
						ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PLAYER_No-Longer-Invincible", 
								ChatColor.GRAY + "You are no longer invincible"));
						p.setInvincible(false);
						Bukkit.getScheduler().cancelTask(invincibilityCountdownIDS.get(p.getVanillaPlayer().getName()));
					}
				}
			}, 20, 20));
			event.setRespawnLocation(GameTeam.getPlayerTeam(p).getTeamSpawn(MapManager.randInt(0, GameTeam.getPlayerTeam(p).getSettings().getConfigurationPart("spawns").getKeys(false).toArray().length - 1)).location);
			p.spawnPlayer();
		}
	}
}
