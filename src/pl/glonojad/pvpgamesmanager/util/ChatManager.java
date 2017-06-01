package pl.glonojad.pvpgamesmanager.util;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatManager implements Listener {
	public static ConsoleCommandSender console;

	public static void Initialize() {
		console = PvPGamesManager.instance.getServer().getConsoleSender();
		PvPGamesManager.pluginPrefix = ChatColor.BLACK + "[" + ChatColor.DARK_GRAY + FileManager.getConfig().getString("PluginPrefix") + ChatColor.BLACK + "]" + ChatColor.RESET;
	}
	public static void log(String message) {
		console.sendMessage(PvPGamesManager.pluginPrefix + ChatColor.AQUA + " " + message);
	}
	public static void logWarning(String message) {
		console.sendMessage(PvPGamesManager.pluginPrefix + ChatColor.YELLOW + " " + message);
	}
	public static void logError(String message) {
		console.sendMessage(PvPGamesManager.pluginPrefix + ChatColor.RED + " " + message);
	}
	public static void sendMessageToPlayer(GamePlayer p, String message) {
		p.getVanillaPlayer().sendMessage(PvPGamesManager.pluginPrefix + " " + message);
	}
	public static void broadcastMessage(String key, String message, Object...params) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			online.sendMessage(PvPGamesManager.pluginPrefix + " " + PvPGamesManager.language.get(online, key, message, params));
		}
	}
	@EventHandler
	public void onPlayerSendMessage(AsyncPlayerChatEvent event) {
		//Caching variables.
		GamePlayer p = GamePlayer.getGamePlayer(event.getPlayer().getName());
		PermissionUser user = PermissionsEx.getUser(p.getVanillaPlayer());
		//Highlighting players.
		for(Player online : Bukkit.getOnlinePlayers()) {
			if(Pattern.compile(Pattern.quote(online.getName()), Pattern.CASE_INSENSITIVE).matcher(event.getMessage()).find()) {
				String highlitedMessage = event.getMessage();
				highlitedMessage = highlitedMessage.replaceAll("(?i)" + online.getName(), online.getDisplayName() + ChatColor.GRAY);
				event.setMessage(highlitedMessage);
			}
		}
		if(p.getVanillaPlayer().hasPermission("chat.color")) {
			event.setFormat(ChatColor.RED + "[" + ChatColor.GOLD + "LVL " + p.getLevel() + ChatColor.RED + "] " + ChatColor.translateAlternateColorCodes('&', user.getPrefix()) + " " + event.getPlayer().getDisplayName() + ChatColor.BLUE + " ⟩⟩ " + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		else {
			event.setFormat(ChatColor.RED + "[" + ChatColor.GOLD + "LVL " + p.getLevel() + ChatColor.RED + "] " + ChatColor.translateAlternateColorCodes('&', user.getPrefix()) + " " + event.getPlayer().getDisplayName() + ChatColor.BLUE + " ⟩⟩ " + ChatColor.GRAY + event.getMessage());
		}
	}
}