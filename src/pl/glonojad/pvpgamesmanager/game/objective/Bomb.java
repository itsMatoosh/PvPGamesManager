package pl.glonojad.pvpgamesmanager.game.objective;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.StructureBuilder;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

/**
 * A bomb objective.
 * @author Mateusz Rebacz
 *
 */
public class Bomb implements Objective, Listener{
	/**
	 * Name of the objective.
	 */
	private String name;
	/**
	 * Location of the objective.
	 */
	private Location location;
	/**
	 * The team owning this objective.
	 */
	private GameTeam owningTeam;
	/**
	 * Settings of this bomb.
	 */
	private ConfigurationPart settings;
	/**
	 * The name of the last player to arm the bomb.
	 */
	private String lastPlayerArmed;
	/**
	 * Time in ticks until the bomb explodes.
	 */
	private int ticksToExplode = 0;
	/**
	 * Whether the bomb is exploded.
	 */
	private Boolean isExploded = false;
	/**
	 * The area of the bomb.
	 */
	public Area bombArea;
	/**
	 * The arm percentage of the bomb.
	 */
	private float armedPercent = 0f;
	/**
	 * Whether the bomb is armed.
	 */
	private boolean isArmed;
	/**
	 * Spawns a new bomb on the specified location.
	 * @param name
	 * @param location
	 * @param gameTeam
	 * @param settings
	 */
	public Bomb(String name, Location location, GameTeam gameTeam, ConfigurationPart settings) {
		this.name = name;
		this.location = location;
		this.owningTeam = gameTeam;
		this.settings = settings;
		this.ticksToExplode = settings.getInt("explosionTicks");
		this.bombArea = StructureBuilder.createBomb(location);
		this.bombArea.getCenter().getBlock().setType(Material.GOLD_BLOCK);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, clock, 20, 20);
		//Adding Core to the sidebar.
		SidebarManager.addObjective(this);
	}
	/**
	 * Called when a player arms the bomb.
	 * @param event
	 */
	@EventHandler
	public void onPlayerArm(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			//Player clicked on block.
			if(bombArea.contains(event.getClickedBlock())) {
				//Player clicked on this bomb.
				GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
				//Deciding whether player wants to arm or disarm the bomb.
				if(owningTeam.equals(null)) {
					if(isArmed) {
						
					}
					else {
						//Player is arming the bomb.
						armedPercent = armedPercent + Float.parseFloat(settings.getString("triggerSpeed"));
						lastPlayerArmed = p.getName();
						//Sending message to arming player.
						ActionBarAPI.sendActionBar(p.getVanillaPlayer(), PvPGamesManager.language.get(p.getVanillaPlayer(), "BOMB_Arming",
								ChatColor.RED + "Arming bomb " + "{0}" + ChatColor.GRAY + " - " + ChatColor.RED + "{1}" + ChatColor.GRAY + "%", 
								ChatColor.translateAlternateColorCodes('$', settings.getString("displayName"))));
						//Starting the countdown.
						if(armedPercent > 100 || armedPercent == 100) {
							startCountdown();
						}	
					}
				}
				else if(owningTeam.equals(p.getTeam())) {
					if(isArmed) {
						//Player is disarming the bomb.
						armedPercent = armedPercent - Float.parseFloat(settings.getString("triggerSpeed"));
						//Sending message to disarming player.
						ActionBarAPI.sendActionBar(p.getVanillaPlayer(), PvPGamesManager.language.get(p.getVanillaPlayer(), "BOMB_Arming",
								ChatColor.RED + "Disarming bomb " + "{0}" + ChatColor.GRAY + " - " + ChatColor.RED + "{1}" + ChatColor.GRAY + "%", 
								ChatColor.translateAlternateColorCodes('$', settings.getString("displayName"))));
						//Starting the countdown.
						if(armedPercent < 0 || armedPercent == 0) {
							stopCountdown(p);
						}	
					}
				}
				else {
					if(isArmed) {
						
					}
					else {
						//Player is arming the bomb.
						armedPercent = armedPercent + Float.parseFloat(settings.getString("triggerSpeed"));
						lastPlayerArmed = p.getName();
						//Sending message to arming player.
						ActionBarAPI.sendActionBar(p.getVanillaPlayer(), PvPGamesManager.language.get(p.getVanillaPlayer(), "BOMB_Arming",
								ChatColor.RED + "Arming bomb " + "{0}" + ChatColor.GRAY + " - " + ChatColor.RED + "{1}" + ChatColor.GRAY + "%", 
								ChatColor.translateAlternateColorCodes('$', settings.getString("displayName"))));
						//Starting the countdown.
						if(armedPercent > 100 || armedPercent == 100) {
							startCountdown();
						}	
					}
				}
			}
		}
	}
	/**
	 * Called when a player attempts to break part of the bomb.
	 * @param event
	 */
	@EventHandler 
	public void onPlayerBreak(BlockBreakEvent event) {
		//Preventing players from breaking the bomb.
		if(bombArea.contains(event.getBlock())) {
			event.setCancelled(true);
			GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
			ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PROTECTION_Cant-Break", 
					ChatColor.RED + "You can't break blocks here!"));	
		}
	}
	/**
	 * Called when an explosion occurs next to the bomb.
	 * @param event
	 */
	@EventHandler 
	public void onExplosionBreak(EntityExplodeEvent event) {
		//Preventing explosions from breaking the bomb.
		for(Block b : event.blockList()) {
			if(bombArea.contains(b)) {
				event.blockList().clear();
				return;
			}
		}
	}
	/**
	 * Starts the bomb armed countdown.
	 */
	public void startCountdown() {
		isArmed = true;
		if(owningTeam == null) {
			ChatManager.broadcastMessage("BOMB_Triggered-Teamless", 
					ChatColor.RED + "The bomb " + "{0}" + ChatColor.RED + " has been armed by " + "{1}", 
					ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")), GamePlayer.getGamePlayer(lastPlayerArmed).getDisplayName());
		}
		else {
			ChatManager.broadcastMessage("BOMB_Triggered", 
					ChatColor.RED + "The bomb " + "{0}" + ChatColor.GRAY + "(" + "{1}" + ChatColor.GRAY + ")" + ChatColor.RED + " has been armed by " + "{2}", 
					ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")), owningTeam.getVanillaTeam().getDisplayName(), GamePlayer.getGamePlayer(lastPlayerArmed).getDisplayName());
		}
	}
	/**
	 * Called to stop the bomb countdown.
	 * @param p
	 */
	public void stopCountdown(GamePlayer p) {
		isArmed = false;
		ticksToExplode = settings.getInt("explosionTicks");
		if(owningTeam == null) {
			ChatManager.broadcastMessage("BOMB_Disarmed-Teamless", 
					ChatColor.GREEN + "The bomb " + "{0}" + ChatColor.GREEN + " has been disarmed by " + "{1}", 
					ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")), p.getDisplayName());
		}
		else {
			ChatManager.broadcastMessage("BOMB_Disarmed", 
					ChatColor.GREEN + "The bomb " + "{0}" + ChatColor.GRAY + "(" + "{1}" + ChatColor.GRAY + ")" + ChatColor.GREEN + " has been disarmed by " + "{2}", 
					ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")), owningTeam.getVanillaTeam().getDisplayName(), p.getDisplayName());
		}
	}
	
	/**
	 * Countdown runnable.
	 */
	public Runnable clock = new Runnable() {
		//This is running once a second.
		public void run() {		
			if(isArmed && !isExploded && ticksToExplode > 0) {
				//Taking away 1 explosionTick
				ticksToExplode--;
				SidebarManager.setObjectiveState(Bomb.this, ticksToExplode + "");
				if(bombArea.getCenter().getBlock().getType().equals(Material.GOLD_BLOCK)) {
					bombArea.getCenter().getBlock().setType(Material.BEACON);
				}
				if(bombArea.getCenter().getBlock().getType().equals(Material.BEACON)) {
					bombArea.getCenter().getBlock().setType(Material.GOLD_BLOCK);
				}
				//Running all onTick actions.
				if(settings.isList("actions.onTick")) {
					for (Object key : settings.getList("actions.onTick")) {
						//Firing each action.
						//Adding the arguments to the action.
						HashMap<String, Object> arguments = new HashMap<String, Object>();
						//Adding TEAM_TRIGGERED.
						arguments.put("TEAM_ARMED", GamePlayer.getGamePlayer(lastPlayerArmed).getTeam().getName());
						//Adding PLAYER_TRIGGERED.
						arguments.put("PLAYER_ARMED", lastPlayerArmed);
						//Adding TEAM_VICTIM.
						if(owningTeam == null) {
							arguments.put("TEAM_VICTIM", null);
						}
						else {
							arguments.put("TEAM_VICTIM", owningTeam.getName());	
						}
						//Adding BOMB_NAME
						arguments.put("BOMB_NAME", name);
						ActionsManager.trigger((String) key, arguments);
					}
				}
			}
			else if(!isArmed) {
				bombArea.getCenter().getBlock().setType(Material.GOLD_BLOCK);
			}
			else if(ticksToExplode == 0) {
				Explode();
			}
		}
	};
	/**
	 * Makes the bomb explode.
	 */
	public void Explode() {
		isExploded = true;
		isArmed = false;
		SidebarManager.setObjectiveState(this, "Exploded");
		HandlerList.unregisterAll(this);
		//Generating the explosion.
		MapManager.currentMap.createExplosion(bombArea.getCenter(), 10);
		//Running all onExplode actions.
		if(settings.isList("actions.onExplosion")) {
			for (Object key : settings.getList("actions.onExplosion")) {
				//Firing each action.
				//Adding the arguments to the action.
				HashMap<String, Object> arguments=new HashMap<String, Object>();
				//Adding TEAM_TRIGGERED.
				arguments.put("TEAM_SOURCE", GamePlayer.getGamePlayer(lastPlayerArmed).getTeam().getName());
				//Adding PLAYER_TRIGGERED.
				arguments.put("PLAYER_SOURCE", lastPlayerArmed);
				//Adding TEAM_VICTIM.
				if(owningTeam == null) {
					arguments.put("TEAM_VICTIM", null);
				}
				else {
					arguments.put("TEAM_VICTIM", owningTeam.getName());	
				}
				//Adding BOMB_NAME
				arguments.put("OBJECTIVE_NAME", name);
				ActionsManager.trigger((String) key, arguments);
			}
		}
	}
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return ChatColor.GREEN + "✔ ";
	}
	public GameTeam getOwningTeam() {
		return owningTeam;
	}
	public ObjectiveType getType() {
		return ObjectiveType.BOMB;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public boolean isFinished() {
		return isExploded;
	}
	public boolean isFinishable() {
		return true;
	}
}
