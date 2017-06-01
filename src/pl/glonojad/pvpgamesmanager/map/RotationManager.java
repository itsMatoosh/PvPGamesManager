package pl.glonojad.pvpgamesmanager.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;
import pl.glonojad.pvpgamesmanager.util.configuration.Configuration;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationType;

@SuppressWarnings("unused")
public class RotationManager {

	public static Configuration rotationSettings;
	public static ArrayList<String> mapsInRotation = new ArrayList<String>();
	public static ArrayList<String> playersVoted = new ArrayList<String>();
	public static float currentInterest = 0;
	
	public static String getMapToPlay() {
		//Caching the rotationSettings.
		rotationSettings = FileManager.getRotation();
		//Making sure, that all maps are registered.
		checkIfMapsExist();
		//Count all maps.
		countAllMaps();
		//Getting a map to play on.
		//Checking if ForcedNextMap is set.
		if(rotationSettings.isSet("ForcedNextMap") && !rotationSettings.getString("ForcedNextMap").equals("")) {
			//Now we know what map to play!
			String map = rotationSettings.getString("ForcedNextMap");
			//Resetting the ForcedNextMap.
			rotationSettings.set("ForcedNextMap", "");
			//Saving the files.
			saveCurrentStats();
			return map;
		}
		else {
			//Getting the map by the rotation criteria.
			//Checking what maps have been played least.
			//Excluding the last map we played.
			mapsInRotation.remove(rotationSettings.getString("LastGameMap"));
			//Finding the quantity of maps which is 40% of all maps.
			int mapsToFind = (int) (mapsInRotation.size()*(40/100.0f));
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
					if(mapStats.getInt("plays") < mapMinStats.getInt("plays") && !mapsMin.containsKey(map) && mapsInRotation.contains(map)) {
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
			//We successfully found the least played maps.
			//Checking if we have only 1 map.
			if(mapsMin.size() == 1) {
				//Returning the only map.
				return mapsMin.entrySet().iterator().next().getKey();
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
				return leastPlayedMap;
			}
			//Getting the 50% most voted maps out of mapsMin.
			int mostVotedMapsToFind = (int) (mapsMin.size()*(50/100.0f));
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
				return mostVotedMaps.entrySet().iterator().next().getKey();	
			}
			else {
				//Returning the first minPlayed map.
				return mapsMin.entrySet().iterator().next().getKey();	
			}
		}
	}
	public static String getMapToPlay(ArrayList<String> excludedMaps) {
		//Caching the rotationSettings.
		rotationSettings = FileManager.getRotation();
		//Making sure, that all maps are registered.
		checkIfMapsExist();
		//Count all maps.
		countAllMaps();
		//Getting a map to play on.
		//Checking if ForcedNextMap is set.
		if(rotationSettings.isSet("ForcedNextMap") && !rotationSettings.getString("ForcedNextMap").equals("")) {
			//Now we know what map to play!
			String map = rotationSettings.getString("ForcedNextMap");
			//Resetting the ForcedNextMap.
			rotationSettings.set("ForcedNextMap", "");
			//Saving the files.
			saveCurrentStats();
			return map;
		}
		else {
			//Getting the map by the rotation criteria.
			//Checking what maps have been played least.
			//Excluding the selected maps.
			ArrayList<String> customRotation = new ArrayList<String>();
			customRotation.addAll(mapsInRotation);
			customRotation.removeAll(excludedMaps);
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
			//We successfully found the least played maps.
			//Checking if we have only 1 map.
			if(mapsMin.size() == 1) {
				//Returning the only map.
				return mapsMin.entrySet().iterator().next().getKey();
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
				return leastPlayedMap;
			}
			//Getting the 50% most voted maps out of mapsMin.
			int mostVotedMapsToFind = (int) (mapsMin.size()*(50/100.0f));
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
				return mostVotedMaps.entrySet().iterator().next().getKey();	
			}
			else {
				//Returning the first minPlayed map.
				return mapsMin.entrySet().iterator().next().getKey();	
			}
		}
	}
	public static void voteForMap(String name, GamePlayer player) {
		//Adding 1 vote to the requested map.
		//Searching for the requested map.
		if(!playersVoted.contains(player.getVanillaPlayer().getName())) {
			for(String map : rotationSettings.getConfigurationPart("Maps").getKeys(false)) {
				if(map.equals(name)) {
					int oldMapVotes = rotationSettings.getInt("Maps." + map + ".votes");
					//Setting new votes.
					rotationSettings.set("Maps." + map + ".votes", oldMapVotes + 1);
					//Saving the changes.
					FileManager.saveRotation();
					//Updating the rotationSettings.
					rotationSettings = FileManager.getRotation();
					//Adding the player to playerVoted.
					playersVoted.add(player.getVanillaPlayer().getName());
					//Sending player a message.
					ChatManager.sendMessageToPlayer(player, PvPGamesManager.language.get(player.getVanillaPlayer(), "VOTE_Successfully-Voted", ChatColor.GREEN + "Successfully voted for " + ChatColor.YELLOW + "{0}", name));
				}
			}	
		}
		else {
			ChatManager.sendMessageToPlayer(player, PvPGamesManager.language.get(player.getVanillaPlayer(), "VOTE_Already-Voted", ChatColor.RED + "You've already voted in this game!"));
		}
	}
	public static void addMapPlay(String name) {
		//Adding 1 play to the requested map.
		//Searching for the requested map.
		for(String map : rotationSettings.getConfigurationPart("Maps").getKeys(false)) {
			if(map.equals(name)) {
				int oldMapPlays = rotationSettings.getInt("Maps." + map + ".plays");
				//Setting new votes.
				rotationSettings.set("Maps." + map + ".plays", oldMapPlays + 1);
				//Saving the changes.
				FileManager.saveRotation();
				//Updating the rotationSettings.
				rotationSettings = FileManager.getRotation();
			}
		}
	}
	public static void addCurrentMapOnlinePlayer() {
		//Adding 1 player to the requested map.
		//Calculating the interest.
		currentInterest = (float)Bukkit.getOnlinePlayers().size() / (float)Bukkit.getMaxPlayers();
	}
	public static void checkIfMapsExist() {
		for(Object map : rotationSettings.getList("Rotation")) {
			boolean foundMap = false;
			for(String mapStats : rotationSettings.getConfigurationPart("Maps").getKeys(false)) {
				if(map.equals(mapStats)) {
					//The map is already registered in rotation settings.
					foundMap = true;
				}
			}
			if(foundMap == false) {
				//The map is not registered!
				//Registering the map.
				ConfigurationPart mapSettings = rotationSettings.getConfigurationPart("Maps").createPart((String) map);
				//Adding required nodes to the map and setting their default values.
				mapSettings.set("votes", 0);
				mapSettings.set("rating", 0);
				mapSettings.set("plays", 0);
				mapSettings.set("interest", 0);
			}
		}
		//Saving changes.
		FileManager.saveRotation();
		//Updating the rotationSettings.
		rotationSettings = FileManager.getRotation();
	}
	public static void saveCurrentStats() {
		//Setting new interest.
		int plays = rotationSettings.getInt("Maps." + MapManager.currentMapName + ".plays");
		float oldInterest = rotationSettings.getInt("Maps." + MapManager.currentMapName + ".interest");
		rotationSettings.set("Maps." + MapManager.currentMapName + ".interest", (((plays - 1) * oldInterest) + currentInterest) / plays);
		//Saving the changes.
		FileManager.saveRotation();
		//Updating the rotationSettings.
		rotationSettings = FileManager.getRotation();
	}
	public static boolean setNextMap(String mapName) {
		ArrayList<String> newMapsInRotation = mapsInRotation;
		newMapsInRotation.add(rotationSettings.getString("LastGameMap"));
		if(newMapsInRotation.contains(mapName)) {
			rotationSettings.set("ForcedNextMap", mapName);
			return true;
		}
		return false;
	}
	public static boolean addMapToRotation(String mapName) {
		if(mapsInRotation.contains(mapName)) {
			return false;
		}
		List<String> maps = rotationSettings.getStringList("Rotation");
		maps.add(mapName);
		mapsInRotation.add(mapName);
		rotationSettings.set("Rotation", maps);
		//Saving changes.
		FileManager.saveRotation();
		//Updating the rotationSettings.
		rotationSettings = FileManager.getRotation();
		return true;
	}
	public static boolean removeMapFromRotation(String mapName) {
		if(!mapsInRotation.contains(mapName)) {
			return false;
		}
		List<String> maps = rotationSettings.getStringList("Rotation");
		maps.remove(mapName);
		mapsInRotation.remove(mapName);
		rotationSettings.set("Rotation", maps);
		//Saving changes.
		FileManager.saveRotation();
		//Updating the rotationSettings.
		rotationSettings = FileManager.getRotation();
		return true;
	}
	public static void countAllMaps() {
		for(Object map : rotationSettings.getList("Rotation")) {
			mapsInRotation.add((String) map);
		}
	}
	/** Deletes map from the last game. */
	public static void deleteOldMap() {
		if(FileManager.getRotation().getString("LastGameMap") == "" || FileManager.getRotation().getString("LastGameMap") == null){
			ChatManager.log("There is no LastGameMap value!");
		}
		else {
			ChatManager.log("Deleting map from last game - " + FileManager.getRotation().getString("LastGameMap"));
			FileManager.removeMapFromMainServerFolder(FileManager.getServerMap(FileManager.getRotation().getString("LastGameMap")));
		}
	}
}
