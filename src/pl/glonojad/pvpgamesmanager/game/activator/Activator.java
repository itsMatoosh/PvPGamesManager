package pl.glonojad.pvpgamesmanager.game.activator;

import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public interface Activator {
	public String getName();
	public ConfigurationPart getSettings();
	public boolean isFinished();
}
