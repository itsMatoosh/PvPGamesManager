package pl.glonojad.pvpgamesmanager.map;

import org.bukkit.Location;

import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class Spawn {
	public Location location;
	public ConfigurationPart settings;
	public int maxPlayers = 0;
	public Spawn(Location loc, ConfigurationPart settings) {
		this.location = loc;
		this.settings = settings;
		if(!settings.getString("maxPlayers").equals("")) {
			maxPlayers = settings.getInt("maxPlayers");	
		}
	}
}
