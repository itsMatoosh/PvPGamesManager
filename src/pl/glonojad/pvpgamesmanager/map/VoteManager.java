package pl.glonojad.pvpgamesmanager.map;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.objects.IconMenu;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;

public class VoteManager {
	public static HashMap<Integer, String> mapsPositions = new HashMap<Integer, String>();
	public static void startMapVoting() {
		for(final Player p : Bukkit.getOnlinePlayers()) {
			final GamePlayer gp = GamePlayer.getGamePlayer(p.getName());
			//Closing all player's menus.
			p.closeInventory();
			Bukkit.getScheduler().scheduleSyncDelayedTask(PvPGamesManager.instance, new Runnable() {
				public void run() {
					//Creating the vote menu.
				     IconMenu voteMenu = new IconMenu(PvPGamesManager.language.get(p, "MAP_Vote-Menu-Title", ChatColor.BLUE + "" + ChatColor.BOLD + "Vote for next map!"), 45, new IconMenu.OptionClickEventHandler() {
				    	 public void onOptionClick(IconMenu.OptionClickEvent clickEvent) {
				    		 RotationManager.voteForMap(mapsPositions.get(clickEvent.getPosition()), gp);
				    		 clickEvent.setWillClose(true);
				    	 }
				     	}, PvPGamesManager.instance);
					//Setting up the vote menu.
				    int i = 0;
				    for(String map : RotationManager.mapsInRotation) {
				    	YamlConfiguration mapYml = FileManager.getMapYml(map);
						voteMenu.setOption(i, new ItemStack(Material.MAP), mapYml.getString("Name"), PvPGamesManager.language.get(p, "MAP_Click-To-Vote", ChatColor.GOLD + "Click here to vote for " + "{0}" + ChatColor.GOLD + "!", mapYml.getString("Name")));
						mapsPositions.put(i, map);
						i++;	
				    }
				    voteMenu.setSpecificTo(p);
					//Opening the menu.
					voteMenu.open(p);
					ChatManager.sendMessageToPlayer(gp, PvPGamesManager.language.get(p, "KIT_Choose-Kit", ChatColor.GREEN + "Choose your " + ChatColor.YELLOW + "kit" + ChatColor.GREEN + "!"));		
				}
			}, 20);
		}
	}
}
