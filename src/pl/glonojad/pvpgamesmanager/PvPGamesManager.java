package pl.glonojad.pvpgamesmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.glonojad.pvpgamesmanager.commands.CommandManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.listeners.ProtectionManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.RotationManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.listeners.PlayerDamage;
import pl.glonojad.pvpgamesmanager.player.listeners.PlayerDeath;
import pl.glonojad.pvpgamesmanager.player.listeners.PlayerExpGet;
import pl.glonojad.pvpgamesmanager.player.listeners.PlayerJoin;
import pl.glonojad.pvpgamesmanager.player.listeners.PlayerLeave;
import pl.glonojad.pvpgamesmanager.player.listeners.PlayerRespawn;
import pl.glonojad.pvpgamesmanager.tracker.listeners.EntityListener;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;
import pl.glonojad.pvpgamesmanager.util.LanguageWrapper;
import pl.glonojad.pvpgamesmanager.util.ResourcePackManager;
import pl.glonojad.pvpgamesmanager.util.serverlist.ServerListManager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

/**
 * Main class of the plugin.
 * @author Mateusz Rebacz
 *
 */
public class PvPGamesManager extends JavaPlugin{
	
	public static PvPGamesManager instance;
	public static LanguageWrapper language;
	public static ProtocolManager protocol;
	public static String pluginPrefix;
	
	/** Called when the plugin starts. */
	@Override
	public void onEnable() {
		//Initializing the plugin.
		Initialize();
		//Setting up the new game.
		GameManager.setupGame();
	}
	/** Initializes the plugin. */
	public void Initialize() {
		//Setting plugin instance.
		instance = this;
		//Setting default message language to English.
		language = new LanguageWrapper(this, "eng");
		//Getting ProtocolManager.
		protocol = ProtocolLibrary.getProtocolManager();
		
		//Initializing components.
		InitComponents();
		
		//Registering plugin listeners.
		RegisterListeners();
	}
	/** Initializes all of the plugin components. */
	void InitComponents() {
		FileManager.loadData();
		ChatManager.Initialize();
		ResourcePackManager.Initialize();
		ServerListManager.Initialize();
	}
	/** Registers default plugin Listeners. */
	void RegisterListeners() {
		//Player Listeners
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerExpGet(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDamage(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
		//Other Listeners
		Bukkit.getPluginManager().registerEvents(new ProtectionManager(), this);
		Bukkit.getPluginManager().registerEvents(new ChatManager(), this);
	}
	/** Called when the plugin is disabled. */
	@Override
	public void onDisable() {
		//Kicking players from the server.
		for(Player p : Bukkit.getOnlinePlayers()) {
			GamePlayer.getGamePlayer(p.getName()).kick((PvPGamesManager.language.get(p, "GAME_End-Kick-No-Win-Message", ChatColor.GOLD + "The next game will begin in few seconds! " + ChatColor.RED + "Please rejoin the server!")));
		}
		//Unloading the world
		Bukkit.getServer().unloadWorld(MapManager.currentMap, true);
		//Updating the last game map.
		FileManager.getRotation().set("LastGameMap", MapManager.currentMapFolder.getName());
		//Saving rotation stats.
		RotationManager.saveCurrentStats();
		//Removing temp map folder from the root server dir.
		FileManager.removeMapFromMainServerFolder(MapManager.currentMapFolder);
		//GC to gain some performance.
		System.gc();
	}
	/** Called when a command is issued. */
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		return CommandManager.execute(sender, command, label, args);
	}
}