package pl.glonojad.pvpgamesmanager.player.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;

/**
 * Listens for damage events.
 * @author Mateusz Rebacz
 *
 */
public class PlayerDamage implements Listener {
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			//Checking if player is invincible.
			GamePlayer p = GamePlayer.getGamePlayer(event.getEntity().getName());
			if(p.isInvincible()) {
				event.setCancelled(true);
				return;
			}
			
			//If the player is a spectator and fell in the void, teleporting back to lobby.
			if(GameTeam.getPlayerTeam(p).getName().equals("Spectators")) {
				event.setCancelled(true);
				if(event.getCause().equals(DamageCause.VOID)) {
					p.getVanillaPlayer().teleport(MapManager.mapLobbyLocation);
				}
			}
		}
	}
	@EventHandler
	public static void onEntityDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() != null && event.getDamager() instanceof Player) {
			GamePlayer damager = GamePlayer.getGamePlayer(event.getDamager().getName());
			if(!damager.canAttack() || damager.getTeam().getName().equals("Spectators")) {
				event.setCancelled(true);
			}
		}
	}
}
