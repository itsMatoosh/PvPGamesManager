package pl.glonojad.pvpgamesmanager.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import me.xhawk87.LanguageAPI.ISOCode;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.TimeConverter;

/**
 * The ingame countdown.
 * @author Mateusz Rebacz
 *
 */
public class InGameCountdown extends Countdown{
	/**
	 * The currently running countdown.
	 */
	public static InGameCountdown current = new InGameCountdown();
	
	protected InGameCountdown() {}
	
	@Override
	public BossBar configureBossBar()
	{
		return Bukkit.createBossBar(PvPGamesManager.language.get(ISOCode.eng, "GAME_Time-Remaining", ChatColor.AQUA + "Time Remaining: " + ChatColor.GREEN + "{0}", TimeConverter.timeConversion(currTime)), 
				BarColor.WHITE, 
				BarStyle.SEGMENTED_20);
	}
	@Override
	public String onTick() {
		if(currTime % 300 == 0 || currTime < 10) {
			//Broadcast the start game message to all players.
			ChatManager.broadcastMessage("GAME_Time-Remaining", ChatColor.AQUA + "Time Remaining: " + ChatColor.GREEN + "{0}", TimeConverter.timeConversion(currTime), TimeConverter.timeConversion(currTime));	
		}
		return PvPGamesManager.language.get(ISOCode.eng, "GAME_Time-Remaining", ChatColor.AQUA + "Time Remaining: " + ChatColor.GREEN + "{0}", TimeConverter.timeConversion(currTime));
	}
	
	/**
	 * Called when the countdown finishes.
	 * Ends the game.
	 */
	@Override
	public boolean onFinish() {
		GameManager.endGame();
		return false;
	}
}
