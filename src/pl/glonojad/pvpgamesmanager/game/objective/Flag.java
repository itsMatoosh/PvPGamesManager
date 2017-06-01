package pl.glonojad.pvpgamesmanager.game.objective;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.listeners.ProtectionManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.KitManager;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.EffectsManager;
import pl.glonojad.pvpgamesmanager.util.StructureBuilder;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class Flag implements Objective, Listener{
	//Flag's
	private String name;
	private Location location;
	private GameTeam owningTeam;
	private ConfigurationPart settings;
	private Block flagBlock;
	private DyeColor flagColor;
	public static HashMap<String, Flag> holdingPlayers = new HashMap<String,Flag>();
	
	/** Creates a new flag with the specified settings. */
	public Flag(String name, Location location, GameTeam gameTeam, ConfigurationPart objectiveSettings, DyeColor flagColor) {
		this.name = name;
		this.owningTeam = gameTeam;
		this.settings = objectiveSettings;
		this.flagBlock = StructureBuilder.createFlag(location, flagColor);
		this.location = flagBlock.getLocation();
		this.flagColor = flagColor;
		//Adding Flag to the sidebar.
		SidebarManager.addObjective(this);
		//Adding protection area to the flag.
		ProtectionManager.createArea(name, 
			new Location(MapManager.currentMap, this.location.getX(), this.location.getY() - 1, this.location.getZ()), 
			new Location(MapManager.currentMap, this.location.getX(), this.location.getY() + 1, this.location.getZ()),
			objectiveSettings.getConfigurationPart("protection"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTouchFlag(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if(event.getClickedBlock().getLocation().equals(this.location)) {
				//Player is breaking a flag.
				GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName()); //The player.
				if(GameTeam.getPlayerTeam(p).getName().equals("Spectators")) {
					event.setCancelled(true);
					return;
				}
				if(holdingPlayers.containsKey(p.getVanillaPlayer().getName())) {
					//Player already has a flag.
					if(GameTeam.getPlayerTeam(p).equals(owningTeam)) {
						//Player Captured The Flag!
						//Firing all configured actions.
						if(settings.isList("actions.onCapture")) {
							for (Object key : settings.getList("actions.onCapture")) {
								//Firing each action.
								//Adding the arguments to the action.
								HashMap<String, Object> arguments=new HashMap<String, Object>();
								//Adding TEAM_CAPTURED.
								arguments.put("TEAM_SOURCE", owningTeam.getName());
								//Adding TEAM_VICTIM.
								arguments.put("TEAM_VICTIM", holdingPlayers.get(p.getVanillaPlayer().getName()).getOwningTeam().getName());
								//Adding PLAYER_CAPTURED.
								arguments.put("PLAYER_SOURCE", p.getVanillaPlayer().getName());
								//Adding FLAG_NAME
								arguments.put("OBJECTIVE_NAME", holdingPlayers.get(p.getVanillaPlayer().getName()).getName());
								//Triggering the action.
								ActionsManager.trigger((String) key, arguments);
							}
						}
						//Broadcasting that player has captured the flag!
						if(owningTeam != null) {
							//This flag belongs to a team.
							ChatManager.broadcastMessage("FLAG_Broadcast-Captured", 
									"{0}" + ChatColor.GREEN + " has captured the " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ")" + ChatColor.GREEN + " flag!", 
									p.getVanillaPlayer().getDisplayName(), 
									ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")),
									owningTeam.getVanillaTeam().getDisplayName());
						}
						else {
							ChatManager.broadcastMessage("FLAG_Broadcast-Captured-No-Owning-Team", 
									"{0}" + ChatColor.GREEN + " has captured the " + "{1}" + ChatColor.GREEN + " flag!", 
									p.getVanillaPlayer().getDisplayName(), 
									ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")));	
						}
						//Taking away the flag from player.
						p.getVanillaPlayer().getInventory().clear();
						holdingPlayers.remove(p.getVanillaPlayer().getName());
						//Disabling effects.
						EffectsManager.setPlayerHasFlag(p, false);
						//Letting player choose his kit again.
						p.setCanChooseKit(true);
						KitManager.openKitMenu(p);
						p.setCanChooseKit(false);
						EffectsManager.objectiveCompleted(GameTeam.getPlayerTeam(p));
						EffectsManager.spawnExperience(event.getClickedBlock().getLocation(), 10);
						event.setCancelled(true);
						return;
					}
					else {
						//He can't take this flag!
						event.setCancelled(true);
						ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "FLAG_Cant-Carry-More", ChatColor.RED + "You can't carry more flags!"));
						//TODO: Make an effect!
						return;
					}
				}
				else {
					//Player doesn't have a flag.
					if(owningTeam.equals(GameTeam.getPlayerTeam(p))) {
						//Player is trying to grab his team's flag.
						//He can't do that!
						event.setCancelled(true);
						ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "FLAG_Grab-Ally", ChatColor.RED + "You can't steal your team's flag!"));
						//TODO: Make an effect!
						return;
					}
					else{
						//Player is trying to grab another team's flag and he doesn't have any flag in inventory.
						if(owningTeam == null) {
							//This flag doesn't belong to a team.
							//Checking flagMode.
							if(settings.getString("flagMode").equals("CAPTURE")) {
								//Firing all configured actions.
								if(settings.isList("actions.onGrab")) {
									for (Object key : settings.getList("actions.onGrab")) {
										//Firing each action.
										//Adding the arguments to the action.
										HashMap<String, Object> arguments=new HashMap<String, Object>();
										//Adding TEAM_GRABBED.
										arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(p).getName());
										//Adding TEAM_VICTIM.
										arguments.put("TEAM_VICTIM", null); //There is no TEAM_VICTIM.
										//Adding PLAYER_GRABBED.
										arguments.put("PLAYER_SOURCE", p.getVanillaPlayer().getName());
										//Adding FLAG_NAME
										arguments.put("OBJECTIVE_NAME", name);
										ActionsManager.trigger((String) key, arguments);
									}
								}
								//Enabling hold mode on player.
							        String name = ChatColor.translateAlternateColorCodes('$', settings.getString("displayName"));
							        Wool wool = new Wool(flagColor);
							        ItemStack woolItem = wool.toItemStack();
							        ItemMeta itemMeta = woolItem.getItemMeta();
							        itemMeta.setDisplayName(name);
							        woolItem.setItemMeta(itemMeta);
							        
							        int i = 0;
								while(i < p.getVanillaPlayer().getInventory().getSize()) {
									p.getVanillaPlayer().getInventory().setItem(i, woolItem);
									i++;
								}
								//Enabling effects
								EffectsManager.setPlayerHasFlag(p, true);
								holdingPlayers.put(p.getVanillaPlayer().getName(), this);
							}
							else if(settings.getString("flagMode").equals("GRAB")) {
								//Firing all configured actions.
								if(settings.isList("actions.onGrab")) {
									for (Object key : settings.getList("actions.onGrab")) {
										//Firing each action.
										//Adding the arguments to the action.
										HashMap<String, Object> arguments=new HashMap<String, Object>();
										//Adding TEAM_GRABBED.
										arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(p).getName());
										//Adding TEAM_VICTIM.
										arguments.put("TEAM_VICTIM", null); //There is no TEAM_VICTIM.
										//Adding PLAYER_GRABBED.
										arguments.put("PLAYER_SOURCE", p.getVanillaPlayer().getName());
										//Adding FLAG_NAME
										arguments.put("OBJECTIVE_NAME", settings);
										ActionsManager.trigger((String) key, arguments);
									}
								}
							}
							//Broadcasting that player has grabbed a flag.
							ChatManager.broadcastMessage("FLAG_Flag-Grabbed-By-Player-No-Owning-Team", 
									"{0}" + ChatColor.YELLOW + " stole " + "{1}" + ChatColor.YELLOW + " flag!", 
									p.getVanillaPlayer().getDisplayName(), 
									ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")));
							event.setCancelled(true);
							event.getClickedBlock().setType(Material.AIR);
							return;
						}
						else {
							//This flag does belong to a team.
							//Checking flagMode.
							if(settings.getString("flagMode").equals("CAPTURE")) {
								//Firing all configured actions.
								if(settings.isList("actions.onGrab")) {
									for (Object key : settings.getList("actions.onGrab")) {
										//Firing each action.
										//Adding the arguments to the action.
										HashMap<String, Object> arguments=new HashMap<String, Object>();
										//Adding TEAM_GRABBED.
										arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(p).getName());
										//Adding TEAM_VICTIM.
										arguments.put("TEAM_VICTIM", owningTeam.getName());
										//Adding PLAYER_GRABBED.
										arguments.put("PLAYER_SOURCE", p.getVanillaPlayer().getName());
										//Adding FLAG_NAME
										arguments.put("OBJECTIVE_NAME", name);
										ActionsManager.trigger((String) key, arguments);
									}
								}
								//Enabling hold mode on player.
							        String name = ChatColor.translateAlternateColorCodes('$', settings.getString("displayName"));
							        Wool wool = new Wool(flagColor);
							        ItemStack woolItem = wool.toItemStack(); 
							        ItemMeta itemMeta = woolItem.getItemMeta();
							        itemMeta.setDisplayName(name);
							        woolItem.setItemMeta(itemMeta);
							        
							        int i = 0;
								while(i < p.getVanillaPlayer().getInventory().getSize()) {
									p.getVanillaPlayer().getInventory().setItem(i, woolItem);
									i++;
								}
								//Enabling effects.
								EffectsManager.setPlayerHasFlag(p, true);
								holdingPlayers.put(p.getVanillaPlayer().getName(), this);
							}
							if(settings.getString("flagMode").equals("GRAB")) {
								//Firing all configured actions.
								if(settings.isList("actions.onGrab")) {
									for (Object key : settings.getList("actions.onGrab")) {
										//Firing each action.
										//Adding the arguments to the action.
										HashMap<String, Object> arguments=new HashMap<String, Object>();
										//Adding TEAM_GRABBED.
										arguments.put("TEAM_SOURCE", GameTeam.getPlayerTeam(p).getName());
										//Adding TEAM_VICTIM.
										arguments.put("TEAM_VICTIM", owningTeam.getName());
										//Adding PLAYER_GRABBED.
										arguments.put("PLAYER_SOURCE", p.getVanillaPlayer().getName());
										//Adding FLAG_NAME
										arguments.put("OBJECTIVE_NAME", name);
										ActionsManager.trigger((String) key, arguments);
									}
								}
							}
							//Broadcasting that player has grabbed a flag.
							ChatManager.broadcastMessage("FLAG_Flag-Grabbed-By-Player", 
									"{0}" + ChatColor.YELLOW + " grabbed " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ")" + ChatColor.YELLOW + " flag!", 
									p.getVanillaPlayer().getDisplayName(), 
									ChatColor.translateAlternateColorCodes('$', settings.getString("displayName")),
									owningTeam.getVanillaTeam().getDisplayName());
							event.getClickedBlock().setType(Material.AIR);
							return;
						}
					}
				}
			}	
		}
	}
	public void respawnFlag() {
		ChatManager.log("Respawning flag " + name);
		location = StructureBuilder.createFlag(location, flagColor).getLocation();
	}
	public void onFlagLost(GamePlayer p) {
		//Respawning the flag.
		location = StructureBuilder.createFlag(location, flagColor).getLocation();
		String flagDisplayName = ChatColor.translateAlternateColorCodes('$', settings.getString("displayName"));
		//Broadcasting flag loss.
		ChatManager.log(p.getVanillaPlayer().getDisplayName() + " lost the " + flagDisplayName + ChatColor.GRAY + " ("+ owningTeam.getVanillaTeam().getDisplayName() + ChatColor.GRAY + ")" + " flag!");
		ChatManager.broadcastMessage("FLAG_Player-Loss", "{0}" + ChatColor.RED + " lost the " + "{1}" + ChatColor.GRAY + " (" + "{2}" + ChatColor.GRAY + ")" + ChatColor.RED + " flag!", p.getVanillaPlayer().getDisplayName(), flagDisplayName, owningTeam.getVanillaTeam().getDisplayName());
		//Removing flag from this player.
		holdingPlayers.remove(p.getVanillaPlayer().getName());
		//Removing effects from player.
		EffectsManager.setPlayerHasFlag(p, false);
	}
	public static Flag getFlag(String name) {
		for(Flag flag : ObjectivesManager.flags) {
			if(flag.getName().equals(name)) {
				return flag;
			}
		}
		return null;
	}
	public GameTeam getOwningTeam() {
		return owningTeam;
	}
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return ChatColor.GOLD + "▲ ";
	}
	public ObjectiveType getType() {
		return ObjectiveType.FLAG;
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
