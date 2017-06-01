package pl.glonojad.pvpgamesmanager.game.activator;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class PlayerDeath implements Activator, Listener{
	private String name;
	private ConfigurationPart settings;
	public PlayerDeath (final String name, ConfigurationPart settings) {
		this.name = name;
		this.settings = settings;
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		GamePlayer p = GamePlayer.getGamePlayer(event.getEntity().getName());
		//Running all the actions.
		for (Object key : settings.getList("actions")) {
			//Firing each action.
			//Adding the arguments to the action.
			HashMap<String, Object> arguments=new HashMap<String, Object>();
			//Adding argument.
			arguments.put("TEAM_VICTIM", p.getTeam().getName());
			//Adding argument.
			arguments.put("PLAYER_VICTIM", p.getName());
			arguments.put("TEAM_SOURCE", "");
			arguments.put("PLAYER_SOURCE", "");	
			//Adding argument.
			if(event.getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
				if(((EntityDamageByEntityEvent) p.getVanillaPlayer().getLastDamageCause()).getDamager() instanceof Player) {
					arguments.put("TEAM_SOURCE", GamePlayer.getGamePlayer(((EntityDamageByEntityEvent)p.getVanillaPlayer().getLastDamageCause()).getDamager().getName()).getTeam().getName());
					arguments.put("PLAYER_SOURCE", ((EntityDamageByEntityEvent)p.getVanillaPlayer().getLastDamageCause()).getDamager().getName());		
				}
			}
			for(String argument : settings.getConfigurationPart("arguments").getKeys(false)) {
				//Adding argument.
				arguments.put(settings.getString("arguments." + argument + ".type"), settings.getString("arguments." + argument + ".value"));
			}
			//Adding ACTIVATOR_NAME
			arguments.put("ACTIVATOR_NAME", name);
			ActionsManager.trigger((String) key, arguments);
		}
	}
	public String getName() {
		return name;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public boolean isFinished() {
		return false;
	}
}