package pl.glonojad.pvpgamesmanager.levolution;

import org.bukkit.Bukkit;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;

public class LEvolutionManager {

	public static void loadModule(String module) {
		if(module.equalsIgnoreCase("ReaBlow")) {
			Bukkit.getPluginManager().registerEvents(new ReaBlow(), PvPGamesManager.instance);
		}
	}
}
