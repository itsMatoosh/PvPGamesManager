package pl.glonojad.pvpgamesmanager.game.objective;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.EffectsManager;
import pl.glonojad.pvpgamesmanager.util.StructureBuilder;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class Core implements Objective, Listener{
	//Core's
		private String name;
		@SuppressWarnings("unused")
		private Location location;
		private GameTeam owningTeam;
		private ConfigurationPart settings;
		private String lastPlayerBroken;
		private Boolean isLeaked = false;
		public Area coreArea;
		public Area leakArea;
		/** Creates a new Core on the specified location. */
		public Core(String name, Location location, GameTeam gameTeam, ConfigurationPart settings) {
			this.name = name;
			this.location = location;
			this.owningTeam = gameTeam;
			this.settings = settings;
			Area[] coreAreas = StructureBuilder.createCore(location);
			this.coreArea = coreAreas[0];
			this.leakArea = coreAreas[1];
			//Adding Core to the sidebar.
			SidebarManager.addObjective(this);
		}
		//Events
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onPlayerBreakCore(BlockBreakEvent event) {
			Block b = event.getBlock();
			if(GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(event.getPlayer().getName())).getName().equals("Spectators")) {
				event.setCancelled(true);
				return;
			}
			if(b.getType().equals(Material.OBSIDIAN)) {
				if(coreArea.contains(b)) {
			    	//Player tries to break a core!
			    	GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
			    	if(GameTeam.getPlayerTeam(p).equals(owningTeam)) {
			    		//Player is trying to break his team's core!
			    		//He can't do that!
			    		ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), 
			    			"CORE_Break-Ally", 
			    			ChatColor.RED + "You can't break your team's core!"));
			    		event.setCancelled(true);
			    	}
			    	else {
			    		//Player is trying to break other team's core!
			    		//No problem!
						//Firing all BREAK actions.
						if(settings.isList("actions.onBreak")) {
							for (Object key : settings.getList("actions.onBreak")) {
								//Firing each action.
								//Adding the arguments to the action.
								HashMap<String, Object> arguments=new HashMap<String, Object>();
								//Adding TEAM_CAPTURED.
								arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(p).getName());
								//Adding TEAM_VICTIM.
								arguments.put("TEAM_VICTIM", settings.getString("owningTeam"));
								//Adding PLAYER_CAPTURED.
								arguments.put("PLAYER_SOURCE", p.getVanillaPlayer().getName());
								//Adding FLAG_NAME
								arguments.put("OBJECTIVE_NAME", name);
								ActionsManager.trigger((String) key, arguments);
							}
						}
						//Broadcasting that player has broken the core!
						if(settings.isSet("owningTeam")) {
							ChatManager.broadcastMessage("CORE_Player-Broke", 
									"{0}" + ChatColor.YELLOW + " broke a part of the " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ")" + ChatColor.YELLOW + " core!", 
									p.getVanillaPlayer().getDisplayName(), 
									ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")),
									GameTeam.getTeam(settings.getString("owningTeam")).getVanillaTeam().getDisplayName());
						}
						else {
							ChatManager.broadcastMessage("CORE_Player-Broke-No-Owning-Team", 
									"{0}" + ChatColor.YELLOW + " broke part of the " + "{1}" + ChatColor.YELLOW + " core!", 
									p.getVanillaPlayer().getDisplayName(), 
									ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")));	
						}
						//Set player as last one to break this core.
						lastPlayerBroken = p.getVanillaPlayer().getName();
						SidebarManager.setObjectiveState(this, "Touched");
						//Drop experience
						EffectsManager.spawnExperience(event.getBlock().getLocation(), 10);
						//TODO: Make an effect!
			    	}	
				}
		    }
		}
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onCoreLeak(BlockFromToEvent event) {
			Block blockFrom=event.getBlock();
			Block blockTo=event.getToBlock();
			boolean lava=blockFrom.getType() == Material.LAVA || blockFrom.getType() == Material.STATIONARY_LAVA;
				if (!event.isCancelled() && lava) {
					    if(isLeaked) {
					    	//Remove core from the lists to save performance.
					    	return;
					    }
					    if(leakArea.contains(blockTo) && settings.getString("coreMode").equals("LEAK")) {
					    	//The core has leaked.
							//Firing all LEAK actions.
							if(settings.isList("actions.onLeak")) {
								for (Object key : settings.getList("actions.onLeak")) {
									//Firing each action.
									//Adding the arguments to the action.
									HashMap<String, Object> arguments=new HashMap<String, Object>();
									//Adding TEAM_CAPTURED.
									arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(lastPlayerBroken)).getName());
									//Adding TEAM_VICTIM.
									arguments.put("TEAM_VICTIM", settings.getString("owningTeam"));
									//Adding PLAYER_CAPTURED.
									arguments.put("PLAYER_SOURCE", lastPlayerBroken);
									//Adding FLAG_NAME
									arguments.put("OBJECTIVE_NAME", name);
									ActionsManager.trigger((String) key, arguments);
								}
							}
							//Broadcasting that player has leaked the core!
							if(owningTeam != null) {
								ChatManager.broadcastMessage("CORE_Team-Leak", 
										"{0}" + ChatColor.YELLOW + " made " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ")" + ChatColor.YELLOW + " core leak!", 
										GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(lastPlayerBroken)).getVanillaTeam().getDisplayName(), 
										ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")),
										GameTeam.getTeam(settings.getString("owningTeam")).getVanillaTeam().getDisplayName());
							}
							else {
								ChatManager.broadcastMessage("CORE_Team-Leak-No-Owning-Team", 
										"{0}" + ChatColor.YELLOW + " made " + "{1}" + ChatColor.YELLOW + " core leak!", 
										GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(lastPlayerBroken)).getVanillaTeam().getDisplayName(), 
										ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")));	
							}
							isLeaked = true;
							HandlerList.unregisterAll(this);
							SidebarManager.setObjectiveState(this, "Leaked");
							//Make an effect!
							EffectsManager.objectiveCompleted(GameTeam.getPlayerTeam(GamePlayer.getGamePlayer(lastPlayerBroken)));
					    }
				}
		}
		public String getName() {
			return name;
		}
		public String getPrefix() {
			return ChatColor.GREEN + "✔ ";
		}
		public ObjectiveType getType() {
			return ObjectiveType.CORE;
		}
		public GameTeam getOwningTeam() {
			return owningTeam;
		}
		public ConfigurationPart getSettings() {
			return settings;
		}
		public boolean isFinished() {
			return isLeaked;
		}
		public boolean isFinishable() {
			return true;
		}
}
