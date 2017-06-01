package pl.glonojad.pvpgamesmanager.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.RotationManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;
import pl.glonojad.pvpgamesmanager.util.configuration.Configuration;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationType;

public class RotationCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public RotationCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public void run() {
		if(args.length == 0) {
			//Showing player info about map rotation.
			if(sender instanceof Player) {
				GamePlayer gp = GamePlayer.getGamePlayer(sender.getName());
				ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(
						gp.getVanillaPlayer(), 
						"ROTATION_CURRENT-ROTATION", 
						ChatColor.YELLOW + "Current map rotation:"));
				int mapsToDisplay = 5;
				//Preventing duplicates.
				String excludedMap = MapManager.currentMapName;
				ArrayList<String> foundMaps = new ArrayList<String>();
				//Caching the rotationSettings.
				Configuration rotationSettings = new Configuration(YamlConfiguration.loadConfiguration(FileManager.rotation), ConfigurationType.ROTATION);
				//Making sure, that all maps are registered.
				RotationManager.checkIfMapsExist();
				//Count all maps.
				RotationManager.countAllMaps();
				while (mapsToDisplay > 0) {
					//Getting a map to play on.
					//Checking if ForcedNextMap is set.
					if(rotationSettings.isSet("ForcedNextMap") && !rotationSettings.getString("ForcedNextMap").equals("")) {
						//Now we know what map to play!
						String map = rotationSettings.getString("ForcedNextMap");
						//Resetting the ForcedNextMap.
						rotationSettings.set("ForcedNextMap", "");
						excludedMap = map;
						foundMaps.add(excludedMap);
						mapsToDisplay--;
						int oldMapPlays = rotationSettings.getInt("Maps." + map + ".plays");
						//Setting new votes.
						rotationSettings.set("Maps." + map + ".plays", oldMapPlays + 1);
						continue;
					}
					else {
						//Getting the map by the rotation criteria.
						//Checking what maps have been played least.
						//Excluding the selected maps.
						ArrayList<String> customRotation = new ArrayList<String>();
						customRotation.addAll(RotationManager.mapsInRotation);
						customRotation.remove(excludedMap);
						//Finding the quantity of maps which is 40% of all maps.
						int mapsToFind = (int) (customRotation.size()*(40/100.0f));
						HashMap<String, ConfigurationPart> mapsMin = new HashMap<String, ConfigurationPart>();
						//Searching for maps.
						while(mapsToFind > 0) {
							String mapMin = null;
							ConfigurationPart mapMinStats = null;
							for(String map : rotationSettings.getConfigurationPart("Maps").getKeys(false)) {
								ConfigurationPart mapStats = rotationSettings.getConfigurationPart("Maps." +  map);
								if(mapMin == null) {
									//Setting the first(default) map.
									mapMin = map;
									mapMinStats = rotationSettings.getConfigurationPart("Maps." + map);
								}
								if(mapStats.getInt("plays") < mapMinStats.getInt("plays") && !mapsMin.containsKey(map) && customRotation.contains(map)) {
									//We found a map, that is less played than the last map we found.
									mapMin = map;
									mapMinStats = rotationSettings.getConfigurationPart("Maps." + map);
								}
							}
							//We found the least played map.
							//Adding it to found maps storage.
							mapsMin.put(mapMin, mapMinStats);
							//Decreasing mapsToFind.
							mapsToFind--;
						}
						//We successfully found the 40 % least played maps.
						//Checking if we have only 1 map.
						if(mapsMin.size() == 1) {
							//Returning the only map.
							excludedMap = mapsMin.entrySet().iterator().next().getKey();
							foundMaps.add(excludedMap);
							mapsToDisplay--;
							int oldMapPlays = rotationSettings.getInt("Maps." + mapsMin.entrySet().iterator().next().getKey() + ".plays");
							//Setting new votes.
							rotationSettings.set("Maps." + mapsMin.entrySet().iterator().next().getKey() + ".plays", oldMapPlays + 1);
							continue;
						}
						//Preventing the isolation of new maps.
						String leastPlayedMap = null;
						String morePlayedMap = null;
						for(String map : mapsMin.keySet()) {
							if(leastPlayedMap == null) {
								leastPlayedMap = map;
							}
							if(mapsMin.get(map).getInt("plays") < mapsMin.get(leastPlayedMap).getInt("plays")) {
								leastPlayedMap = map;
							}
							else {
								morePlayedMap = map;
							}
						}
						if(mapsMin.get(morePlayedMap).getInt("plays") * (50/100.0f) > mapsMin.get(leastPlayedMap).getInt("plays")) {
							//Returning the only map.
							excludedMap = leastPlayedMap;
							foundMaps.add(excludedMap);
							mapsToDisplay--;
							int oldMapPlays = rotationSettings.getInt("Maps." + leastPlayedMap + ".plays");
							//Setting new votes.
							rotationSettings.set("Maps." + leastPlayedMap + ".plays", oldMapPlays + 1);
							continue;
						}
						//Getting the 50% most voted maps out of mapsMin.
						float mapsMinSize = mapsMin.size();
						int mostVotedMapsToFind = (int) (mapsMinSize*(50f/100.0f));
						HashMap<String, ConfigurationPart> mostVotedMaps = new HashMap<String, ConfigurationPart>();
						while(mostVotedMapsToFind > 0) {
							String mapMin = null;
							ConfigurationPart mapMinStats = null;
							for(Entry<String, ConfigurationPart> entry : mapsMin.entrySet()) {
								ConfigurationPart mapStats = entry.getValue();
								String mapName = entry.getKey();
								if(mapMin == null) {
									//Setting the first(default) map.
									mapMin = mapName;
									mapMinStats = mapStats;
								}
								if(mapStats.getInt("votes") > mapMinStats.getInt("votes") && !mostVotedMaps.containsKey(mapName)) {
									//We found a map, that more voted than the last map we found.
									mapMin = mapName;
									mapMinStats = mapStats;
								}
							}
							//We found the most voted map.
							//Adding it to found maps storage.
							mostVotedMaps.put(mapMin, mapMinStats);
							//Decreasing mostVotedMapsToFind.
							mostVotedMapsToFind--;
						}
						//We successfully found the most voted maps.
						if(mostVotedMaps.size() > 0) {
							//Returning the first map.
							excludedMap = mostVotedMaps.entrySet().iterator().next().getKey();
							foundMaps.add(excludedMap);
							mapsToDisplay--;
							int oldMapPlays = rotationSettings.getInt("Maps." + mostVotedMaps.entrySet().iterator().next().getKey() + ".plays");
							//Setting new votes.
							rotationSettings.set("Maps." + mostVotedMaps.entrySet().iterator().next().getKey() + ".plays", oldMapPlays + 1);
							continue;
						}
						else {
							//Returning the first minPlayed map.
							excludedMap = mapsMin.entrySet().iterator().next().getKey();
							foundMaps.add(excludedMap);
							mapsToDisplay--;
							int oldMapPlays = rotationSettings.getInt("Maps." + mapsMin.entrySet().iterator().next().getKey() + ".plays");
							//Setting new votes.
							rotationSettings.set("Maps." + mapsMin.entrySet().iterator().next().getKey() + ".plays", oldMapPlays + 1);
							continue;	
						}
					}
				}
				//Sending player found maps.
				for(String map : foundMaps) {
					ChatManager.sendMessageToPlayer(gp, ChatColor.GRAY + " - " + map);
				}
				return;
			}
		}
		if(args[0].equals("nextmap")) {
			//Player wants to set a next map!
			//Checking permissions.
			if(sender.hasPermission("rotation.nextmap")) {
				if(args[1] == null) {
					sender.sendMessage("Please specify a map!");
				}
				else {
					if(RotationManager.setNextMap(args[1].replace('_', ' '))) {
						sender.sendMessage("Next map set to " + args[1].replace('_', ' '));	
					}
					else {
						sender.sendMessage("You requested a non-existant map!");
					}
				}
			}
		}
		if(args[0].equals("addmap")) {
			//Player wants to set a next map!
			//Checking permissions.
			if(sender.hasPermission("rotation.addmap")) {
				if(args[1] == null) {
					sender.sendMessage("Please specify a map!");
				}
				else {
					if(RotationManager.addMapToRotation(args[1].replace('_', ' '))) {
						sender.sendMessage("Added map " + args[1].replace('_', ' ') + " to the rotation!");	
					}
					else {
						sender.sendMessage("You requested a non-existant map!");
					}
				}
			}
		}
		if(args[0].equals("removemap")) {
			//Player wants to set a next map!
			//Checking permissions.
			if(sender.hasPermission("rotation.removemap")) {
				if(args[1] == null) {
					sender.sendMessage("Please specify a map!");
				}
				else {
					if(RotationManager.removeMapFromRotation(args[1].replace('_', ' '))) {
						sender.sendMessage("Removed map " + args[1].replace('_', ' ') + " from the rotation!");	
					}
					else {
						sender.sendMessage("You requested a non-existant map!");
					}
				}
			}
		}
		return;
	}
}