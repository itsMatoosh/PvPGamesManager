package pl.glonojad.pvpgamesmanager.game.actions;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameState;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.Flag;
import pl.glonojad.pvpgamesmanager.game.objective.ObjectivesManager;
import pl.glonojad.pvpgamesmanager.game.objective.Collapse.Collapse;
import pl.glonojad.pvpgamesmanager.game.win.WinManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.VoteManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.EndCountdown;
import pl.glonojad.pvpgamesmanager.threads.InGameCountdown;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class ActionsManager {

	public static void trigger(final String actionName, @SuppressWarnings("rawtypes") final HashMap arguments) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(PvPGamesManager.instance, new Runnable() {
			public void run() {
				//Getting action settings.
				ConfigurationPart actionSettings = MapManager.mapConfiguration.getConfigurationPart("Actions." + actionName);
				if(actionSettings.getString("type").equals("ADD_POINTS")) {
					int amount = actionSettings.getInt("amount");
					//The type of action is ADD_POINTS.
					if(GameTeam.getTeam(addArguments(actionSettings.getString("forWho"), actionSettings.getConfigurationPart("arguments"), arguments)) != null) {
						GameTeam.getTeam(addArguments(actionSettings.getString("forWho"), actionSettings.getConfigurationPart("arguments"), arguments)).addPoints(amount);	
					}
					else if (GamePlayer.getGamePlayer(addArguments(actionSettings.getString("forWho"), actionSettings.getConfigurationPart("arguments"), arguments)) != null){
						GamePlayer.getGamePlayer(addArguments(actionSettings.getString("forWho"), actionSettings.getConfigurationPart("arguments"), arguments)).addPoints(amount);
					}
				}
				if(actionSettings.getString("type").equals("RESPAWN_FLAG")) {
					//The type of action is RESPAWN_FLAG.
					Flag.getFlag(addArguments(actionSettings.getString("flagName"), actionSettings.getConfigurationPart("arguments"), arguments)).respawnFlag();
				}
				if(actionSettings.getString("type").equals("SPAWN_PLAYER")) {
					//The type of action is SPAWN_PLAYER.
					GamePlayer.getGamePlayer(addArguments(actionSettings.getString("player"), actionSettings.getConfigurationPart("arguments"), arguments)).spawnPlayer();
				}
				if(actionSettings.getString("type").equals("ADD_PLAYER_TO_TEAM")) {
					//The type of action is ADD_PLAYER_TO_TEAM.
					GamePlayer p = GamePlayer.getGamePlayer(addArguments(actionSettings.getString("player"), actionSettings.getConfigurationPart("arguments"), arguments));
					p.getTeam().remove(p);
					GameTeam.getTeam(addArguments(actionSettings.getString("team"), actionSettings.getConfigurationPart("arguments"), arguments)).add(p);
				}
				if(actionSettings.getString("type").equals("TRIGGER_COLLAPSE")) {
					//The type of action is TRIGGER_COLLAPSE.
					Collapse col = (Collapse) ObjectivesManager.getObjective(addArguments(actionSettings.getString("collapse"), actionSettings.getConfigurationPart("arguments"), arguments));
					col.BeginCollapse(new Vector(
							actionSettings.getDouble("collapseVector.x"),
							actionSettings.getDouble("collapseVector.y"),
							actionSettings.getDouble("collapseVector.z")));
				}
				if(actionSettings.getString("type").equals("CHECKER")) {
					//The type of action is CHECKER.
					if(new Checker().check(addArguments(actionSettings.getString("condition"), actionSettings.getConfigurationPart("arguments"), arguments), addArguments(actionSettings.getString("value"), actionSettings.getConfigurationPart("arguments"), arguments))) {
						//The condition is obeyed.
						//Running specified actions.
						for (String key : actionSettings.getStringList("actions")) {
							//Firing each action.
							//Adding the arguments to the action.
							HashMap<String, Object> args=new HashMap<String, Object>();
							for(String argument : actionSettings.getConfigurationPart("forwardedArguments").getKeys(false)) {
								//Adding argument.
								args.put(actionSettings.getString("forwardedArguments." + argument + ".type"), addArguments(actionSettings.getString("forwardedArguments." + argument + ".value"), actionSettings.getConfigurationPart("arguments"), arguments));
							}
							//Adding ACTIVATOR_NAME
							args.put("OBJECTIVE_NAME", actionName);
							ActionsManager.trigger(key, args);
							ChatManager.log(key);
						}
					}
				}
				if(actionSettings.getString("type").equals("END_GAME")) {
					//The type of action is END_GAME.
					//Checking winner using Draw Manager.
					if(actionSettings.getString("mode").equals("TEAM")) {
						ArrayList<GameTeam> winningTeams = WinManager.getWinningTeams(actionSettings);
						if(winningTeams == null) {
							return;
						}
						if(winningTeams.size() > 1) {
							//There is a draw between teams.
							InGameCountdown.current.End();
							//A match ended with a draw.
							GameManager.currentGameState = GameState.POST_GAME;
							for(Player p : Bukkit.getOnlinePlayers()) {
								//Making everyone a spectator!
								p.setGameMode(GameMode.SPECTATOR);
							}
							//Starting the countdown.
							ArrayList<String> winners = new ArrayList<String>();
							for(GameTeam t : winningTeams) {
								winners.add(t.getVanillaTeam().getDisplayName());
							}
							new EndCountdown(winners, null).Start();
							//Starting the map voting!
							VoteManager.startMapVoting();
							return;
						} else {
							//Only 1 team won.
							GameManager.endGame(winningTeams.get(0));
							return;
						}	
					}
					else if(actionSettings.getString("mode").equals("PLAYER")) {
						ArrayList<GamePlayer> winningPlayers = WinManager.getWinningPlayers(actionSettings);
						if(winningPlayers == null) {
							return;
						}
						if(winningPlayers.size() > 1) {
							//There is a draw between teams.
							InGameCountdown.current.End();
							//A match ended with a draw.
							GameManager.currentGameState = GameState.POST_GAME;
							for(Player p : Bukkit.getOnlinePlayers()) {
								//Making everyone a spectator!
								p.setGameMode(GameMode.SPECTATOR);
							}
							//Starting the countdown.
							ArrayList<String> winners = new ArrayList<String>();
							for(GamePlayer p : winningPlayers) {
								winners.add(p.getDisplayName());
							}
							new EndCountdown(winners, null).Start();
							//Starting the map voting!
							VoteManager.startMapVoting();
							return;
						} else {
							//Only 1 player won.
							GameManager.endGame(winningPlayers.get(0));
							return;
						}
					}
					else {
			    		//Ending the game.
						if(!actionSettings.isSet("winner") || actionSettings.getString("winner").equals("")) {
							GameManager.endGame();
						}
						if(GameTeam.getTeam(addArguments(actionSettings.getString("winner"), actionSettings.getConfigurationPart("arguments"), arguments)) != null) {
				    		GameManager.endGame(GameTeam.getTeam(addArguments(actionSettings.getString("winner"), actionSettings.getConfigurationPart("arguments"), arguments)));	
						}
						else if (GamePlayer.getGamePlayer(addArguments(actionSettings.getString("winner"), actionSettings.getConfigurationPart("arguments"), arguments)) != null){
							GameManager.endGame(GamePlayer.getGamePlayer(addArguments(actionSettings.getString("winner"), actionSettings.getConfigurationPart("arguments"), arguments)));
						}	
					}
				}
				ChatManager.log("Running action " + actionName);
			}
		}, 1);
	}
	public static String addArguments(String input, ConfigurationPart argumentsSection, @SuppressWarnings("rawtypes") HashMap argumentsMeanings) {
		String output = input;
		for(String key : argumentsSection.getKeys(false)) {
			String replaceTo = "";
			if(output.contains(key)) {
				//Global arguments.
				if(argumentsSection.getString(key + ".type").contains("GLOBAL-PLAYER_ON_LIST")) {
					int index = Integer.parseInt(argumentsSection.getString(key + ".type").split(":")[1]);
					int playerID = -1;
					for(Player p : Bukkit.getOnlinePlayers()) {
						playerID++;
						if(playerID == index) {
							replaceTo = p.getName();
						}
					}
					replaceTo = "NOT FOUND";
				}
				//Standard arguments
				if(argumentsSection.getString(key + ".type").equals("TEAM_VICTIM")) {
					replaceTo = (String) argumentsMeanings.get("TEAM_VICTIM");
				}
				if(argumentsSection.getString(key + ".type").equals("TEAM_SOURCE")) {
					replaceTo = (String) argumentsMeanings.get("TEAM_SOURCE");
				}
				if(argumentsSection.getString(key + ".type").equals("PLAYER_VICTIM")) {
					replaceTo = (String) argumentsMeanings.get("PLAYER_VICTIM");
				}
				if(argumentsSection.getString(key + ".type").equals("PLAYER_SOURCE")) {
					replaceTo = (String) argumentsMeanings.get("PLAYER_SOURCE");
				}
				if(argumentsSection.getString(key + ".type").equals("OBJECTIVE_NAME")) {
					replaceTo = (String) argumentsMeanings.get("OBJECTIVE_NAME");
				}
			}
			output = output.replaceAll(key, replaceTo);
		}
		return output;
	}
}