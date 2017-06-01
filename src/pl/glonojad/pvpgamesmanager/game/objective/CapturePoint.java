package pl.glonojad.pvpgamesmanager.game.objective;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.listeners.ProtectionManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.StructureBuilder;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

/**
 * Represents a capture point objective.
 * @author Mateusz Rebacz
 *
 */
public class CapturePoint implements Objective, Listener {
	/**
	 * The name of the objective.
	 */
	private String name;
	/**
	 * The team owning the objective.
	 */
	private GameTeam owningTeam;
	/**
	 * Settings of the objective.
	 */
	private ConfigurationPart settings;
	/**
	 * The time is takes for one player to capture the point.
	 */
	private int captureTime;
	/**
	 * The area the player has to be in in order to capture the point.
	 */
	private Area captureArea;
	/**
	 * The glass block in the center of the point.
	 */
	private Location glassBlock;
	/**
	 * The wool blocks that change color as the player captures the point.
	 */
	private ArrayList<Block> woolBlocks = new ArrayList<Block>();
	
	//Capture shit.
	private int timeToCapture = 0;
	private float capturePercent;
	private boolean capturing = false;
	private HashMap<String,Integer> captureTimerIDS = new HashMap<String, Integer>();
	private GameTeam controllingTeam;
	
	/**
	 * Spawns a new Capture Point on a specified location.
	 * @param name
	 * @param gameTeam
	 * @param location
	 * @param settings
	 * @param captureTime
	 */
	public CapturePoint(String name, GameTeam gameTeam, Location location, ConfigurationPart settings, int captureTime) {
		this.name = name;
		this.owningTeam = gameTeam;
		this.settings = settings;
		this.captureTime = captureTime;
		this.captureArea = StructureBuilder.createCapturePoint(location);
		this.glassBlock = location;
		this.glassBlock.getBlock().setType(Material.STAINED_GLASS);
		//Adding protection area to the CapturePoint.
		ProtectionManager.createArea(name, 
			new Location(MapManager.currentMap, this.glassBlock.getX() - 4, this.glassBlock.getY() - 3, this.glassBlock.getZ() - 4), 
			new Location(MapManager.currentMap, this.glassBlock.getX() + 4, this.glassBlock.getY() + 3, this.glassBlock.getZ() + 4),
			settings.getConfigurationPart("protection"));
		for(Block b : new Area(new Location(MapManager.currentMap, this.glassBlock.getX() - 4, this.glassBlock.getY() - 3, this.glassBlock.getZ() - 4), new Location(MapManager.currentMap, this.glassBlock.getX() + 4, this.glassBlock.getY() + 3, this.glassBlock.getZ() + 4))) {
			if(b.getType().equals(Material.WOOL)) {
				woolBlocks.add(b);
			}
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, clock, 20, 20);
		//Adding Core to the sidebar.
		SidebarManager.addObjective(this);
	}
	/**
	 * Called when a player starts capturing the point.
	 * @param event
	 */
	@EventHandler
	public void onPointCapture(PlayerMoveEvent event) {
		//Checking if player is within bounds of the capture area.
		GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
		if(!GameTeam.getPlayerTeam(p).getName().equals("Spectators")) {
			if(captureArea.contains(event.getTo())) {
				if(!captureArea.contains(event.getFrom()))
				{
					//Player just got into the area.
					if(GameTeam.getPlayerTeam(p).equals(owningTeam)) {
						//This is player team's point. 
						//He can't capture it!
						ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "CAPTURE-POINT_Cant-Capture-Own-Team", 
							ChatColor.RED + "You can't capture your team's point!"));
					}
					else {
						if(controllingTeam == null) {
							if(capturing == false) {
								//Player is in the capture area.
								startCapturing(event.getPlayer());
							}
						}
						else {
							if(!GameTeam.getPlayerTeam(p).equals(controllingTeam)) {
								if(capturing == false) {
									//Player is in the capture area.
									startCapturing(event.getPlayer());		
								}
							}
							else {
								ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "CAPTURE-POINT_Already-Captured", ChatColor.GREEN + "Your team already controlls this point!"));
							}	
						}
					}
				}
			}
			else if(captureArea.contains(event.getFrom())) {
				//Player moved out of the area.
				stopCapturing(p);
			}
		}
	}
	/**
	 * Called when a player dies to check if he wasn't capturing the point at the same time.
	 * @param event
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(captureArea.contains(event.getEntity().getLocation())) {
			stopCapturing(GamePlayer.getGamePlayer(event.getEntity().getName()));
		}
	}
	
	/**
	 * Called when a player starts capturing the point.
	 * @param player
	 */
	private void startCapturing(final Player player) {
		final String playerName = player.getName();
		if(timeToCapture == 0) {
			//Reseting the timer if needed.
			timeToCapture = captureTime;	
		}
		capturing = true;
		captureTimerIDS.put(playerName, Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				GamePlayer p = GamePlayer.getGamePlayer(playerName);
				if(timeToCapture > 0) {
					timeToCapture--;
					capturePercent = 100 - ((timeToCapture * 100) / captureTime);
					//Sending message to capturing player.
					ActionBarAPI.sendActionBar(player, PvPGamesManager.language.get(p.getVanillaPlayer(), "CAPTURE-POINT_Capture",
							ChatColor.GRAY + "Capturing point " + "{0}" + ChatColor.GRAY + " - " + ChatColor.RED + "{1}" + ChatColor.GRAY + "%", 
							ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")),
							(int)capturePercent));
					
					//Adding color to point.
					float blocksToColorize = woolBlocks.size() * (capturePercent / 100);
					for(Block b : woolBlocks) {
						if(blocksToColorize > 0) {
							b.setType(Material.WOOL);
							b.setData(GameTeam.getPlayerTeam(p).getDyeColor().getWoolData());
							blocksToColorize--;
						}
					}
				}
				else {
					//Point captured!
					capturing = false;
					controllingTeam = GameTeam.getPlayerTeam(p);
					glassBlock.getBlock().setData(GameTeam.getPlayerTeam(p).getDyeColor().getDyeData());
					if(owningTeam != null) {
						ChatManager.broadcastMessage("CAPTURE-POINT_Captured-By-Team", ChatColor.GREEN + "Team " + "{0}" + ChatColor.GREEN + " captured point " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ")", 
								controllingTeam.getVanillaTeam().getDisplayName(), ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")), owningTeam.getVanillaTeam().getDisplayName());
					}
					else {
						ChatManager.broadcastMessage("CAPTURE-POINT_Captured-By-Team-Teamless", ChatColor.GREEN + "Team " + "{0}" + ChatColor.GREEN + " captured point " + "{1}", 
								controllingTeam.getVanillaTeam().getDisplayName(), ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")));	
					}
					//Running all onCapture actions.
					if(settings.isList("actions.onCapture")) {
						for (Object key : settings.getList("actions.onCapture")) {
							//Firing each action.
							//Adding the arguments to the action.
							HashMap<String, Object> arguments=new HashMap<String, Object>();
							//Adding TEAM_CAPTURED.
							arguments.put("TEAM_SOURCE", controllingTeam.getName());
							//Adding TEAM_VICTIM.
							arguments.put("TEAM_VICTIM", owningTeam.getName());
							//Adding TEAM_VICTIM.
							arguments.put("PLAYER_SOURCE", player.getName());
							//Adding CAPTURE-POINT_NAME
							arguments.put("OBJECTIVE_NAME", name);
							ActionsManager.trigger((String) key, arguments);
						}
					}
					stopCapturing(p);
				}
			}
		}, 20l, 20l));
	}
	/**
	 * Called when a player stops capturing the point.
	 * @param p
	 */
	private void stopCapturing(GamePlayer p) {
		//Cancelling the current capture process.
		if(captureTimerIDS.containsKey(p.getVanillaPlayer().getName())) {
			Bukkit.getScheduler().cancelTask(captureTimerIDS.get(p.getVanillaPlayer().getName()));	
			captureTimerIDS.remove(p.getVanillaPlayer().getName());	
		}
		//Cancelling the capturing.
		capturing = false;
	}
	
	/**
	 * The clock runnable.
	 */
	public Runnable clock = new Runnable() {
		//This is running once a second.
		public void run() {
			if(controllingTeam != null) {
				//Running all onHold actions.
				if(settings.isList("actions.onHold")) {
					for (Object key : settings.getList("actions.onHold")) {
						//Firing each action.
						//Adding the arguments to the action.
						HashMap<String, Object> arguments=new HashMap<String, Object>();
						//Adding TEAM_CAPTURED.
						arguments.put("TEAM_SOURCE", controllingTeam.getName());
						//Adding TEAM_VICTIM.
						if(owningTeam == null) {
							arguments.put("TEAM_VICTIM", null);
						}
						else {
							arguments.put("TEAM_VICTIM", owningTeam.getName());	
						}
						//Adding CAPTURE-POINT_NAME
						arguments.put("OBJECTIVE_NAME", name);
						ActionsManager.trigger((String) key, arguments);
					}
				}
			}
		}
	};
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return ChatColor.AQUA + "Ⓐ ";
	}
	public ObjectiveType getType() {
		return ObjectiveType.CAPTURE_POINT;
	}
	public GameTeam getOwningTeam() {
		return owningTeam;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public boolean isFinished() {
		return false;
	}
	public boolean isFinishable() {
		return false;
	}
}
