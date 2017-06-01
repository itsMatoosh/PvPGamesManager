package pl.glonojad.pvpgamesmanager.player;

import java.util.HashMap;

import pl.glonojad.pvpgamesmanager.util.FileManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class StatsManager {	
	public static void loadStatsForPlayer(GamePlayer player) {
		boolean success = false;
		for(String playerUUID : FileManager.getStats().getConfigurationPart("Players").getKeys(false)) {
			if(playerUUID.equals(player.getVanillaPlayer().getUniqueId().toString())) {
				//We found stats for given player.
				success = true;
				ConfigurationPart playerSection = FileManager.getStats().getConfigurationPart("Players." + playerUUID);
				//Adding fetched stats to playersStats HashTable.
				player.setOverallKills(playerSection.getInt("kills"));
				player.setOverallDeaths(playerSection.getInt("deaths"));
				player.getVanillaPlayer().setExp(Float.parseFloat(playerSection.getString("experience")));
				player.getVanillaPlayer().setLevel(playerSection.getInt("level"));
				//Guns stats
				HashMap<String, String> gunsUpgraded = new HashMap<String, String>();
				ConfigurationPart playerGuns = playerSection.getConfigurationPart("unlockedGuns");
				for(String gunName : playerGuns.getKeys(false)) {
					gunsUpgraded.put(gunName, playerGuns.getString(gunName));
				}
				player.setUpgradedGuns(gunsUpgraded);
				break;
			}
		}
		if(success) {
			//Player was loaded successfully.
			return;
		}
		else {
			//Player couldn't be found!
			//Registering him to the stats.
			registerNewPlayer(player);
		}
	}
	public static void addKill(GamePlayer player) {
		ConfigurationPart playerStats = FileManager.getStats().getConfigurationPart("Players");
		ConfigurationPart playerSection = playerStats.getConfigurationPart(player.getVanillaPlayer().getUniqueId().toString());
		playerSection.set("kills", playerSection.getInt("kills") + 1);
		//Saving stats.
		FileManager.saveStats();
		//Loading stats once again.
		loadStatsForPlayer(player);
	}
	public static void addDeath(GamePlayer player) {
		ConfigurationPart playerStats = FileManager.getStats().getConfigurationPart("Players");
		ConfigurationPart playerSection = playerStats.getConfigurationPart(player.getVanillaPlayer().getUniqueId().toString());
		playerSection.set("deaths", playerSection.getInt("deaths") + 1);
		//Saving stats.
		savePlayerStatsToDisk(player);
		//Loading stats once again.
		loadStatsForPlayer(player);
	}
	private static void registerNewPlayer(GamePlayer player) {
		ConfigurationPart playerStats = FileManager.getStats().getConfigurationPart("Players");
		ConfigurationPart playerSection = playerStats.createPart(player.getVanillaPlayer().getUniqueId().toString());
		playerSection.set("kills", 0);
		playerSection.set("deaths", 0);
		playerSection.set("experience", 0.0);
		playerSection.set("level", 0);
		playerSection.set("name", player.getName());
		//Guns stats.
		ConfigurationPart playerGuns = playerSection.createPart("unlockedGuns");
		playerGuns.set("AK-47", "AK-47LvL1");
		//Saving stats.
		FileManager.saveStats();
		//Loading stats once again.
		loadStatsForPlayer(player);
	}
	
	public static void savePlayerStatsToDisk(GamePlayer player) {
		ConfigurationPart playerSection = FileManager.getStats().getConfigurationPart("Players." + player.getVanillaPlayer().getUniqueId());
		playerSection.set("kills", player.getOverallKills());
		playerSection.set("deaths", player.getOverallDeaths());
		playerSection.set("experience", player.getVanillaPlayer().getExp());
		playerSection.set("level",player.getVanillaPlayer().getLevel());
		playerSection.set("name", player.getName());
		//Guns stats TODO
		//ConfigurationPart playerGuns = playerSection.createSection("unlockedGuns");
		//playerGuns.set("AK-47", "AK-47LvL1");
		//Saving stats.
		FileManager.saveStats();
	}
}
