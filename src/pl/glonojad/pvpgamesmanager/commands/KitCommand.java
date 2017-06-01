package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.KitManager;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class KitCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public KitCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			GamePlayer gp = GamePlayer.getGamePlayer(player.getName());
			if(GameManager.currentGameState == GameState.IN_GAME) {
				KitManager.openKitMenu(gp);
				return;
			}
			else {
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(player, "GAME_Not-Started", ChatColor.RED + "The game hasn't started yet! Wait {0} seconds!", StartCountdown.current.currTime));
				return;
			}
		}
		else {
			ChatManager.logWarning("Console can't user kit menu!");
		}
	}
}
