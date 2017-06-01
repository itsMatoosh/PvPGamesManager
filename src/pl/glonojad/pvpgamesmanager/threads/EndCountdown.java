package pl.glonojad.pvpgamesmanager.threads;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.xhawk87.LanguageAPI.ISOCode;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.RotationManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;

/**
 * The end countdown of the game.
 * @author Mateusz Rebacz
 *
 */
public class EndCountdown extends Countdown
{
	/** Custom win message for the countdown. */
	public String customWinMessage = null;
	/** Winners to broadcast during the countdown. */
	public ArrayList<String> winners = new ArrayList<String>();
	/** Currently running countdown. */
	public static EndCountdown current;
	
	/**
	 * Creates a new ending countdown.
	 * @param winners
	 * @param customWinMessage
	 */
	public EndCountdown(ArrayList<String> winners, String customWinMessage) {
		current = this;
		this.time = 30;
		this.winners = winners;
		this.customWinMessage = customWinMessage;
	}

	public EndCountdown() {}

	/**
	 * Configures the boss bar message.
	 */
	@Override
	public BossBar configureBossBar() {
		return Bukkit.createBossBar(PvPGamesManager.language.get(ISOCode.eng, "GAME_Thanks-For-Playing",ChatColor.DARK_RED + "❤ " + ChatColor.GREEN + "" + ChatColor.BOLD + "Thanks for playing!" + ChatColor.DARK_RED + " ❤"), 
				BarColor.PINK, 
				BarStyle.SEGMENTED_10, 
				BarFlag.PLAY_BOSS_MUSIC, 
				BarFlag.DARKEN_SKY);
	}
	@Override
	public String onTick() {
		//Sending the end game message to all the players.
		if(currTime % 10 == 0 || currTime < 10) {
			//Broadcast the start game message to all players.
			ChatManager.broadcastMessage("GAME_Endgame-Timer",ChatColor.GREEN + "Server will restart in " + ChatColor.YELLOW + "{0}" + ChatColor.GREEN + " seconds!", currTime);
		}
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			//Setting a screen title with the win message.
			if(customWinMessage == null) {
				//No win message specified, constructing the message based on the winners list.
				customWinMessage = "";
				
				//Constructing message for more than one winner.
				if(winners.size() > 1) {
					String winnersCombined = "";
					for(int i = 0; i < (winners.size()); i++) {
						winnersCombined += winners.get(i) + " " + PvPGamesManager.language.get(p, "AND", "and") + " ";
					}
					winnersCombined += winners.get(winners.size() - 1);
					
					if(customWinMessage == "") {
						customWinMessage = PvPGamesManager.language.get(p, "GAME_Draw_Between", ChatColor.YELLOW + "Draw between ") + winnersCombined;
					}
					else {
						customWinMessage = customWinMessage + " " + PvPGamesManager.language.get(p, "AND", "and") + " " + winnersCombined;	
					}
				} 
				//Constructing message for one winner.
				else {
					customWinMessage = PvPGamesManager.language.get(p, "GAME_Winner", "{0}" + ChatColor.GREEN + " won the game!", winners.get(0));	
				}
			}

			//Showing the constructed win message.
			p.sendTitle(customWinMessage, PvPGamesManager.language.get(p, "SERVER_Restart-Soon", ChatColor.YELLOW + "The server will restart in " + ChatColor.RED + "{0}" + ChatColor.YELLOW + " seconds!", currTime), 0, 200, 100);
		}
		
		return PvPGamesManager.language.get(ISOCode.eng, "GAME_Thanks-For-Playing",ChatColor.DARK_RED + "❤ " + ChatColor.GREEN + "" + ChatColor.BOLD + "Thanks for playing!" + ChatColor.DARK_RED + " ❤");
	}
	@Override
	public boolean onFinish() {
		//The endgame countdown finished.
		for(Player p : Bukkit.getOnlinePlayers()) {
			GamePlayer.getGamePlayer(p.getName()).kick(PvPGamesManager.language.get(p, "GAME_End-Kick", "{0}" + "\n" + ChatColor.GOLD + "The next game will begin in few seconds! " + ChatColor.RED + "Please rejoin the server!", customWinMessage));
		}
		//Cancelling the end countdown.
		Bukkit.getScheduler().cancelTask(id);
		//Unloading the current map.
		Bukkit.getServer().unloadWorld(MapManager.currentMap, true);
		//Setting the last game map.
		FileManager.getRotation().set("LastGameMap", MapManager.currentMapFolder.getName());
		RotationManager.saveCurrentStats();
		//Deleting the current map.
		RotationManager.deleteOldMap();
		
		//Collecting system garbage.
		System.gc();
		//Reloading the plugin.
		Bukkit.shutdown();
		
		return false;
	}
} 
