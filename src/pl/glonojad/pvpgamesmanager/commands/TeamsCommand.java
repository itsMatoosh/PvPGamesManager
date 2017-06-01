package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class TeamsCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public TeamsCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(sender instanceof Player) {
			GamePlayer gp = GamePlayer.getGamePlayer(sender.getName());
			if(GameManager.currentGameState.equals(GameState.IN_GAME)) {
				//Sending Teams:
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(gp.getVanillaPlayer(), 
						"TEAMS_TEAMS", 
						ChatColor.DARK_GRAY + "Teams:"));
				//Looping through all of the teams.
				for(GameTeam t : GameTeam.getTeams()) {
					String players = "";
					for(OfflinePlayer p : t.getVanillaTeam().getPlayers()) {
						players = players + GamePlayer.getGamePlayer(p.getName()).getDisplayName() + ", ";
					}
					if(gp.getTeam().equals(t)) {
						//Checking if player is in this team.
						ChatManager.sendMessageToPlayer(gp, 
								ChatColor.YELLOW + "⟩⟩ " + t.getVanillaTeam().getDisplayName() + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + t.getVanillaTeam().getPlayers().size() + ChatColor.DARK_GRAY + "): " +  players);
						continue;
					}
					ChatManager.sendMessageToPlayer(gp, 
						t.getVanillaTeam().getDisplayName() + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + t.getVanillaTeam().getPlayers().size() + ChatColor.DARK_GRAY + "): " +  players);
				}	
			}
			else {
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(gp.getVanillaPlayer(), "GAME_Not-Started", ChatColor.RED + "The game hasn't started yet! Wait {0} seconds!", StartCountdown.current.currTime));
				return;
			}	
		}
		else {
			sender.sendMessage(ChatColor.DARK_GRAY + "Teams:");
			for(GameTeam t : GameTeam.getTeams()) { 
				sender.sendMessage(t.getVanillaTeam().getDisplayName() + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + t.getVanillaTeam().getPlayers().size() + ChatColor.DARK_GRAY + "): " +  t.getVanillaTeam().getPlayers().toString());
			}
		}
	}
}
