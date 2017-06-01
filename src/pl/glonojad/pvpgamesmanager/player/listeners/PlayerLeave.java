package pl.glonojad.pvpgamesmanager.player.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.glonojad.pvpgamesmanager.game.objective.Flag;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.StatsManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class PlayerLeave implements Listener{
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		//Caching the player.
		GamePlayer player = GamePlayer.getGamePlayer(event.getPlayer().getName());
		//Removing player from his team.
		player.getTeam().remove(player);
		//Removing flag from player.
		if(Flag.holdingPlayers.containsKey(player.getVanillaPlayer().getName())) {
			Flag.holdingPlayers.get(player.getVanillaPlayer().getName()).onFlagLost(player);
		}
		//Updating player stats.
		StatsManager.savePlayerStatsToDisk(player);
		//Adding the quit message.
		event.setQuitMessage(null);
		ChatManager.broadcastMessage("PLAYER_Left-Server", "{0}" + ChatColor.GRAY + " left the server", player.getVanillaPlayer().getDisplayName());
	}
}