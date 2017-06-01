package pl.glonojad.pvpgamesmanager.threads;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;

/**
 * Base for all the countdown classes.
 * @author Mateusz Rebacz
 *
 */
public abstract class Countdown {	
	/**
	 * The countdown time. 
	 */
	public int time;
	/**
	 * Current countdown time.
	 */
	public int currTime;
	/**
	 * The countdown id, used in thread identification.
	 */
	public int id;
	/** 
	 * The boss bar associcated with this countdown.
	 */
	public BossBar bossBar;
	/**
	 * Whether the countdown is running.
	 */
	public boolean running = false;
	
	/**
	 * Starts the countdown.
	 */
	public void Start() {
		//Adding the countdown to current.
		running = true;
		
		//Setting the currTime variable to the start time.
		currTime = time;
		
		//Creating the bossbar.
		if(bossBar == null) {
			bossBar = configureBossBar();
		} else {
			//Making sure the player list 
			bossBar.removeAll();
		}
		
		//Adding all the players to the bossbar.
		for(Player p : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(p);
		}
		
		//Starting the countdown.
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() {
			public void run() {
    			if(currTime > 0) {
    				//We are still counting down.
    				//Calling the onTick function.
    				bossBar.setTitle(onTick());
    				bossBar.setProgress(currTime / time);
    				
    				currTime--;
    				return;
    			}
    			if(currTime == 0) {
    				//Countdown finished.
    				//Running the onFinish function and checking whether the countdown should be reset.
    				if(onFinish()) {
    					//Resetting the timer and starting the countdown again.
    					currTime = time;
    				}
    				else {
    					End();
    				}
    			}
            }
        }, 20, 20);
	}
	/**
	 * Ends the countdown. 
	 */
	public void End() {
		//Cancelling the task.
		Bukkit.getScheduler().cancelTask(id);
		
		//Making the bossbar invisible.
		bossBar.removeAll();
		bossBar.setVisible(false);
		running = false;
	}
	/**
	 * Called when a player joins the server.
	 * @param gPlayer
	 */
	public void onPlayerJoin(GamePlayer gPlayer) {
		//Adding the joined player to the bossbar.
		if(bossBar != null) {
			bossBar.addPlayer(gPlayer.getVanillaPlayer());	
		}
	}
	
	public abstract BossBar configureBossBar();
	public abstract String onTick();
	public abstract boolean onFinish();
}