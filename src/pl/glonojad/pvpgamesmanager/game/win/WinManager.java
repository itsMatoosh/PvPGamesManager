package pl.glonojad.pvpgamesmanager.game.win;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.Objective;
import pl.glonojad.pvpgamesmanager.game.objective.ObjectivesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class WinManager {
	static boolean useTeamPoints;
	static boolean usePlayerPoints;
	static boolean usePlayerLives;
	static boolean useTeamObjectives;
	static int desiredTeamPoints = 0;
	static int desiredPlayerPoints = 0;
	static int desiredPlayerLives = 0;
	public static ArrayList<GameTeam> getWinningTeams(ConfigurationPart drawSettings) {
		//Which stats should DrawManager use?
		if(drawSettings.getString("usedStats.teamPoints").equals("true") || drawSettings.getString("usedStats.teamPoints").equals("false")) {
			useTeamPoints = drawSettings.getBoolean("usedStats.teamPoints");	
		}
		else {
			useTeamPoints = true;
			desiredTeamPoints = drawSettings.getInt("usedStats.teamPoints");
		}
		useTeamObjectives = drawSettings.getBoolean("usedStats.teamObjectives");
		if(drawSettings.getString("usedStats.playerPoints").equals("true") || drawSettings.getString("usedStats.playerPoints").equals("false")) {
			usePlayerPoints = drawSettings.getBoolean("usedStats.playerPoints");	
		}
		else {
			usePlayerPoints = true;
			desiredPlayerPoints = drawSettings.getInt("usedStats.playerPoints");
		}
		if(drawSettings.getString("usedStats.playerLives").equals("true") || drawSettings.getString("usedStats.playerLives").equals("false")) {
			usePlayerLives = drawSettings.getBoolean("usedStats.playerLives");	
		}
		else {
			usePlayerLives = true;
			desiredPlayerLives = drawSettings.getInt("usedStats.playerLives");
		}
		
		//DrawPoints
		//Used to identify the winning team.
		HashMap<GameTeam, Integer> drawPoints = new HashMap<GameTeam, Integer>();
		//Adding teams to drawPoints table.
		for(GameTeam t : GameTeam.teams) {
			drawPoints.put(t, 0);
		}
		//Checking team points.
		if(useTeamPoints) {
			//Adding draw points for each team.
			for(GameTeam t : getTeamsWithMostPoints()) {
				drawPoints.put(t, drawPoints.get(t) + 1);
			}
		}
		//Checking player points.
		if(usePlayerPoints) {
			//Adding draw points for each team.
			for(GamePlayer p : getPlayersWithMostPoints()) {
				drawPoints.put(p.getTeam(), drawPoints.get(p.getTeam()) + 1);
			}
		}
		//Checking team objectives.
		if(useTeamObjectives) {
			//Adding draw points for each team.
			Set<Entry<GameTeam, Integer>> teams = getFinishedObjectivesForTeams().entrySet();
			for(Entry<GameTeam, Integer> t : teams) {
				drawPoints.put(t.getKey(), drawPoints.get(t.getKey()) + t.getValue());
			}
		}
		//Checking player lives.
		if(usePlayerLives) {
			//Adding draw points for each team.
			for(GamePlayer p : getPlayersWithMostLives()) {
				drawPoints.put(p.getTeam(), drawPoints.get(p.getTeam()) + 1);
			}
		}
		//Returning the winning team.
		//Getting the best team by DrawPoints.
		Set<Entry<GameTeam, Integer>> teams = drawPoints.entrySet();
		ArrayList<GameTeam> bestTeams = new ArrayList<GameTeam>();
		//Getting 1st best team.
		Entry<GameTeam, Integer> bestTeam = teams.iterator().next();
		for(Entry<GameTeam, Integer> t : teams) {
			if(t.getValue() > bestTeam.getValue()) {
				bestTeam = t;
				continue;
			}
		}
		//We have a team with most points.
		bestTeams.add(bestTeam.getKey());
		//Checking if we have other teams with same results.
		for(Entry<GameTeam, Integer> t : teams) {
			if(t.getValue() == bestTeam.getValue()) {
				bestTeams.add(t.getKey());
				continue;
			}
		}
		if(bestTeam.getValue() == 0) {
			return null;
		}
	    // Creating LinkedHashSet
	     LinkedHashSet<GameTeam> lhs = new LinkedHashSet<GameTeam>();
	 
	     /* Adding ArrayList elements to the LinkedHashSet
	      * in order to remove the duplicate elements and 
	      * to preserve the insertion order.
	      */
	     lhs.addAll(bestTeams);
	  
	     // Removing ArrayList elements
	     bestTeams.clear();
	 
	     // Adding LinkedHashSet elements to the ArrayList
	     bestTeams.addAll(lhs);
		//Returning the best teams.
		return bestTeams;
	}
	public static ArrayList<GamePlayer> getWinningPlayers(ConfigurationPart drawSettings) {
		//Which stats should DrawManager use?
		if(drawSettings.getString("usedStats.teamPoints").equals("true") || drawSettings.getString("usedStats.teamPoints").equals("false")) {
			useTeamPoints = drawSettings.getBoolean("usedStats.teamPoints");	
		}
		else {
			useTeamPoints = true;
			desiredTeamPoints = drawSettings.getInt("usedStats.teamPoints");
		}
		useTeamObjectives = drawSettings.getBoolean("usedStats.teamObjectives");
		if(drawSettings.getString("usedStats.playerPoints").equals("true") || drawSettings.getString("usedStats.playerPoints").equals("false")) {
			usePlayerPoints = drawSettings.getBoolean("usedStats.playerPoints");	
		}
		else {
			usePlayerPoints = true;
			desiredPlayerPoints = drawSettings.getInt("usedStats.playerPoints");
		}
		if(drawSettings.getString("usedStats.playerLives").equals("true") || drawSettings.getString("usedStats.playerLives").equals("false")) {
			usePlayerLives = drawSettings.getBoolean("usedStats.playerLives");	
		}
		else {
			usePlayerLives = true;
			desiredPlayerLives = drawSettings.getInt("usedStats.playerLives");
		}
		//DrawPoints
		//Used to identify the winning team.
		HashMap<GamePlayer, Integer> drawPoints = new HashMap<GamePlayer, Integer>();
		//Adding players to drawPoints table.
		for(GamePlayer p : GamePlayer.gamePlayers) {
			drawPoints.put(p, 0);
		}
		//Checking team points.
		if(useTeamPoints) {
			//Adding draw points for each player from winning team.
			for(GameTeam t : getTeamsWithMostPoints()) {
				for(GamePlayer p : t.getMembers()) {
					drawPoints.put(p, drawPoints.get(p) + 1);	
				}
			}
		}
		//Checking player points.
		if(usePlayerPoints) {
			//Adding draw points for each player.
			for(GamePlayer p : getPlayersWithMostPoints()) {
				drawPoints.put(p, drawPoints.get(p) + 1);
			}
		}
		//Checking team objectives.
		if(useTeamObjectives) {
			//Adding draw points for each team.
			Set<Entry<GameTeam, Integer>> teams = getFinishedObjectivesForTeams().entrySet();
			for(Entry<GameTeam, Integer> t : teams) {
				for(GamePlayer p : t.getKey().getMembers()) {
					drawPoints.put(p, drawPoints.get(p) + t.getValue());	
				}
			}
		}
		//Checking player lives.
		if(usePlayerLives) {
			//Adding draw points for each team.
			for(GamePlayer p : getPlayersWithMostLives()) {
				drawPoints.put(p, drawPoints.get(p) + 1);
			}
		}
		//Returning the winning players.
		//Getting the best player by DrawPoints.
		Set<Entry<GamePlayer, Integer>> players = drawPoints.entrySet();
		ArrayList<GamePlayer> bestPlayers = new ArrayList<GamePlayer>();
		//Getting 1st best player.
		Entry<GamePlayer, Integer> bestPlayer = players.iterator().next();
		for(Entry<GamePlayer, Integer> p : players) {
			if(p.getValue() > bestPlayer.getValue()) {
				bestPlayer = p;
				continue;
			}
		}
		//We have a player with most points.
		bestPlayers.add(bestPlayer.getKey());
		//Checking if we have other teams with same results.
		for(Entry<GamePlayer, Integer> p : players) {
			if(p.getValue() == bestPlayer.getValue()) {
				bestPlayers.add(p.getKey());
				continue;
			}
		}
		if(bestPlayer.getValue() == 0) {
			return null;
		}
	    // Creating LinkedHashSet
	     LinkedHashSet<GamePlayer> lhs = new LinkedHashSet<GamePlayer>();
	 
	     /* Adding ArrayList elements to the LinkedHashSet
	      * in order to remove the duplicate elements and 
	      * to preserve the insertion order.
	      */
	     lhs.addAll(bestPlayers);
	  
	     // Removing ArrayList elements
	     bestPlayers.clear();
	 
	     // Adding LinkedHashSet elements to the ArrayList
	     bestPlayers.addAll(lhs);
		//Returning the best teams.
		return bestPlayers;
	}
	static ArrayList<GameTeam> getTeamsWithMostPoints() {
		ArrayList<GameTeam> bestTeams = new ArrayList<GameTeam>();
		if(desiredTeamPoints == 0) {
			//Getting 1st best team.
			GameTeam bestTeam = GameTeam.teams.get(0);
			for(GameTeam t : GameTeam.teams) {
				if(t.getPoints() > bestTeam.getPoints()) {
					bestTeam = t;
					continue;
				}
			}
			//We have a team with most points.
			bestTeams.add(bestTeam);
			//Checking if we have other teams with same results.
			for(GameTeam t : GameTeam.teams) {
				if(t.getPoints() == bestTeam.getPoints()) {
					bestTeams.add(t);
					continue;
				}
			}
			//Returning the best teams.
			return bestTeams;			
		}
		else {
			//Checking if teams have desired points.
			for(GameTeam t : GameTeam.teams) {
				if(t.getPoints() == desiredTeamPoints || t.getPoints() > desiredTeamPoints) {
					bestTeams.add(t);
					continue;
				}
			}
			return bestTeams;
		}
	}
	static HashMap<GameTeam, Integer> getFinishedObjectivesForTeams() {
		HashMap<GameTeam, Integer> teamsWithFinishedObjectives = new HashMap<GameTeam, Integer>();
		//Checking the if the objectives are finished.
		for(GameTeam t : GameTeam.teams) {
			teamsWithFinishedObjectives.put(t, 0);
			for(Objective obj : ObjectivesManager.objectives) {
				if(obj.getOwningTeam().equals(t)) {
					//This objective belongs to this team.
					if(obj.isFinishable()) {
						//This objective is simply finishable.
						if(obj.getSettings().getBoolean("reverseFinishable")) {
							if(!obj.isFinished()) {
								//Adding 1 finished objective to the team.
								teamsWithFinishedObjectives.put(t, teamsWithFinishedObjectives.get(t) + 1);
							}
						}
						else {
							if(obj.isFinished()) {
								//Adding 1 finished objective to the team.
								teamsWithFinishedObjectives.put(t, teamsWithFinishedObjectives.get(t) + 1);
							}
						}
					}
				}
			}
		}
		return teamsWithFinishedObjectives;
	}
	static ArrayList<GamePlayer> getPlayersWithMostPoints() {
		ArrayList<GamePlayer> bestPlayers = new ArrayList<GamePlayer>();
		if(desiredPlayerPoints == 0) {
			//Getting 1st best team.
			GamePlayer bestPlayer = GamePlayer.gamePlayers.get(0);
			for(GamePlayer p : GamePlayer.gamePlayers) {
				if(p.getPoints() > bestPlayer.getPoints()) {
					bestPlayer = p;
					continue;
				}
			}
			//We have a player with most points.
			bestPlayers.add(bestPlayer);
			//Checking if we have other players with same results.
			for(GamePlayer p : GamePlayer.gamePlayers) {
				if(p.getPoints() == bestPlayer.getPoints()) {
					bestPlayers.add(p);
					continue;
				}
			}
			//Returning the best teams.
			return bestPlayers;	
		} 
		else {
			//Checking if players have desired points.
			for(GamePlayer p : GamePlayer.gamePlayers) {
				if(p.getPoints() == desiredPlayerPoints || p.getPoints() > desiredPlayerPoints) {
					bestPlayers.add(p);
					continue;
				}
			}
			return bestPlayers;
		}
	}
	static ArrayList<GamePlayer> getPlayersWithMostLives() {
		ArrayList<GamePlayer> bestPlayers = new ArrayList<GamePlayer>();
		if(desiredPlayerLives == 0) {
			//Getting 1st best player.
			GamePlayer bestPlayer = GamePlayer.gamePlayers.get(0);
			for(GamePlayer p : GamePlayer.gamePlayers) {
				if(p.getLives() > bestPlayer.getLives()) {
					bestPlayer = p;
					continue;
				}
			}
			//We have a player with most points.
			bestPlayers.add(bestPlayer);
			//Checking if we have other players with same results.
			for(GamePlayer p : GamePlayer.gamePlayers) {
				if(p.getLives() == bestPlayer.getLives()) {
					bestPlayers.add(p);
					continue;
				}
			}
			//Returning the best teams.
			return bestPlayers;	
		}
		else {
			//Checking if players have desired lives.
			for(GamePlayer p : GamePlayer.gamePlayers) {
				if(p.getLives() == desiredPlayerLives) {
					bestPlayers.add(p);
					continue;
				}
			}
			return bestPlayers;
		}
	}
}
