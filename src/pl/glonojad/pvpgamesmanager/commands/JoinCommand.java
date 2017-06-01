package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class JoinCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public JoinCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			GamePlayer gp = GamePlayer.getGamePlayer(player.getName());
			if(GameManager.currentGameState.equals(GameState.IN_GAME)) {
				if(GameTeam.getPlayerTeam(gp).getName().equals("Spectators")) {
					//Player isn't already in the game.
					if(gp.canJoinGame()) {
						//Player can join the game.
						GameTeam playerTeam = GameTeam.getSmallestTeam();
						playerTeam.add(gp);
						gp.spawnPlayer();
						return;
					}
					else {
						//Player can't join the game!
						ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(player, "GAME_Cant-Join", ChatColor.RED + "You can't join the game!"));
						return;
					}
				}
				else {
					//Player is already in the game.
					//He can't join the team, because this will make a bug!
					ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(player, "GAME_Already-Playing", ChatColor.RED + "You are already in the game!"));
					return;
				}
			}
			else {
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(player, "GAME_Not-Started", ChatColor.RED + "The game hasn't started yet! Wait {0} seconds!", StartCountdown.current.currTime));
				return;
			}
		}
		else {
			ChatManager.logWarning("Console can't join the game");
			return;
		}
	}
}
