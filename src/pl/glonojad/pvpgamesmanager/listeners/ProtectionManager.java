package pl.glonojad.pvpgamesmanager.listeners;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class ProtectionManager implements Listener{
	//Areas storage.
	public static HashMap <String, ConfigurationPart> areasSettings = new HashMap <String, ConfigurationPart>();
	public static HashMap <Area, String> areasNames = new HashMap <Area, String>();
	//Materials storage.
	public static HashMap <Material, ConfigurationPart> materialsSettings = new HashMap <Material, ConfigurationPart>();
	
	public static void createArea(String areaName, Location l1, Location l2, ConfigurationPart configurationPart) {
		Area newArea = new Area(l1, l2);
		//Putting new area into area storage.
		areasNames.put(newArea, areaName);
		areasSettings.put(areaName, configurationPart);
	}
	public static void registerProtectedMaterial(Material m,ConfigurationPart materialSettings) {
		//Putting new protected material into material storage.
		materialsSettings.put(m, materialSettings);
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
		if(!p.canBreakBlocks() || GameTeam.getPlayerTeam(p).getName().equals("Spectators")) {
			event.setCancelled(true);
		}
		if(ProtectionManager.materialsSettings.containsKey(event.getBlock().getType())) {
			//This block type is protected.
			ConfigurationPart materialSettings = ProtectionManager.materialsSettings.get(event.getBlock().getType());
			if(materialSettings.getBoolean("break") == false) {
				//This material cannot be broken.
				event.setCancelled(true);
				return;
			}
		}
		for(Entry<Area, String>entries : ProtectionManager.areasNames.entrySet()) {
			Area area = entries.getKey();
			String areaName = entries.getValue();
			if(area.contains(event.getBlock())) {
				//The block that was broken by a player is in the area.
				ConfigurationPart areaSettings = ProtectionManager.areasSettings.get(areaName);
				if(areaSettings.isList("break.teams")) {
					//There are configured teams, that can break in this region.
					for(Object key : areaSettings.getList("break.teams")) {
						//Checking of player can break in that region.
						if(!(GameTeam.getPlayerTeam(p).getName().equals(key))) {
							//Player can't break blocks in that region.
							event.setCancelled(true);
							ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PROTECTION_Cant-Break", 
								ChatColor.RED + "You can't break blocks here!"));
						}
					}
				}
				else {
					//No one can break blocks here!
					event.setCancelled(true);
					ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PROTECTION_Cant-Break", 
						ChatColor.RED + "You can't break blocks here!"));
				}
			}
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
		if(!p.canPlaceBlocks() || GameTeam.getPlayerTeam(p).getName().equals("Spectators")) {
			event.setCancelled(true);
		}
		if(ProtectionManager.materialsSettings.containsKey(event.getBlock().getType())) {
			//This block type is protected.
			ConfigurationPart materialSettings = ProtectionManager.materialsSettings.get(event.getBlock().getType());
			if(materialSettings.getBoolean("place") == false) {
				//This material cannot be broken.
				event.setCancelled(true);
				return;
			}
		}
		for(Entry<Area, String>entries : ProtectionManager.areasNames.entrySet()) {
			Area area = entries.getKey();
			String areaName = entries.getValue();
			if(area.contains(event.getBlock())) {
				//The block that was broken by a player is in the area.
				ConfigurationPart areaSettings = ProtectionManager.areasSettings.get(areaName);
				if(areaSettings.isList("place.teams")) {
					//There are configured teams, that can break in this region.
					for(Object key : areaSettings.getList("place.teams")) {
						//Checking of player can break in that region.
						if(!(GameTeam.getPlayerTeam(p).getName().equals(key))) {
							//Player can't break blocks in that region.
							event.setCancelled(true);
							ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PROTECTION_Cant-Place", 
								ChatColor.RED + "You can't place blocks here!"));
						}
					}
				}
				else {
					//No one can place blocks here!
					event.setCancelled(true);
					ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "PROTECTION_Cant-Place", 
						ChatColor.RED + "You can't place blocks here!"));
				}
			}
		}
	}
	@EventHandler
	public void onExplosionInProtectedArea(EntityExplodeEvent event) {
		for(Block b : event.blockList()) {
			if(materialsSettings.containsKey(b.getType())) {
				//This block type is protected.
				ConfigurationPart materialSettings = materialsSettings.get(b.getType());
				if(materialSettings.getBoolean("explode") == false) {
					//This material cannot be broken.
					event.blockList().clear();
					return;
				}
			}
			for(Entry<Area, String>entries : areasNames.entrySet()) {
				Area area = entries.getKey();
				String areaName = entries.getValue();
				if(area.contains(b.getLocation())) {
					ConfigurationPart areaSettings = areasSettings.get(areaName);
					if(areaSettings.isSet("explosion-block-damage")) {
						if(areaSettings.getBoolean("explosion-block-damage") == false) {
							//The explosion happened in an explosion-protected area.
							//Canceling the explosion.
							event.blockList().clear();
							return;
						}
					}	
				}
			}
		}
	}
}
