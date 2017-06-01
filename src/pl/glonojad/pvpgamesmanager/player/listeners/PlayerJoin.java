package pl.glonojad.pvpgamesmanager.player.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.RotationManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.StatsManager;
import pl.glonojad.pvpgamesmanager.threads.EndCountdown;
import pl.glonojad.pvpgamesmanager.threads.InGameCountdown;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;
import pl.glonojad.pvpgamesmanager.util.ResourcePackManager;
import pl.glonojad.pvpgamesmanager.util.WeaponsManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

/**
 * Listens for the player join event.
 * @author Mateusz Rebacz
 *
 */
public class PlayerJoin implements Listener{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		//Caching the player.
		final Player player = event.getPlayer();
		
		//Managing GamePlayer for the joined player.
		final GamePlayer gPlayer = manageGamePlayer(player);
		
		//Setting the join message.
		event.setJoinMessage(null);
		ChatManager.broadcastMessage("PLAYER_Join-Server", ChatColor.YELLOW + "{0}" + ChatColor.GRAY + " joined the server", player.getName());
		
		//Loading the chunk for player.
		Chunk playerTeleportChunk = MapManager.currentMap.getChunkAt(MapManager.mapLobbyLocation);
		if(!(MapManager.currentMap.isChunkLoaded(playerTeleportChunk))) {
			MapManager.currentMap.loadChunk(playerTeleportChunk);
		}
		
		//Supplying the player.
		supplyPlayer(gPlayer);
		
		//Adding player to spectators team.
		GameTeam.getSpectators().add(gPlayer);

		//Fetching player stats.
		StatsManager.loadStatsForPlayer(gPlayer);
		
		//Teleporting player to lobby.
		player.teleport(MapManager.mapLobbyLocation);	
		
		//Giving player a proper resourcepack.
		if(WeaponsManager.gunsMode) {
			//Giving player guns texture.
			ResourcePackManager.changeResourcePack(ResourcePackManager.TexturePacksbyName.get("Guns"), gPlayer);
		}
		if (MapManager.mapConfiguration.isSet("Settings.resourcePack") && !MapManager.mapConfiguration.getString("Settings.resourcePack").equals("")) {
			ResourcePackManager.preChange(gPlayer, ResourcePackManager.TexturePacksbyName.get(MapManager.mapConfiguration.getString("Settings.resourcePack")));
		}	
		
		//Setting player's tablist message.
		//new TabTitleObject(ChatColor.RED + "" + ChatColor.BOLD + "PvP " + ChatColor.GREEN + "" + ChatColor.BOLD + "Games " + "" + ChatColor.BOLD + ChatColor.DARK_RED + "P" + "" + ChatColor.BOLD + ChatColor.WHITE + "L", PvPGamesManager.language.get(player, "GAME_Map", ChatColor.AQUA + "Map: " + ChatColor.YELLOW + "{0}", MapManager.currentMapName)).send(player);
		
		//Updating map interest stats.
		RotationManager.addCurrentMapOnlinePlayer();
		
		//Let's send player a welcome message!
		for(String line : FileManager.getConfig().getStringList("WelcomeMessage")) {
			ChatManager.sendMessageToPlayer(gPlayer, ChatColor.translateAlternateColorCodes('$', 
				line));
		}
		
		//Sending the join event to the countdown class.
		if(StartCountdown.current != null) {
			StartCountdown.current.onPlayerJoin(gPlayer);	
		}
		if(InGameCountdown.current != null) {
			InGameCountdown.current.onPlayerJoin(gPlayer);	
		}
		if(EndCountdown.current != null) {
			EndCountdown.current.onPlayerJoin(gPlayer);	
		}
	}
	/** Manages GamePlayer assignment of the joined player. */
	private GamePlayer manageGamePlayer(Player player) {
		//Refreshing player's GamePlayer.
		GamePlayer.refreshByPlayer(player);
		//Assigning the GamePlayer to player.
		final GamePlayer p;
		if(GamePlayer.getGamePlayer(player.getName()) == null) {
			p = new GamePlayer(player);	
		}
		else {
			p = GamePlayer.getGamePlayer(player.getName());		
		}
		
		return p;
	}
	/** Manages join message for the joined player. */
	private void supplyPlayer(GamePlayer player) {
		//Resetting player's inventory.
		player.resetInventory();
		
		//Giving player a welcome book.
		givePlayerWelcomeBook(player.getVanillaPlayer());
				
		//Reviving the player.
		player.getVanillaPlayer().setHealth(20);
		player.getVanillaPlayer().setFoodLevel(20);
	}
	/**
	 * Gives player the welcome book.
	 * @param p
	 */
	public void givePlayerWelcomeBook(Player p) {
		//Giving player a welcome book.
        ItemStack welcomeBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta welcomeBookInfo = (BookMeta)welcomeBook.getItemMeta();
        welcomeBookInfo.setTitle(ChatColor.translateAlternateColorCodes('$', FileManager.getBook().getString("Title")));
        welcomeBookInfo.setAuthor(ChatColor.translateAlternateColorCodes('$', FileManager.getBook().getString("Author")));
        ConfigurationPart pages = FileManager.getBook().getConfigurationPart("Book");
        for(String page : pages.getKeys(false)) {
        	List<String> lines = pages.getStringList(page);
        	String output = "";
        	for(String line : lines) {
        		output = output + "\n" + line;
        	}
        	welcomeBookInfo.addPage(ChatColor.translateAlternateColorCodes('$', output));
        }
        welcomeBook.setItemMeta(welcomeBookInfo);
        p.getInventory().addItem(welcomeBook);
	}
}