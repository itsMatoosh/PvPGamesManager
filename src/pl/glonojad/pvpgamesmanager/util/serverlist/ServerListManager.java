package pl.glonojad.pvpgamesmanager.util.serverlist;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import org.spigotmc.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server.Spigot;
import org.bukkit.event.Listener;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.threads.EndCountdown;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.util.FileManager;

/**
 * Manages the appearance of the server on the list.
 * @author Mateusz Rêbacz
 *
 */
public class ServerListManager implements Listener {
	//Pregame
	static String pregameMOTD;
	//Ingame
	static String gameMOTD;
	//Endgame
	static String endgameMOTD;
	//Player count.
	static String playerCountMessage;
	
	/**
	 * Initializes the ServerListManager.
	 */
	public static void Initialize() {
		//Caching the motds.
		//PregameMOTD
		pregameMOTD = FileManager.getServerMOTD().getString("MOTD.pregame");
		//GameMOTD
		gameMOTD = FileManager.getServerMOTD().getString("MOTD.inGame");
		//EndgameMOTD
		endgameMOTD = FileManager.getServerMOTD().getString("MOTD.endgame");
		
		//ProtocolLib
		//Registering the ProtocolLib listener.
		PvPGamesManager.protocol.addPacketListener(
            new PacketAdapter(PacketAdapter
                            .params(PvPGamesManager.instance, PacketType.Status.Server.SERVER_INFO, PacketType.Status.Server.PONG)
                            .listenerPriority(ListenerPriority.HIGHEST)){
                    @SuppressWarnings("deprecation")
					@Override
                    public void onPacketSending(PacketEvent e) {
                        if(e.getPacket().getType() == PacketType.Status.Server.SERVER_INFO){
                        	//Checking if maintenance is active.
                            if(!e.getPacket().getServerPings().read(0).getVersionName().equals("Nudel1.0")){
                                //Customising the status.
                            	//MOTD\\
                            	e.getPacket().getServerPings().read(0).setMotD(getMOTD());
                            	//Player count\\
                                e.getPacket().getServerPings().read(0).setPlayersVisible(true);
                                e.getPacket().getServerPings().read(0).setPlayersOnline(Bukkit.getOnlinePlayers().size());
                                //Player count message\\
                                //e.getPacket().getServerPings().read(0).setVersionProtocol(420);
                                //e.getPacket().getServerPings().read(0).setVersionName(ChatColor.translateAlternateColorCodes('$', addArguments(playerCountMessage)));
                                
                                //Player list content\\
                                /*int lastID = 4;
                                String desc = MapManager.currentMapDescription;
                                String[] lines = desc.split("\n");
                                ArrayList<WrappedGameProfile> description = new ArrayList<WrappedGameProfile>();
                                //Adding header to player list content.
                               description.addAll(Arrays.asList(
                        				new WrappedGameProfile("id1", "        " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "PvP " + 
                        					ChatColor.AQUA + "" + ChatColor.BOLD + "Games " + 
                        					ChatColor.DARK_RED + "" + ChatColor.BOLD + "PL"),
                        					new WrappedGameProfile("id2", ChatColor.YELLOW + " ============================== "),
                        					new WrappedGameProfile("id3", ChatColor.GREEN + "Aktualna mapa: " + ChatColor.AQUA + MapManager.currentMapName)
                        		));
                                for(String line : lines) {
                                	lastID++;
                                	description.add(new WrappedGameProfile("id" + lastID, "  " + ChatColor.translateAlternateColorCodes('$', line)));
                                }
                                e.getPacket().getServerPings().read(0).setPlayers(description);*/
                            }
                        }
                        if(e.getPacket().getType() == PacketType.Status.Server.PONG){
                            //d("Ping got in, cancelling");
                            e.setCancelled(true);
                        }
                    }
                }
            );
	}
	/**
	 * Gets the current MOTD of the server.
	 * @return
	 */
	public static String getMOTD() {
		if(GameManager.currentGameState.equals(GameState.LOBBY)) {
			//Game hasn't started yet!
			return ChatColor.translateAlternateColorCodes('$', addArguments(pregameMOTD));
		}
		if(GameManager.currentGameState.equals(GameState.IN_GAME)) {
			//The game has started.
			return ChatColor.translateAlternateColorCodes('$', addArguments(gameMOTD));
		}
		if(GameManager.currentGameState.equals(GameState.POST_GAME)) {
			//The game ended.
			return ChatColor.translateAlternateColorCodes('$', addArguments(endgameMOTD));
		}
		return "";
	}
	/**
	 * Replaces the arguments in a MOTD message.
	 * @param input
	 * @return
	 */
	static String addArguments(String input) {
		String output = input;
		if(output.contains("mapname")) {
		 	output = output.replace("mapname", MapManager.currentMapName);
		}
		if(output.contains("mapversion")) {
		 	output = output.replace("mapversion", MapManager.currentMapVersion);
		}
		if(output.contains("mapauthor")) {
		 	output = output.replace("mapauthor", MapManager.currentMapAuthor);
		}
		if(output.contains("pregamesec")) {
			output = output.replace("pregamesec", "" + StartCountdown.current.time);
		}
		if(output.contains("endgamesec")) {
			output = output.replace("endgamesec", "" + EndCountdown.current.time);
		}
		if(output.contains("onlineplayers")) {
			output = output.replace("onlineplayers", "" + Bukkit.getOnlinePlayers().size());
		}
		if(output.contains("maxplayers")) {
			output = output.replace("maxplayers", "" + Bukkit.getMaxPlayers());
		}
		return output;
	}
}
