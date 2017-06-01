package pl.glonojad.pvpgamesmanager.threads.sidebar;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.ObjectiveType;

/**
 * Manages the sidebar.
 * TODO: Clean up the code.
 * @author Mateusz Rebacz
 *
 */
public class SidebarManager implements Runnable{
	/** Scores page of the sidebar. */
	public static Objective Scores;
	/** Objectives page of the sidebar. */
	public static Objective Objectives;
	/** Last page shown on the sidebar. */
	private static SidebarPage lastPage = SidebarPage.NONE;
	
	private static HashMap<String, Integer> lastNumbers = new HashMap<String,Integer>();
	private static int lastTeamlessObjectiveNumber = 1000;
	private static int lastTeamInObjectivesNumber = 50;
	private static HashMap<String, Score> objectiveScores = new HashMap<String,Score>();
	private static HashMap<String, ObjectiveType> objectiveTypes = new HashMap<String,ObjectiveType>();
	private static HashMap<String, String> objectiveTeams = new HashMap<String,String>();
	private static HashMap<String, String> objectiveStates = new HashMap<String,String>();
	
	/** Initializes the sidebar manager. */
	public static void initialize() {
		//Registering scores and objectives labels.
		Scores = GameManager.mainScoreboard.registerNewObjective("§e§lPoints", "dummy");
		Objectives = GameManager.mainScoreboard.registerNewObjective("§a§lObjectives", "dummy");
	}
	/** Sidebar page changing task. */
	public void run() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PvPGamesManager.instance, new Runnable() 
		{
			//Changing the sidebar pages.
			public void run() {
				switch(lastPage) 
				{
					case NONE:
						//Enabling objectives.
						Scores.setDisplaySlot(DisplaySlot.SIDEBAR);
						lastPage = SidebarPage.SCORES;
						return;
					case OBJECTIVES:
						//Enabling scores.
						Objectives.setDisplaySlot(null);
						Scores.setDisplaySlot(DisplaySlot.SIDEBAR);
						lastPage = SidebarPage.SCORES;
						return;
					case SCORES:
						//Enabling objectives.
						Scores.setDisplaySlot(null);
						Objectives.setDisplaySlot(DisplaySlot.SIDEBAR);
						lastPage = SidebarPage.OBJECTIVES;
						return;
					default:
						//Enabling objectives.
						Scores.setDisplaySlot(DisplaySlot.SIDEBAR);
						lastPage = SidebarPage.SCORES;
						return;
				}
			}
		}, 10, 140);
	}
	/** Adds a specified objective to the sidebar. */
	public static void addObjective(final pl.glonojad.pvpgamesmanager.game.objective.Objective toAdd, final GameTeam team) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(PvPGamesManager.instance, new Runnable() {
            public void run() {
        		//This objective does belong to a team.
            	Score objective = Objectives.getScore(" " + toAdd.getName());
            	if(toAdd.getType().equals(ObjectiveType.FLAG)) {
            		objective = Objectives.getScore(ChatColor.GOLD + " ▲ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.FLAG);
            	}
            	if(toAdd.getType().equals(ObjectiveType.CORE)) {
            		objective = Objectives.getScore(ChatColor.GREEN + " ✔ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.CORE);
            	}
            	if(toAdd.getType().equals(ObjectiveType.DESTRUCTION)) {
            		objective = Objectives.getScore(ChatColor.RED + " 0% " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.DESTRUCTION);
            	}
            	if(toAdd.getType().equals(ObjectiveType.CAPTURE_POINT)) {
            		objective = Objectives.getScore(ChatColor.AQUA + " Ⓐ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.CAPTURE_POINT);
            	}
            	if(toAdd.getType().equals(ObjectiveType.SCOREBOX)) {
            		objective = Objectives.getScore(ChatColor.YELLOW + " ▼ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.SCOREBOX);
            	}
            	if(toAdd.getType().equals(ObjectiveType.COLLAPSE)) {
            		objective = Objectives.getScore(ChatColor.GREEN + " ✔ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.COLLAPSE);
            	}
            	if(toAdd.getType().equals(ObjectiveType.BOMB)) {
            		objective = Objectives.getScore(ChatColor.GREEN + " ✔ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.BOMB);
            	}
        		objective.setScore(lastNumbers.get(team.getName()) - 1);
            	objectiveScores.put(toAdd.getName(), objective);
            	objectiveTeams.put(toAdd.getName(), team.getName());
        		lastNumbers.put(team.getName(), lastNumbers.get(team.getName()) - 1);
            }
        }, 5);
	}
	/** Adds a specified objective to the sidebar. */
	public static void addObjective(final pl.glonojad.pvpgamesmanager.game.objective.Objective toAdd) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(PvPGamesManager.instance, new Runnable() {
            public void run() {
        		//This objective has no team.
        		Score objective = Objectives.getScore(toAdd.getName());
            	if(toAdd.getType().equals(ObjectiveType.FLAG)) {
            		objective = Objectives.getScore(ChatColor.GOLD + "▲ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.FLAG);
            	}
            	if(toAdd.getType().equals(ObjectiveType.CORE)) {
            		objective = Objectives.getScore(ChatColor.GREEN + "✔ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.CORE);
            	}
            	if(toAdd.getType().equals(ObjectiveType.DESTRUCTION)) {
            		objective = Objectives.getScore(ChatColor.RED + "0% " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.DESTRUCTION);
            	}
            	if(toAdd.getType().equals(ObjectiveType.CAPTURE_POINT)) {
            		objective = Objectives.getScore(ChatColor.AQUA + "Ⓐ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.CAPTURE_POINT);
            	}
            	if(toAdd.getType().equals(ObjectiveType.SCOREBOX)) {
            		objective = Objectives.getScore(ChatColor.YELLOW + "▼ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.SCOREBOX);
            	}
            	if(toAdd.getType().equals(ObjectiveType.COLLAPSE)) {
            		objective = Objectives.getScore(ChatColor.GREEN + " ✔ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.COLLAPSE);
            	}
            	if(toAdd.getType().equals(ObjectiveType.BOMB)) {
            		objective = Objectives.getScore(ChatColor.GREEN + " ✔ " + toAdd.getName());
                	objectiveTypes.put(toAdd.getName(), ObjectiveType.BOMB);
            	}
        		objective.setScore(lastTeamlessObjectiveNumber++);
            	objectiveScores.put(toAdd.getName(), objective);
        		lastTeamlessObjectiveNumber--;
            }
        }, 5);
	}
	@SuppressWarnings("deprecation")
	public static void setObjectiveState(pl.glonojad.pvpgamesmanager.game.objective.Objective obj , String state) {
		if(objectiveScores.containsKey(obj.getName()) && objectiveTypes.containsKey(obj.getName()) && objectiveStates.get(obj.getName()) != state) {
			//Setting objective state.
			ObjectiveType objectiveType = objectiveTypes.get(obj.getName());
			Score objectiveScore = objectiveScores.get(obj.getName());
			Score newObjective = null;
			if(objectiveType.equals(ObjectiveType.CORE)) {
				if(objectiveTeams.containsKey(obj.getName())) {
					//This core belongs to a team.
					if(state.equals("Untouched")) {
						//Setting state of the objective to Untouched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.GREEN + " ✔ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Touched")) {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.YELLOW + " ☼ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Leaked")) {
						//Setting state of the objective to Leaked.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.RED + " ✘ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
				else {
					//This core doesn't have a team.
					if(state.equals("Untouched")) {
						//Setting state of the objective to Untouched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.GREEN + "✔ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Touched")) {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.YELLOW + "☼ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Leaked")) {
						//Setting state of the objective to Leaked.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.RED + "✘ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
			}
			if(objectiveType.equals(ObjectiveType.DESTRUCTION)) {
				if(objectiveTeams.containsKey(obj.getName())) {
					//This destruction objective belongs to a team.
					
					//Setting state of the objective to Untouched.
					int lastPoints = objectiveScore.getScore();
					GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
					newObjective = Objectives.getScore(ChatColor.GREEN + " " + state + "% " + obj.getName());
					newObjective.setScore(lastPoints);
					
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
				else {
					//This core doesn't have a team.
					//Setting state of the objective to Untouched.
					int lastPoints = objectiveScore.getScore();
					GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
					newObjective = Objectives.getScore(ChatColor.GREEN + "" + state + "% " + obj.getName());
					newObjective.setScore(lastPoints);
						
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
			}
			if(objectiveType.equals(ObjectiveType.COLLAPSE)) {
				if(objectiveTeams.containsKey(obj.getName())) {
					//This collapse belongs to a team.
					if(state.equals("Uncollapsed")) {
						//Setting state of the objective to Untouched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.GREEN + " ✔ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Collapsed")) {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.RED + " ✖ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
				else {
					//This core doesn't have a team.
					if(state.equals("Uncollapsed")) {
						//Setting state of the objective to Untouched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.GREEN + "✔ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Collapsed")) {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.RED + "✖ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
			}
			if(objectiveType.equals(ObjectiveType.BOMB)) {
				if(objectiveTeams.containsKey(obj.getName())) {
					//This bomb belongs to a team.
					if(state.equals("Untouched")) {
						//Setting state of the objective to Untouched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.GREEN + " ✔ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Exploded")) {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.RED + " ✖ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					else {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.YELLOW + " " + state + "⌚ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
				else {
					//This core doesn't have a team.
					if(state.equals("Untouched")) {
						//Setting state of the objective to Untouched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.GREEN + "✔ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					if(state.equals("Exploded")) {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.RED + "✖ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					else {
						//Setting state of the objective to Touched.
						int lastPoints = objectiveScore.getScore();
						GameManager.mainScoreboard.resetScores(objectiveScore.getPlayer());
						newObjective = Objectives.getScore(ChatColor.YELLOW + " " + state + "⌚ " + obj.getName());
						newObjective.setScore(lastPoints);
					}
					objectiveScores.put(obj.getName(), newObjective);
					objectiveStates.put(obj.getName(), state);
					return;
				}
			}
		}
	}
	public static void setPoints(String teamName, int Score) {
		String teamDisplayName = GameTeam.getTeam(teamName).getVanillaTeam().getDisplayName();
		Scores.getScore(teamDisplayName).setScore(Score);
	}
	public static int getPoints(String teamDisplayName) {
		return Scores.getScore(teamDisplayName).getScore();
	}
	public static void addTeam(String teamName, String teamDisplayName) {
		//Adding team to scores.
		Score teamInScores = Scores.getScore(teamDisplayName);
		teamInScores.setScore(0);
		//Adding team to objectives.
		Score teamInObjectives = Objectives.getScore(teamDisplayName);
		lastTeamInObjectivesNumber = lastTeamInObjectivesNumber - 50;
		teamInObjectives.setScore(lastTeamInObjectivesNumber);
		lastNumbers.put(teamName, lastTeamInObjectivesNumber);
	}
}
