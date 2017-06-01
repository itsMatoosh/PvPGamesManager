package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;

public class StatsCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public StatsCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(args.length == 0) {
			//Player wants to see his stats!
			//Checking permissions.
			if(sender.hasPermission("stats.view.self")) {
				GamePlayer gp = GamePlayer.getGamePlayer(sender.getName());
				//Stats
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
						gp.getVanillaPlayer(), 
						"STATS_YOUR-STATS", 
						ChatColor.GREEN + "" + ChatColor.BOLD + "Your current " + ChatColor.AQUA + "" + ChatColor.BOLD + "stats:"));
				//Level
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
						gp.getVanillaPlayer(), 
						"STATS_YOUR-LEVEL", 
						ChatColor.GOLD + "Level" + ChatColor.GRAY + ": " + ChatColor.YELLOW + "{0}", 
						gp.getLevel()));
				//Kills
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
						gp.getVanillaPlayer(), 
						"STATS_YOUR-KILLS", 
						ChatColor.DARK_RED + "Kills" + ChatColor.GRAY + ": " + ChatColor.YELLOW + "{0}", 
						gp.getOverallKills()));
				//Deaths
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
						gp.getVanillaPlayer(), 
						"STATS_YOUR-DEATHS", 
						ChatColor.DARK_RED + "Deaths" + ChatColor.GRAY + ": " + ChatColor.YELLOW + "{0}", 
						gp.getOverallDeaths()));
				//Points this game.
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
						gp.getVanillaPlayer(), 
						"STATS_YOUR-POINTS-IN-THIS-GAME", 
						ChatColor.RED + "Points" + ChatColor.GRAY + " (in the current game): " + ChatColor.YELLOW + "{0}", 
						gp.getPoints()));
				if(gp.getLives() != -1) {
					//Lives this game.
					ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
							gp.getVanillaPlayer(), 
							"STATS_YOUR-LIVES-IN-THIS-GAME", 
							ChatColor.RED + "Lives" + ChatColor.GRAY + " (in the current game): " + ChatColor.YELLOW + "{0}", 
							gp.getLives()));	
				}
			}
		}
		else if(args[0].equals("registeredplayers")) {
			//Player wants to see how many players are registered on server!
			//Checking permissions.
			if(sender.hasPermission("stats.view.registeredplayerscount")) {
				int registeredPlayers = 0;
				for(@SuppressWarnings("unused") String playerUUID : FileManager.getStats().getConfigurationPart("Players").getKeys(false)) {
					registeredPlayers++;
				}
				sender.sendMessage("There are " + registeredPlayers + " players registered on this server");
			}
		}
		return;
	}
}