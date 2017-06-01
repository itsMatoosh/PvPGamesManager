package pl.glonojad.pvpgamesmanager.game.actions;

import org.bukkit.Bukkit;

import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;

public class Checker {
	public boolean check(String condition, String value) {
		if(condition.equals("ONLINE_PLAYERS")) {
			if(checkOnlinePlayers(Integer.parseInt(value))) {
				return true;
			}
			return false;
		}
		if(condition.equals("DISQUALIFIED_PLAYERS")) {
			if(checkDisqualifiedPlayers(Integer.parseInt(value))) {
				return true;
			}
			return false;
		}
		if(condition.equals("UNDISQUALIFIED_PLAYERS")) {
			if(checkUnDisqualifiedPlayers(Integer.parseInt(value))) {
				return true;
			}
			return false;
		}
		if(condition.contains("PLAYER_COUNT_IN_TEAM:")) {
			String[] team = condition.split(":");
			if(checkPlayerCountInTeam(team[1], Integer.parseInt(value))) {
				return true;
			}
			return false;
		}
		if(condition.contains("PLAYER_IN_TEAM:")) {
			String[] player = condition.split(":");
			if(checkPlayerInTeam(player[1], value)) {
				return true;
			}
			return false;
		}
		return false;
	}
	private boolean checkOnlinePlayers(int amount) {
		if(Bukkit.getOnlinePlayers().size() == amount) {
			return true;
		} else {
			return false;
		}
	}
	private boolean checkDisqualifiedPlayers(int amount) {
		int disqualified = 0;
		for(GamePlayer p : GamePlayer.gamePlayers) {
			if(p.getLives() < 0 || p.getLives() == 0) {
				disqualified++;
			}
		}
		if(disqualified == amount) {
			return true;
		}
		return false;
	}
	private boolean checkUnDisqualifiedPlayers(int amount) {
		int undisqualified = 0;
		for(GamePlayer p : GamePlayer.gamePlayers) {
			if(p.getLives() > 0) {
				undisqualified++;
			}
		}
		if(undisqualified == amount) {
			return true;
		}
		return false;
	}
	private boolean checkPlayerCountInTeam(String teamName, int amount) {
		if(GameTeam.getTeam(teamName).getMembers().size() == amount) {
			return true;
		} else {
			return false;	
		}
	}
	private boolean checkPlayerInTeam(String teamName, String playerName) {
		if(GameTeam.getTeam(teamName).getMembers().contains(GamePlayer.getGamePlayer(playerName))) {
			return true;
		} else {
			return false;	
		}
	}
}
