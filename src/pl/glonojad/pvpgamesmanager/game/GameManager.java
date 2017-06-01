package pl.glonojad.pvpgamesmanager.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.activator.ActivatorsManager;
import pl.glonojad.pvpgamesmanager.game.win.WinManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.RotationManager;
import pl.glonojad.pvpgamesmanager.map.VoteManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.EndCountdown;
import pl.glonojad.pvpgamesmanager.threads.InGameCountdown;
import pl.glonojad.pvpgamesmanager.threads.StartCountdown;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class GameManager{
	
	//Lobby settings.
	public static int secondsToStartGame;
	public static int secondsForGame;
	public static int secondsToEndGame;
	//Scoreboard settings.
	public static Scoreboard mainScoreboard;
	public static Objective objectives;
	public static GameState currentGameState = GameState.LOBBY;
	
	/** Sets up the game. */
	public static void setupGame() {
		//Setting up a new scoreboard.
		mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		//Setting up the SidebarManager.
		SidebarManager.initialize();
		new Thread(new SidebarManager()).start();
		
		//Loading the map chosen by the rotation system.
		loadChosenMap();
		
		//Creating the spectators team.
		createSpectatorsTeam();
		
		//Starting the pregame countdown.
		new StartCountdown().Start();
	}
	/** Loads the map chosen by players in the previous game.*/
	private static void loadChosenMap() {
		//Getting the map name that was chosen by players in the last game.
		String chosenMap = RotationManager.getMapToPlay();
		//Loading the chosen map.
		MapManager.loadMap(chosenMap);
		//Adding one play to the maps stats.
		RotationManager.addMapPlay(chosenMap);
	}
	/** Creates the default spectators team.*/
	private static void createSpectatorsTeam() {
		new GameTeam("Spectators", ChatColor.AQUA + "" + ChatColor.BOLD + "Spectators", ChatColor.GRAY + "", false);
	}
	/** Starts the game. 
	 * Forces players to join the match. */
	public static void startGame() {
		//Setting up activators. Activators are set now so they don't trigger during the lobby state.
		for(String activator : MapManager.mapConfiguration.getConfigurationPart("Activators").getKeys(false)) {
			ConfigurationPart activatorSettings = MapManager.mapConfiguration.getConfigurationPart("Activators." + activator);
			ActivatorsManager.createActivator(activator, activatorSettings);
		}
		
		//Changing the current game state.
		currentGameState = GameState.IN_GAME;
		
		//Assigning players to teams. Players are assigned equally among the teams.
		for(Player p : Bukkit.getOnlinePlayers()) {
			GamePlayer gp = GamePlayer.getGamePlayer(p.getName());
			//Clearing player's inventory.
			p.getInventory().clear();
			//Assigning player to the smallest team.
			GameTeam.getSmallestTeam().add(gp);
			//Spawning the player.
			gp.spawnPlayer();
		}
		
		//Starting the inGame countdown.
		InGameCountdown.current.Start();
	}
	/** Ends the running game. 
	 * Announces the winner of the game.
	 * Winner can be either GameTeam or GamePlayer.*/
	public static void endGame(Object winner){
		//Making sure the game didn't end yet.
		if(currentGameState != GameState.POST_GAME) {			
			//Changing the game state.
			currentGameState = GameState.POST_GAME;
			new EndCountdown();
			
			//Canceling the inGameCountDown.
			InGameCountdown.current.End();
			
			//Starting the map voting!
			VoteManager.startMapVoting();
			
			//Announcing the winner.
			if(winner instanceof GameTeam) {
				//The winner is a team.
				GameTeam winningTeam = (GameTeam) winner;
				EndCountdown.current.winners.add(winningTeam.getName());
				
				//Making every player a spectator.
				for(Player p : Bukkit.getOnlinePlayers()) {
					//Setting player's gamemode to spectator.
					p.setGameMode(GameMode.SPECTATOR);
					//Clearing player's inventory.
					p.getInventory().clear();
					//Send player a message.
					ChatManager.sendMessageToPlayer(GamePlayer.getGamePlayer(p.getName()), PvPGamesManager.language.get(p, "GAME_Winning-Team", ChatColor.GREEN + "Team " + "{0}" + ChatColor.GREEN + " won the game!", winningTeam.getVanillaTeam().getDisplayName()));
					ChatManager.sendMessageToPlayer(GamePlayer.getGamePlayer(p.getName()), PvPGamesManager.language.get(p, "GAME_Spectator", ChatColor.GRAY + "You are now a " + ChatColor.AQUA + "Specator"));
				}
			}
			if(winner instanceof GamePlayer) {
				//The winner is a player.
				GamePlayer winningPlayer = (GamePlayer) winner;
				EndCountdown.current.winners.add(winningPlayer.getDisplayName());
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					//Making everyone spectator!
					//Setting player's gamemode to spectator.
					p.setGameMode(GameMode.SPECTATOR);
					//Clearing player's inventory.
					p.getInventory().clear();
					//Send player a message.
					ChatManager.sendMessageToPlayer(GamePlayer.getGamePlayer(p.getName()), PvPGamesManager.language.get(p, "GAME_Winning-Team", ChatColor.GREEN + "Team " + "{0}" + ChatColor.GREEN + " won the game!", winningPlayer.getDisplayName()));
					ChatManager.sendMessageToPlayer(GamePlayer.getGamePlayer(p.getName()), PvPGamesManager.language.get(p, "GAME_Spectator", ChatColor.GRAY + "You are now a " + ChatColor.AQUA + "Spectator"));
				}
			}
			
			//Starting the end countdown.
			if(!EndCountdown.current.running) {
				EndCountdown.current.Start();		
			}
		}
	}
	public static void endGame() {
		if(currentGameState != GameState.POST_GAME) {
			//Checking winner using Draw Manager.
			if(MapManager.mapConfiguration.getString("Draw.mode").equals("TEAM")) {
				ArrayList<GameTeam> winningTeams = WinManager.getWinningTeams(MapManager.mapConfiguration.getConfigurationPart("Draw"));
				if(winningTeams == null) {
					final String winMessage = ChatColor.YELLOW + "Remis";
					InGameCountdown.current.End();
					//A match ended with a draw.
					currentGameState = GameState.POST_GAME;
					for(Player p : Bukkit.getOnlinePlayers()) {
						//Making everyone a spectator!
						p.setGameMode(GameMode.SPECTATOR);
					}
					//Starting the countdown.
					if(EndCountdown.current == null || !EndCountdown.current.running) {
						new EndCountdown(null, winMessage).Start();	
					}
					//Starting the map voting!
					VoteManager.startMapVoting();
					return;
				}
				if(winningTeams.size() > 1) {
					//There is a draw between teams.
					InGameCountdown.current.End();
					//A match ended with a draw.
					currentGameState = GameState.POST_GAME;
					for(Player p : Bukkit.getOnlinePlayers()) {
						//Making everyone a spectator!
						p.setGameMode(GameMode.SPECTATOR);
					}
					//Starting the countdown.
					ArrayList<String> winners = new ArrayList<String>();
					for(GameTeam t : winningTeams) {
						winners.add(t.getVanillaTeam().getDisplayName());
					}
					if(EndCountdown.current == null || !EndCountdown.current.running) {
						new EndCountdown(winners, null).Start();	
					}
					//Starting the map voting!
					VoteManager.startMapVoting();
					return;
				} else {
					//Only 1 team won.
					endGame(winningTeams.get(0));
					return;
				}	
			}
			else if(MapManager.mapConfiguration.getString("Draw.mode").equals("PLAYER")) {
				ArrayList<GamePlayer> winningPlayers = WinManager.getWinningPlayers(MapManager.mapConfiguration.getConfigurationPart("Draw"));
				if(winningPlayers == null) {
					final String winMessage = ChatColor.YELLOW + "Remis";
					InGameCountdown.current.End();
					//A match ended with a draw.
					currentGameState = GameState.POST_GAME;
					for(Player p : Bukkit.getOnlinePlayers()) {
						//Making everyone a spectator!
						p.setGameMode(GameMode.SPECTATOR);
					}
					//Starting the countdown.
					if(EndCountdown.current == null || !EndCountdown.current.running) {
						new EndCountdown(null, winMessage).Start();	
					}
					//Starting the map voting!
					VoteManager.startMapVoting();
					return;
				}
				if(winningPlayers.size() > 1) {
					//There is a draw between teams.
					InGameCountdown.current.End();
					//A match ended with a draw.
					currentGameState = GameState.POST_GAME;
					for(Player p : Bukkit.getOnlinePlayers()) {
						//Making everyone a spectator!
						p.setGameMode(GameMode.SPECTATOR);
					}

					//Starting the countdown.
					ArrayList<String> winners = new ArrayList<String>();
					for(GamePlayer p : winningPlayers) {
						winners.add(p.getDisplayName());
					}
					if(EndCountdown.current == null || !EndCountdown.current.running) {
						new EndCountdown(winners, null).Start();	
					}
					//Starting the map voting!
					VoteManager.startMapVoting();
					return;
				} else {
					//Only 1 team won.
					endGame(winningPlayers.get(0));
					return;
				}	
			}
			else {
				final String winMessage = ChatColor.YELLOW + "Remis";
				InGameCountdown.current.End();
				//A match ended with a draw.
				currentGameState = GameState.POST_GAME;
				for(Player p : Bukkit.getOnlinePlayers()) {
					//Making everyone a spectator!
					p.setGameMode(GameMode.SPECTATOR);
				}
				
				//Starting the countdown.
				if(EndCountdown.current == null || !EndCountdown.current.running) {
					new EndCountdown(null, winMessage).Start();	
				}
				//Starting the map voting!
				VoteManager.startMapVoting();	
			}
		}
	}
	/**
	 * Updates the scoreboard.
	 */
	public static void updateScoreboard() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(mainScoreboard);
		}	
	}
}