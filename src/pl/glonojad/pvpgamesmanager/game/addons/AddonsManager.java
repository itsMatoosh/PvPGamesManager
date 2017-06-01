package pl.glonojad.pvpgamesmanager.game.addons;

import java.util.ArrayList;

import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class AddonsManager {
	public static ArrayList<Addon> addons = new ArrayList<Addon>();
	//Methods for creating addons.
	public static void createAddon(String addon, ConfigurationPart addonSettings) {
		if(addonSettings.getString("type").equalsIgnoreCase("ChestRandomizer")) {
			//Creating a ChestRandomizer.
			new ChestRandomizer(addon, addonSettings);
		}
		//Logging the successful objective registration.
		ChatManager.log("Successfully registered addon " + addon);
	}
	public static Addon getAddon (String addon) {
		for(Addon act : addons) {
			if(act.getName().equals(addon)) {
				return act;
			}
		}
		return null;
	}
}
