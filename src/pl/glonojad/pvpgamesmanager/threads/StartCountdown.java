package pl.glonojad.pvpgamesmanager.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import me.xhawk87.LanguageAPI.ISOCode;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;

/**
 * The start countdown of the game.
 * @author Mateusz Rebacz
 *
 */
public class StartCountdown extends Countdown {
	/**
	 * Currently running countdown.
	 */
	public static StartCountdown current;
	
	/**
	 * Creates a start countdown.
	 */
	public StartCountdown() {
		current = this;
		time = 60;
	}
	
	@Override
	public BossBar configureBossBar() {
		return Bukkit.createBossBar(PvPGamesManager.language.get(ISOCode.eng,"GAME_Pregame-Timer-BarAPI",ChatColor.GREEN + "The game will start in " + ChatColor.YELLOW + "{0}" + ChatColor.GREEN + " seconds!", currTime), 
				BarColor.BLUE,
				BarStyle.SEGMENTED_10,
				BarFlag.PLAY_BOSS_MUSIC,
				BarFlag.DARKEN_SKY);
	}

	@Override
	public String onTick() {
		//Broadcasting message in chat.
		if(currTime % 10 == 0 || currTime < 10) {
			//Broadcast the start game message to all players.
			ChatManager.broadcastMessage("GAME_Pregame-Timer",ChatColor.GREEN + "The game will start in " + ChatColor.YELLOW + "{0}" + ChatColor.GREEN + " seconds!", currTime);	
		}
		
		return PvPGamesManager.language.get(ISOCode.eng,"GAME_Pregame-Timer-BarAPI",ChatColor.GREEN + "The game will start in " + ChatColor.YELLOW + "{0}" + ChatColor.GREEN + " seconds!", currTime);
	}

	@Override
	public boolean onFinish() {
		//Checking if there are enough players on the server.
		if(Bukkit.getOnlinePlayers().toArray().length < FileManager.getConfig().getInt("Min-Players-To-Start")) {
			//Let's reset the timer and start the countdown again!
			ChatManager.log(ChatColor.DARK_RED + "Not enought players! " + ChatColor.RED + "Timer resets!");
			ChatManager.broadcastMessage("GAME_Not-Enought-Players",ChatColor.DARK_RED + "Not enought players!" + ChatColor.RED + " Timer resets!");
			return true;
		}
		else {
			GameManager.startGame();
			return false;
		}
	}
}
