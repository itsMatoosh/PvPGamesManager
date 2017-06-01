package pl.glonojad.pvpgamesmanager.game.addons;

import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public interface Addon {
	public String getName();
	public ConfigurationPart getSettings();
}
