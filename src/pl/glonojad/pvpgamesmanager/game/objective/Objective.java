package pl.glonojad.pvpgamesmanager.game.objective;

import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

/**
 * Represents a single objective.
 * @author Mateusz Rebacz
 *
 */
public interface Objective {
	public String getName();
	public String getPrefix();
	public GameTeam getOwningTeam();
	public ObjectiveType getType();
	public ConfigurationPart getSettings();
	public boolean isFinished();
	public boolean isFinishable();
}
