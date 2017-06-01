package pl.glonojad.pvpgamesmanager.map;

import java.io.File;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.addons.AddonsManager;
import pl.glonojad.pvpgamesmanager.game.objective.ObjectivesManager;
import pl.glonojad.pvpgamesmanager.levolution.LEvolutionManager;
import pl.glonojad.pvpgamesmanager.listeners.ProtectionManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.InGameCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;
import pl.glonojad.pvpgamesmanager.util.WeaponsManager;
import pl.glonojad.pvpgamesmanager.util.configuration.Configuration;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationType;

public class MapManager extends JavaPlugin {

	//Map directories.
	public static File currentMapFolder;
	public static World currentMap;
	public static Configuration mapConfiguration;
	//Map informations.
	public static String currentMapName;
	public static String currentMapDescription;
	public static String currentMapObjective;
	public static String currentMapAuthor;
	public static String currentMapVersion;
	//Locations
	public static Location mapLobbyLocation;
	
	/** Loads a specified map. If the map isn't specified loads a random map. */
	public static void loadMap(String mapName) {
		ChatManager.log("Loading map " + mapName);
		if(mapName == null) {
			if(FileManager.getMapsFolder().listFiles().length == 1) {
				currentMapFolder = FileManager.getMapsFolder().listFiles()[0];
			}
			else {
				//We don't know which map to load. Let's pick a random map!
				currentMapFolder = FileManager.getMapsFolder().listFiles()[randInt(0,FileManager.getMapsFolder().listFiles().length - 1)];	
			}
			//We need to copy the map to our server's folder.
			FileManager.copyMapToServerFolder(currentMapFolder);
			//We need to update our currentMapFolder variable.
			currentMapFolder = FileManager.loadedMap;
			//We need to load the map.
			currentMap = Bukkit.createWorld(new WorldCreator(currentMapFolder.getName()));
			//We need to load the mapConfig.yml
			loadMapConfiguration(currentMapFolder.getName());
			//Setting up the map lobby.
			mapLobbyLocation = new Location(currentMap,mapConfiguration.getDouble("Lobby.Location.x"),mapConfiguration.getDouble("Lobby.Location.y"),mapConfiguration.getDouble("Lobby.Location.z"),mapConfiguration.getInt("Lobby.Location.pitch"), mapConfiguration.getInt("Lobby.Location.yaw"));
			ChatManager.log("Successfully loaded map " + currentMapFolder.getName());
		}
		else {
			//We already know which map to load. Let's load it!
			currentMapFolder = FileManager.getPluginMap(mapName);
			//We need to copy the map to our server's folder.
			FileManager.copyMapToServerFolder(currentMapFolder);
			//We need to update our currentMapFolder variable.
			currentMapFolder = FileManager.loadedMap;
			//We need to load the map.
			currentMap = Bukkit.createWorld(new WorldCreator(currentMapFolder.getName()));
			//We need to load the map.yml
			loadMapConfiguration(currentMapFolder.getName());
			//Setting up the map lobby.
			mapLobbyLocation = new Location(currentMap,mapConfiguration.getDouble("Lobby.Location.x"),mapConfiguration.getDouble("Lobby.Location.y"),mapConfiguration.getDouble("Lobby.Location.z"),mapConfiguration.getInt("Lobby.Location.pitch"), mapConfiguration.getInt("Lobby.Location.yaw"));
			ChatManager.log("Successfully loaded map " + currentMapFolder.getName());
		}
	}
	/** Loads a specified map's configuration. */
	public static void loadMapConfiguration(String mapName) {
		ChatManager.log("Searching " + currentMapFolder.getName() + " for a map.yml");
		for(File f : currentMapFolder.listFiles()) {
			if(f.isFile()) {
				if(f.getName().equals("map.yml")) {
					mapConfiguration = new Configuration(YamlConfiguration.loadConfiguration(f), ConfigurationType.MAP);
					//Load the required data from map.yml.
					currentMapName = mapConfiguration.getString("Name");
					currentMapDescription = mapConfiguration.getString("Description");
					currentMapObjective = mapConfiguration.getString("Objective");
					currentMapAuthor = mapConfiguration.getString("Author");
					currentMapVersion = mapConfiguration.getString("Version");
					InGameCountdown.current.time = (mapConfiguration.getInt("Settings.maxGameTime")) * 60;
					ChatManager.log("Successfully loaded configuration for " + currentMapFolder.getName());
					applyConfig();
					return;
				}
			}
		}
		ChatManager.logError("Map " + currentMapFolder.getName() + " doesn't have map.yml!");
	}
	/** Applies current map's config. */
	private static void applyConfig() {	
		//Setting up custom teams.
		for(String team : MapManager.mapConfiguration.getConfigurationPart("Teams").getKeys(false)) {
			ConfigurationPart teamSettings = MapManager.mapConfiguration.getConfigurationPart("Teams." + team);
			new GameTeam(team, teamSettings);
		}
		
		//Setting up objectives.
		for(String objective : MapManager.mapConfiguration.getConfigurationPart("Objectives").getKeys(false)) {
			ConfigurationPart objectiveSettings = MapManager.mapConfiguration.getConfigurationPart("Objectives." + objective);
			ObjectivesManager.createObjective(objective, objectiveSettings);
		}

		//Areas\\
		for(String area : MapManager.mapConfiguration.getConfigurationPart("Protection.areas").getKeys(false)) {
			ConfigurationPart areaSettings = MapManager.mapConfiguration.getConfigurationPart("Protection.areas." + area);
			ProtectionManager.createArea(area, 
				new Location(MapManager.currentMap, areaSettings.getDouble("minLocation.x"), areaSettings.getDouble("minLocation.y"), areaSettings.getDouble("minLocation.z")), 
				new Location(MapManager.currentMap, areaSettings.getDouble("maxLocation.x"), areaSettings.getDouble("maxLocation.y"), areaSettings.getDouble("maxLocation.z")), 
				areaSettings);
		}
		
		//Materials\\
		for(String material : MapManager.mapConfiguration.getConfigurationPart("Protection.materials").getKeys(false)) {
			ConfigurationPart materialSettings = MapManager.mapConfiguration.getConfigurationPart("Protection.materials." + material);
			ProtectionManager.registerProtectedMaterial(materialSettings.getItemStack("material").getType(), materialSettings);
		}
		
		//Player Settings\\
		for(GameTeam t : GameTeam.getTeams()) {
			GamePlayer.playerSettings.put(t, MapManager.mapConfiguration.getConfigurationPart("Teams." + t.getName() + ".player"));
		}
		
		//HealthBar\\
		Objective healthBar = GameManager.mainScoreboard.registerNewObjective("showhealth", "health");
		healthBar.setDisplaySlot(DisplaySlot.BELOW_NAME);
		healthBar.setDisplayName("/ 20");
		
		//LEvolution\\
		for(String module : mapConfiguration.getStringList("LEvolution.modules")) {
			LEvolutionManager.loadModule(module);
		}
		//Addons\\
		for(String addon : mapConfiguration.getConfigurationPart("Addons").getKeys(false)) {
			AddonsManager.createAddon(addon, mapConfiguration.getConfigurationPart("Addons." + addon));
		}
		
		//Enabling/Disabling guns.
		WeaponsManager.initialize();
		if(mapConfiguration.getConfigurationPart("Settings").getBoolean("enableGuns")) {
			WeaponsManager.enableGuns();
		}
		else {
			WeaponsManager.disableGuns();
		}
	}
	public static int randInt(int min, int max) {
		
		if(max < 0) {
			max = 0;
		}
		if(max == 0) {
			return 0;
		}
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt(max);

	    return randomNum;
	}
}
