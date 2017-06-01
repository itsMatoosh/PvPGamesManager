package pl.glonojad.pvpgamesmanager.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;

public class EffectsManager {

	public static HashMap<Player, Integer> flagProcessors = new HashMap<Player, Integer>();
	
	public static void setPlayerHasFlag(final GamePlayer p, Boolean hasFlag) {
		if(hasFlag) {
			//Player has a flag.
			//Registering new flagProcessor.
			flagProcessors.put(p.getVanillaPlayer(), 
				Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() {
					public void run() {
						MapManager.currentMap.playEffect(p.getVanillaPlayer().getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 2);
					}
				}, 1L, 20));
		}
		else {
			//Player no longer has a flag.
			//Unregistering flagProccesors.
			Bukkit.getScheduler().cancelTask(flagProcessors.get(p.getVanillaPlayer()));
		}
	}
	public static void objectiveCompleted(GameTeam gameTeam) {
		for(OfflinePlayer o : gameTeam.getVanillaTeam().getPlayers()) {
			Player p = o.getPlayer();
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
			MapManager.currentMap.spawn(p.getLocation(), Firework.class);
		}
	}
	public static void spawnExperience(Location l, Integer amount) {
		ExperienceOrb orb = MapManager.currentMap.spawn(l, ExperienceOrb.class);
		MapManager.currentMap.playSound(l, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 2);
		orb.setExperience(amount);
	}
}
