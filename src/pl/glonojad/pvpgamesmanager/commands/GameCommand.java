package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.command.CommandSender;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.threads.InGameCountdown;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;

public class GameCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public GameCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(args[0].equals("start")) {
			//Player wants to force start a game!
			//Checking permissions.
			if(sender.hasPermission("game.start")) {
				if(GameManager.currentGameState.equals(GameState.LOBBY)) {
					sender.sendMessage("Starting the game!");
					StartCountdown.current.currTime = 1;
				}
				else {
					sender.sendMessage("The game has already started!");
				}
			}
		}
		if(args[0].equals("end")) {
			//Player wants to force start a game!
			//Checking permissions.
			if(sender.hasPermission("game.end")) {
				if(GameManager.currentGameState.equals(GameState.IN_GAME)) {
					sender.sendMessage("Ending the game!");
					InGameCountdown.current.currTime = 1;
				}
				else {
					sender.sendMessage("The game cannot be ended now!");
				}
			}
		}
		return;
	}
}