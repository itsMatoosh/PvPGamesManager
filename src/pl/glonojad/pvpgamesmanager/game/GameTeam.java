package pl.glonojad.pvpgamesmanager.game;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.win.Winner;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.Spawn;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

/** A team in the game. */
public class GameTeam implements Listener, Winner{
	
	private String name;
	private Color color;
	private DyeColor dyeColor;
	private Team vanillaTeam;
	private int points = 0;
	private int size = 0;
	private ConfigurationPart settings;
	private ArrayList<Spawn> spawns = new ArrayList<Spawn>();
	private ArrayList<GamePlayer> members = new ArrayList<GamePlayer>();
	
	public static ArrayList<GameTeam> teams = new ArrayList<GameTeam>();
	public static GameTeam spectators;
	
	/** Creates a new team for this game. */
	@SuppressWarnings("deprecation")
	public GameTeam(String teamName, ConfigurationPart teamSettings) {
		//Creating a new team.
		this.name = teamName;	
		this.vanillaTeam = GameManager.mainScoreboard.registerNewTeam(teamName);
		this.settings = teamSettings;
		if(!teamSettings.getString("size").equals("") && teamSettings.getInt("size") != 0) {
			this.size = teamSettings.getInt("size");
		}
		//Setting team displayName.
		this.vanillaTeam.setDisplayName(ChatColor.translateAlternateColorCodes('$', teamSettings.getString("displayName")));
		//Setting team prefix.
		this.vanillaTeam.setPrefix(ChatColor.translateAlternateColorCodes('$', teamSettings.getString("prefix")));
		if(teamSettings.isSet("suffix")) {
			//Setting team suffix.
			this.vanillaTeam.setSuffix(ChatColor.translateAlternateColorCodes('$', teamSettings.getString("suffix")));
		}
		//Setting team allowFriendlyFire.
		this.vanillaTeam.setAllowFriendlyFire(teamSettings.getBoolean("allowFriendlyFire"));
		//Setting team canSeeFriendlyInvisibles.
		this.vanillaTeam.setCanSeeFriendlyInvisibles(teamSettings.getBoolean("canSeeFriendlyInvisibles"));
		//Setting team nameTagVisibility.
		if(teamSettings.getString("nameTagVisibility").equals("ALWAYS")) {
			this.vanillaTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
		}
		if(teamSettings.getString("nameTagVisibility").equals("NEVER")) {
			this.vanillaTeam.setNameTagVisibility(NameTagVisibility.NEVER);
		}
		if(teamSettings.getString("nameTagVisibility").equals("HIDE_FOR_OTHER_TEAMS")) {
			this.vanillaTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		}
		if(teamSettings.getString("nameTagVisibility").equals("HIDE_FOR_OWN_TEAM")) {
			this.vanillaTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);
		}
		this.dyeColor = DyeColor.getByWoolData((byte) this.settings.getInt("color"));
		this.color = this.dyeColor.getColor();
		//Adding spawns.
    	for(String spawn : settings.getConfigurationPart("spawns").getKeys(false)) {
			spawns.add(new Spawn(new Location(
				MapManager.currentMap, 
				settings.getConfigurationPart("spawns").getDouble(spawn + ".x"), 
				settings.getConfigurationPart("spawns").getDouble(spawn + ".y"), 
				settings.getConfigurationPart("spawns").getDouble(spawn + ".z"), 
				settings.getConfigurationPart("spawns").getInt(spawn + ".pitch"), 
				settings.getConfigurationPart("spawns").getInt(spawn + ".yaw")), settings.getConfigurationPart("spawns." + spawn)));
		}
		//Adding team to the sidebar view.
	    SidebarManager.addTeam(name, vanillaTeam.getDisplayName());
	    //Adding team to teams.
	    teams.add(this);
		ChatManager.log("Registered team " + teamName + " with prefix " + this.vanillaTeam.getPrefix() + "[Playername]");
	}
	/** Creates a new light team. */
	public GameTeam(String teamName,String teamDisplayName, String teamPrefix, Boolean friendlyfire) {
		//Creating a new team.
		this.name = teamName;
		this.vanillaTeam = GameManager.mainScoreboard.registerNewTeam(teamName);
		this.vanillaTeam.setPrefix(teamPrefix);
		this.vanillaTeam.setAllowFriendlyFire(friendlyfire);
		this.vanillaTeam.setDisplayName(teamDisplayName);
		//Adding team to teams.
		spectators = this;
		ChatManager.log("Registered team " + teamName);
	}
	public static GameTeam getTeam(String teamName) {
		//Returning a requested team.
		for(GameTeam team : teams) {
			if(team.getName().equals(teamName)) {
				return team;
			}
		}
		return null;
	}
	public boolean add(GamePlayer p) {
			if(this.size == 0 || (this.members.size() + 1) < this.size) {
				//Adding player to the team.
				vanillaTeam.addPlayer(p.getVanillaPlayer());
				members.add(p);
				p.getVanillaPlayer().setDisplayName(vanillaTeam.getPrefix() + p.getVanillaPlayer().getName() + vanillaTeam.getSuffix());
				ChatManager.log("Added " + p.getVanillaPlayer().getName() + " to " + name + " team!");
				//Updating the scoreboard.
				GameManager.updateScoreboard();
				if(!(name.equals("Spectators"))) {
					//Sending player a message.
					ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(),"GAME_Your-Team", ChatColor.GRAY + "You are in the " + "{0}" + ChatColor.GRAY + " team!", vanillaTeam.getDisplayName()));
					//Sending messages to other players.
					for(Player online : PvPGamesManager.instance.getServer().getOnlinePlayers()) {
						if(!(online.getName().equals(p.getVanillaPlayer().getName()))) {
							online.sendMessage(PvPGamesManager.language.get(online, "PLAYER_Join-Game", "{0}" + ChatColor.GRAY + " joined the game", p.getVanillaPlayer().getDisplayName()));
						}
					}
				} else {
					//Setting player's gamemode to spectator..
					p.getVanillaPlayer().setGameMode(GameMode.SPECTATOR);
					//Clearing player's inventory.
					p.getVanillaPlayer().getInventory().clear();
					//Setting a proper displayName for a player.
					p.getVanillaPlayer().setDisplayName(ChatColor.GRAY + p.getVanillaPlayer().getName());
					
					//Send player a spectator message.
					ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "GAME_Spectator", ChatColor.GRAY + "You are now a " + ChatColor.AQUA + "Spectator"));
				}
				return true;
			}
			else {
				//The team is already full.
				ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(),"GAME_Team-Full", ChatColor.RED + "Team " + "{0}" + ChatColor.RED + " is full!", vanillaTeam.getDisplayName()));
				return false;
			}
	}
	public void remove(GamePlayer p) {
		//Removing player from the team and adding him to the lobby.
		vanillaTeam.removePlayer(p.getVanillaPlayer().getPlayer());
		members.remove(p);
		//Adding player to the lobby team.
		GameTeam.getSpectators().add(p);
		//Updating the scoreboard.
		GameManager.updateScoreboard();	

	}
	public void addPoints(int points) {
		this.points = this.points + points;
		SidebarManager.setPoints(name, this.points);
		ChatManager.log("Team " + this.name + " gained " + points + " points!");
		return;
	}
    public static GameTeam getSmallestTeam() {
        //Smallest team, starting with a default team
        GameTeam teamMin = teams.get(0);
        //Loop through all teams and update the smallest team if needed
        for (GameTeam team : teams) {
            if (team.vanillaTeam.getPlayers().size() < teamMin.vanillaTeam.getPlayers().size()) {
                if(team.size != 0 && team.size < team.members.size() + 1) {
                	continue;
                } else {
                	teamMin = team;
                }
            }
        }
        //Return the smallest team.
        return teamMin;
    }
    public Spawn getTeamSpawn(int spawnID) {
    	if(spawns.get(spawnID) == null) {
    		spawns.get(spawnID - 1);
    	}
    	return spawns.get(spawnID);
    }
    public static GameTeam getSpectators() {
    	return spectators;
    }
    public Spawn getRandomSpawn() {
    	return spawns.get(MapManager.randInt(0, spawns.size() - 1));
    }
    public static GameTeam getPlayerTeam (GamePlayer p) {
    	if(GameTeam.getSpectators().getVanillaTeam().hasPlayer(p.getVanillaPlayer())) {
    		return GameTeam.getSpectators();
    	}
    	for(GameTeam t : teams) {
    		if(t.vanillaTeam.hasPlayer(p.getVanillaPlayer())) {
    			return t;
    		}
    	}
    	return null;
    }
	public String getName() {
		return name;
	}
	public Color getColor() {
		return color;
	}
	public DyeColor getDyeColor() {
		return dyeColor;
	}
	public Team getVanillaTeam() {
		return vanillaTeam;
	}
	public Integer getPoints() {
		return points;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public static ArrayList<GameTeam> getTeams() {
		return teams;
	}
	public ArrayList<Spawn> getSpawns() {
		return spawns;
	}
	public ArrayList<GamePlayer> getMembers() {
		return members;
	}
}