package pl.glonojad.pvpgamesmanager.game.addons;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class ChestRandomizer implements Addon{
	public String name;
	public ConfigurationPart settings;
	
	public ChestRandomizer(String name, ConfigurationPart settings) {
		this.name = name;
		this.settings = settings;
		//Storing the chests.
		ArrayList<Chest> chests = new ArrayList<Chest>();
		for(String chest : settings.getStringList("chests")) {
			String[] coords = chest.split(";");
			Block b = new Location(MapManager.currentMap, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])).getBlock();
			if(b.getType() == Material.CHEST) {
				chests.add((Chest) b.getState());	
			}
			else {
				ChatManager.logError("[ChestRandomizer] Couldn't find a chest on coordinates: " + chest);
			}
		}
		//Randomizing the chests.
		for(Chest chest : chests) {
			//Creating a new Inventory.
			Inventory inv = chest.getBlockInventory();
			inv.clear();
			for(String item : settings.getConfigurationPart("items").getKeys(false)) {
				//Deciding whether to use or to ignore that item, based on the configured chance.
				if(MapManager.randInt(0, 100) < settings.getInt("items." + item + ".chance")) {
					//The item won the random lotery.
					//Adding it to the chest.
					inv.addItem(settings.getItemStack("items." + item + ".material"));
					continue;
				}
			}
			//We have a randomized inventory.
			chest.getBlockInventory().setContents(inv.getContents());
		}
	}
	public String getName() {
		return name;
	}

	public ConfigurationPart getSettings() {
		return settings;
	}
}
