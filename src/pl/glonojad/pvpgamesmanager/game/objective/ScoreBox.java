package pl.glonojad.pvpgamesmanager.game.objective;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class ScoreBox implements Objective, Listener{
	private String name;
	private GameTeam owningTeam;
	private Area captureArea;
	private ConfigurationPart settings;
	/** Createsa a new ScoreBox on a specified location. */
	public ScoreBox(String name, GameTeam owningTeam, Area captureArea, ConfigurationPart settings) {
		this.name = name;
		this.owningTeam = owningTeam;
		this.captureArea = captureArea;
		this.settings = settings;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, clock, 20, 20);
		SidebarManager.addObjective(this);
	}
	@EventHandler
	public void onPlayerReach(PlayerMoveEvent event) {
		//Checking if player entered the capture area.
		if(captureArea.contains(event.getTo())) {
			GamePlayer enteringPlayer = GamePlayer.getGamePlayer(event.getPlayer().getName());
			if(!enteringPlayer.getTeam().getName().equals("Spectators")) {
				if(enteringPlayer.getTeam().equals(owningTeam)) {
					//This player tries to enter his own team's scorebox.
					//He can't do that!
					event.setCancelled(true);
					ChatManager.sendMessageToPlayer(enteringPlayer, 
						PvPGamesManager.language.get(enteringPlayer.getVanillaPlayer(), 
							"SCOREBOX_Enter-Friendly", 
							ChatColor.RED + "You can't enter your team's scorebox!"));
				}
				else {
					//Player just entered another team's scorebox.
					if(settings.getString("scoreBoxMode").equals("ENTER")) {
						//Firing all configured actions.
						if(settings.isList("actions.onEnter")) {
							for (Object key : settings.getList("actions.onEnter")) {
								//Firing each action.
								//Adding the arguments to the action.
								HashMap<String, Object> arguments=new HashMap<String, Object>();
								//Adding TEAM_BROKE.
								arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(event.getPlayer().getName())).getName());
								//Adding TEAM_VICTIM.
								arguments.put("TEAM_VICTIM", owningTeam.getName());
								//Adding PLAYER_BROKE.
								arguments.put("PLAYER_SOURCE", event.getPlayer().getName());
								//Adding DESTRUCTION_NAME
								arguments.put("OBJECTIVE_NAME", name);
								ActionsManager.trigger((String) key, arguments);
							}
						}
						//Broadcasting a message.
						if(owningTeam != null) {
							ChatManager.broadcastMessage("SCOREBOX_Enter-Enemy", "{0}" + ChatColor.GREEN + " entered " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ") " + ChatColor.YELLOW + "Scorebox", 
									enteringPlayer.getVanillaPlayer().getDisplayName(),
									ChatColor.translateAlternateColorCodes('$', this.settings.getString("displayName")),
									owningTeam.getVanillaTeam().getDisplayName());
						}
						else {
							ChatManager.broadcastMessage("SCOREBOX_Enter-Enemy-Teamless", "{0}" + ChatColor.GREEN + " entered " + "{1}" + ChatColor.YELLOW + " Scorebox", 
									enteringPlayer.getVanillaPlayer().getDisplayName(),
									ChatColor.translateAlternateColorCodes('$', this.settings.getString("displayName")));
						}
					}	
				}	
			}
		}
	}
	public Runnable clock = new Runnable() {
		//This is running once a second.
		public void run() {
			//Playing effect in the captureArea.
			for(Block b : captureArea) {
				MapManager.currentMap.playEffect(b.getLocation(), Effect.LAVA_POP, 1);
			}
		}
	};
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return ChatColor.YELLOW + "▼ ";
	}
	public ObjectiveType getType() {
		return ObjectiveType.SCOREBOX;
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
