package pl.glonojad.pvpgamesmanager.game.objective.Collapse;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.Objective;
import pl.glonojad.pvpgamesmanager.game.objective.ObjectiveType;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class Collapse implements Objective, Listener {
	
	public String name;
	public GameTeam owningTeam;
	public ConfigurationPart settings;
	
	public ArrayList<CollapseArea> collapseAreas = new ArrayList<CollapseArea>();
	private ArrayList<FallingBlock> collapseShrapnel = new ArrayList<FallingBlock>();
	private boolean collapsed = false;
	public ArrayList<CollapseCauseArea> causeAreas = new ArrayList<CollapseCauseArea>();
	public String lastPlayerBroken;
	
	/** Creates a new Collapse. */
	public Collapse(String name, GameTeam gameTeam, Area collapseArea, ConfigurationPart settings) {
		this.name = name;
		this.owningTeam = gameTeam;
		this.settings = settings;
		for(String area : settings.getConfigurationPart("collapseAreas").getKeys(false)) {
			ConfigurationPart areaSettings = settings.getConfigurationPart("collapseAreas." + area);
			this.collapseAreas.add(new CollapseArea(area, new Area(
				new Location(MapManager.currentMap, areaSettings.getInt("minLocation.x"), areaSettings.getInt("minLocation.y"), areaSettings.getInt("minLocation.z")), 
				new Location(MapManager.currentMap, areaSettings.getInt("maxLocation.x"), areaSettings.getInt("maxLocation.y"), areaSettings.getInt("maxLocation.z"))), 
				areaSettings));
		}
		for(String area : settings.getConfigurationPart("causeAreas").getKeys(false)) {
			ConfigurationPart areaSettings = settings.getConfigurationPart("causeAreas." + area);
			this.causeAreas.add(new CollapseCauseArea(area, new Area(
				new Location(MapManager.currentMap, areaSettings.getInt("minLocation.x"), areaSettings.getInt("minLocation.y"), areaSettings.getInt("minLocation.z")), 
				new Location(MapManager.currentMap, areaSettings.getInt("maxLocation.x"), areaSettings.getInt("maxLocation.y"), areaSettings.getInt("maxLocation.z"))), 
				areaSettings));
		}
		SidebarManager.addObjective(this);
	}
	@EventHandler
	public void OnExplosionInCauseArea(EntityExplodeEvent event) {
		for(CollapseCauseArea a : causeAreas) {
			if(a.getArea().getCenter().distance(event.getLocation()) < 35) {
				//The explosion was able to reach the causeArea.
				ArrayList<Location> blocksBroken = new ArrayList<Location>();
				for(Block b : event.blockList()) {
					if(a.getArea().contains(b.getLocation())) {
						blocksBroken.add(b.getLocation());
					}
				}
				if(event.getEntity() instanceof TNTPrimed) {
					TNTPrimed tnt = (TNTPrimed) event.getEntity();
					if(tnt.getSource() != null) {
						lastPlayerBroken = tnt.getSource().getName();
					}
				}
				new Thread(new OnCollapseBreakBlock(this, blocksBroken, a)).run();
			}
		}
	}
	@EventHandler
	public void OnBreakBlockInCauseArea(BlockBreakEvent event) {
		for(CollapseCauseArea a : causeAreas) {
			if(a.getArea().contains(event.getBlock())) {
				new Thread(new OnCollapseBreakBlock(this, event.getBlock().getLocation(), a)).run();
			}
		}
	}
	@EventHandler
	public void OnBurnBlockInCauseArea(BlockBurnEvent event) {
		for(CollapseCauseArea a : causeAreas) {
			if(a.getArea().contains(event.getBlock())) {
				new Thread(new OnCollapseBreakBlock(this, event.getBlock().getLocation(), a)).run();
			}
		}
	}
	@EventHandler
	public void onShrapnelFall(EntityChangeBlockEvent event)
	{
	    if (event.getEntity() instanceof FallingBlock)
	    {
	        if(collapseShrapnel.contains(event.getEntity())) {
	        	//Shrapnel fell on the ground.
	        	if(MapManager.randInt(0, 3) == 1) {
	        		MapManager.currentMap.createExplosion(event.getBlock().getLocation().getX(), event.getBlock().getLocation().getY(), event.getBlock().getLocation().getZ(), 2, false, false);	
	        	}
	        	else if(MapManager.randInt(0, 3) == 2){
	        		MapManager.currentMap.createExplosion(event.getBlock().getLocation().getX(), event.getBlock().getLocation().getY(), event.getBlock().getLocation().getZ(), 3, true, true);	
	        	}
	        	collapseShrapnel.remove(event.getEntity());
	        }
	    }
	}
	@SuppressWarnings("deprecation")
	public void BeginCollapse(final Vector velocity) {
		if(collapsed == false) {
			ChatManager.log("Collapsing " + name);
			for(final CollapseArea ca : collapseAreas) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(PvPGamesManager.instance, new Runnable() {
					public void run() {
						for(Block b : ca.getArea()) {
							if(!b.getType().equals(Material.AIR)) {
								double height = b.getLocation().getY() - ca.getArea().getLowerY();
								Vector localVelocity = new Vector(velocity.getX(), velocity.getY(), velocity.getZ());
								if(ca.getSettings().getBoolean("useCollapseVector")) {
									//Setting X
									if(localVelocity.getX() < 0) {
										//X is negative.
										double x = localVelocity.getX() - (height / 50d);
										localVelocity.setX(x);
									}
									else {
										//X is positive.
										double x = localVelocity.getX() + (height / 50d);
										localVelocity.setX(x);	
									}
									//Setting Z
									if(localVelocity.getZ() < 0) {
										double z = localVelocity.getZ() - (height / 50d);
										localVelocity.setZ(z);
									}
									else {
										double z = localVelocity.getZ() + (height / 50d);
										localVelocity.setZ(z);
									}	
								}
								//Destroying the original block and spawning a falling block.
								FallingBlock fb = MapManager.currentMap.spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
								fb.setVelocity(localVelocity);
								collapseShrapnel.add(fb);
								b.setType(Material.AIR);
							}
						}
						collapsed = true;
					}				
				}, ca.getSettings().getLong("collapseDelay") * 20);	
			}
		}
	}
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return ChatColor.GREEN + "✔ ";
	}
	public GameTeam getOwningTeam() {
		return owningTeam;
	}
	public ObjectiveType getType() {
		return ObjectiveType.COLLAPSE;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public boolean isFinished() {
		return collapsed;
	}
	public boolean isFinishable() {
		return true;
	}
}
