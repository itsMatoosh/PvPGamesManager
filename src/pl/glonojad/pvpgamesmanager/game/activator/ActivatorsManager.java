package pl.glonojad.pvpgamesmanager.game.activator;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class ActivatorsManager{
	public static ArrayList<Activator> activators = new ArrayList<Activator>();
	//Methods for creating activators.
	public static void createActivator(String activator, ConfigurationPart activatorSettings) {
		if(activatorSettings.getString("type").equalsIgnoreCase("Timer")) {
			//Creating a timer.
			Timer timerTemp = new Timer(activator, activatorSettings.getInt("totalTime"), activatorSettings.getInt("step"), activatorSettings);
			activators.add(timerTemp);
		}
		if(activatorSettings.getString("type").equalsIgnoreCase("PlayerDeath")) {
			//Creating a timer.
			PlayerDeath playerDeathTemp = new PlayerDeath(activator, activatorSettings);
			Bukkit.getPluginManager().registerEvents(playerDeathTemp, PvPGamesManager.instance);
			activators.add(playerDeathTemp);
		}
		//Logging the successful objective registration.
		ChatManager.log("Successfully registered activator " + activator);
	}
	public static Activator getActivator (String activator) {
		for(Activator act : activators) {
			if(act.getName().equals(activator)) {
				return act;
			}
		}
		return null;
	}
}
