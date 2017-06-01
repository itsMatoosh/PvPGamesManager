package pl.glonojad.pvpgamesmanager.game.activator;

import java.util.HashMap;

import org.bukkit.Bukkit;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class Timer implements Activator{
	private String name;
	private int totalTime;
	private int tt;
	@SuppressWarnings("unused")
	private int step = 20;
	private int id;
	private boolean isFinished = false;
	private ConfigurationPart settings;
	public Timer (final String name, int totalTime, int step, final ConfigurationPart settings) {
		this.name = name;
		this.totalTime = totalTime;
		this.tt = this.totalTime;
		this.step = step;
		this.settings = settings;
		//Starting the countdown.
		ChatManager.log("Starting the timer");
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() {
			public void run() {
				if(!isFinished) {
					tt--;
					if(tt < 0 || tt == 0) {
						//Countdown finished.
						isFinished = true;
					}
				}
				else {
					//Running all the actions.
					for (Object key : settings.getList("actions")) {
						//Firing each action.
						//Adding the arguments to the action.
						HashMap<String, Object> arguments=new HashMap<String, Object>();
						for(String argument : settings.getConfigurationPart("arguments").getKeys(false)) {
							//Adding argument.
							arguments.put(settings.getString("arguments." + argument + ".type"), settings.getString("arguments." + argument + ".value"));
						}
						//Adding ACTIVATOR_NAME
						arguments.put("ACTIVATOR_NAME", name);
						ActionsManager.trigger((String) key, arguments);
					}
					Bukkit.getScheduler().cancelTask(id);
				}
			}
		}, step, step);
	}
	public String getName() {
		return name;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public boolean isFinished() {
		return isFinished;
	}
}
