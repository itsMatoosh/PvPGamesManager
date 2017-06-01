package pl.glonojad.pvpgamesmanager.levolution;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import pl.glonojad.pvpgamesmanager.map.MapManager;

public class ReaBlow implements Listener{
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		for(Block b : event.blockList()) {
			if(!b.getType().equals(Material.AIR)) {
				if(b.getType().equals(Material.TNT)) {
					MapManager.currentMap.spawnEntity(b.getLocation(), EntityType.PRIMED_TNT);
					return;
				}
				FallingBlock fb = MapManager.currentMap.spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
				Vector localLocation = new Vector(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ());
				localLocation.subtract(event.getEntity().getLocation().toVector());
				Vector velocity = new Vector(localLocation.getX() / 10, localLocation.getY() / 10, localLocation.getZ() / 10);
				fb.setVelocity(velocity);	
				b.setType(Material.AIR);
			}
		}
		event.blockList().clear();
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDebrisFall(EntityChangeBlockEvent event) {
		if(event.getEntity() instanceof FallingBlock) {
			if(!event.getTo().equals(Material.AIR)) {
				MapManager.currentMap.playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND,	event.getTo().getId());	
			}
		}
	}
}
