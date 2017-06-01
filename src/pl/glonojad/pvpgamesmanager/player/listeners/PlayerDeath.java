package pl.glonojad.pvpgamesmanager.player.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.Flag;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.StatsManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

/**
 * Listens for the player death event.
 * @author Mateusz Rebacz
 *
 */
public class PlayerDeath implements Listener{
	@EventHandler
	public static void onPlayerDeath(PlayerDeathEvent event) {
		final Player p = event.getEntity();
		final GamePlayer gp = GamePlayer.getGamePlayer(p.getName());
		event.setKeepLevel(true);
		if(GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(p.getName())).getName().equals("Spectators")) {
			//Player is in the lobby! Let's just revive him.
		}
		else {
			//Checking if player have had a flag.
			if(Flag.holdingPlayers.containsKey(p.getName())) {
				Flag.holdingPlayers.get(p.getName()).onFlagLost(GamePlayer.getGamePlayer(p.getName()));
			}
			//Letting player choose kit again.
			gp.setCanChooseKit(true);
			//Adding player a death.
			StatsManager.addDeath(gp);
		}
		//Checking lives.
		if(GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(gp)).isSet("lives") && !GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(gp)).getString("lives").equals("")) {
			//The lives are enabled for this map.
			if(gp.getLives() != -1) {
				//This player has an active lives module.
				//Checking if player is able to respawn.
				if(gp.getLives() > 0) {
					//Taking away 1 life from player.
					gp.setLives(gp.getLives() - 1);
					if (gp.getLives() == 0) {
						//Player doesn't have lives anymore.
						//Sending player a message.
						ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(gp.getVanillaPlayer(), "DEATH-No-Lives-Left", ChatColor.RED + "You have no lives left! " + ChatColor.DARK_RED + "Goodbye!"));
						//Kicking him from the game.
						GameTeam.getPlayerTeam(gp).remove(gp);
						//Preventing player from rejoining.
						gp.setCanJoinGame(false);
						return;
					}
					//Player can play longer.
					//Sending player a message.
					ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(gp.getVanillaPlayer(), "DEATH-Lives-Left", ChatColor.RED + "You have " + ChatColor.DARK_RED + "{0}" + ChatColor.RED + " lives left!", gp.getLives()));
					return;
				}
			}
			else {
				//This player doesn't have an active lives module yet.
				gp.setLives(GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(gp)).getInt("lives"));
				//Sending player a message.
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(gp.getVanillaPlayer(), "DEATH-Lives-Left", ChatColor.RED + "You have " + ChatColor.DARK_RED + "{0}" + ChatColor.RED + " lives left!", GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(gp)).getInt("lives")));
			}
		}
	}
}
