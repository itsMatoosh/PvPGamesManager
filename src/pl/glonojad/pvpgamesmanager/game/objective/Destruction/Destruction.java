package pl.glonojad.pvpgamesmanager.game.objective.Destruction;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.Objective;
import pl.glonojad.pvpgamesmanager.game.objective.ObjectiveType;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.EffectsManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class Destruction implements Objective, Listener {

	public String name;
	public GameTeam owningTeam;
	public Area destructionArea;
	public ConfigurationPart settings;
	public float destructionPercent;
	public ArrayList<Location> blocks =  new ArrayList<Location>();
	public String lastPlayerBroken;
	public int totalBlocks = 0;
	public boolean isDestructed = false;
	
	/** Creates a new Destruction area. */
	public Destruction(String name, GameTeam gameTeam, Area destructionArea, ConfigurationPart settings) {
		this.name = name;
		this.owningTeam = gameTeam;
		this.destructionArea = destructionArea;
		this.settings = settings;
		for(Block b : destructionArea) {
			if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.BEDROCK)) {
				blocks.add(b.getLocation());
				totalBlocks++;
			}
		}
		ChatManager.log("Destruction area " + name + " has " + blocks.size() + " blocks!");
		SidebarManager.addObjective(this);
	}
	@EventHandler
	public void onExplosionDestruction(EntityExplodeEvent event) {
		if(!isDestructed) {
			ArrayList<Location> destroyedBlocks = new ArrayList<Location>();
			for(Block b : event.blockList()) {
				if(blocks.contains(b.getLocation())) {
					//A part of the area has been destroyed by an explosion.
					if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.BEDROCK)) {
						destroyedBlocks.add(b.getLocation());
					}
				}
			}
			if(destroyedBlocks.size() != 0) {
				//Setting the last player broken.
				if(event.getEntityType().equals(EntityType.PRIMED_TNT)) {
					TNTPrimed tnt = (TNTPrimed) event.getEntity();
					if(tnt.getSource() != null) {
						lastPlayerBroken = ((Player)tnt.getSource()).getName();
					}
				}
				new Thread(new OnDestructionBreakBlock(this, destroyedBlocks)).run();
			}	
		}
	}
	@EventHandler
	public void onPlayerDestruction(BlockBreakEvent event) {
		if(!isDestructed) {
			if(GamePlayer.getGamePlayer(event.getPlayer().getName()).getTeam().getName().equals("Spectators")) {
				event.setCancelled(true);
				return;
			}
			if(blocks.contains(event.getBlock().getLocation())) {
				//Setting the last player broken.
				lastPlayerBroken = event.getPlayer().getName();
				new Thread(new OnDestructionBreakBlock(this, 1, event.getBlock().getLocation())).run();
				if(MapManager.randInt(0, 2) == 1) {
					EffectsManager.spawnExperience(event.getBlock().getLocation(), 1);
				}
			}
		}
	}
	@EventHandler
	public void onFireDestruction(BlockBurnEvent event) {
		if(!isDestructed) {
			if(blocks.contains(event.getBlock().getLocation())) {
				new Thread(new OnDestructionBreakBlock(this, 1, event.getBlock().getLocation())).run();
			}
		}
	}
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return ChatColor.RED + "0% ";
	}
	public ObjectiveType getType() {
		return ObjectiveType.DESTRUCTION;
	}
	public GameTeam getOwningTeam() {
		return owningTeam;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public boolean isFinished() {
		return isDestructed;
	}
	public boolean isFinishable() {
		return true;
	}
}