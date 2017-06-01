package pl.glonojad.pvpgamesmanager.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;

public class StickedBlocks {

	Location center;
	HashMap<FallingBlock, Location> fallingBlocks = new HashMap<FallingBlock, Location>();
	
	@SuppressWarnings("deprecation")
	public StickedBlocks(Location centerLocation, ArrayList<Block> blocks) {
		//Setting the center of StickedBlocks.
		this.center = centerLocation;
		//Destroying the original block and spawning a falling block.
		for(Block b : blocks) {
			if(!b.getType().equals(Material.AIR)) {
				//Spawning a falling block and adding it to fallingBlocks with its Location from the center.
				Location correctLocation = new Location( MapManager.currentMap, b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ() );
				correctLocation.subtract(center);
				fallingBlocks.put(MapManager.currentMap.spawnFallingBlock(b.getLocation(), b.getType(), b.getData()), correctLocation);
				//Destroying the original block.
				b.setType(Material.AIR);	
			}
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() {
			public void run() {
				//Holding blocks in place.
				for(Entry<FallingBlock, Location> fb  : fallingBlocks.entrySet()) {
					Location wantedLocation = new Location( MapManager.currentMap, fb.getValue().getX(), fb.getValue().getY(), fb.getValue().getZ() );
					wantedLocation.add(center);
					if(!fb.getKey().getLocation().equals(wantedLocation)) {
						fb.getKey().teleport(wantedLocation);
					}
				}
			}
		}, 0, 10);
	}
}
