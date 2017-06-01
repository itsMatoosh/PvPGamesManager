package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class LeaveCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public LeaveCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			GamePlayer gp = GamePlayer.getGamePlayer(player.getName());
			if(GameManager.currentGameState == GameState.IN_GAME) {
				//Removing player from his current team.
				GameTeam.getPlayerTeam(gp).remove(gp);
				//Teleporting player to map main spawn.
				player.teleport(MapManager.mapLobbyLocation);
				return;
			}
			else {
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(player, "GAME_Not-Started", ChatColor.RED + "The game hasn't started yet! Wait {0} seconds!", StartCountdown.current.currTime));
				return;
			}
		}
		else {
			ChatManager.logWarning("Console can't leave the game!");
		}
	}
}
