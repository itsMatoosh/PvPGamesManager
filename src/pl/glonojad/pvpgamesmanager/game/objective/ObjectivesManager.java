package pl.glonojad.pvpgamesmanager.game.objective;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.objective.Collapse.Collapse;
import pl.glonojad.pvpgamesmanager.game.objective.Destruction.Destruction;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class ObjectivesManager{
	public static ArrayList<Flag> flags = new ArrayList<Flag>();
	public static ArrayList<Objective> objectives = new ArrayList<Objective>();
	//Methods for creating objectives.
	@SuppressWarnings("deprecation")
	public static void createObjective(String objective, ConfigurationPart objectiveSettings) {
		if(objectiveSettings.getString("type").equalsIgnoreCase("Flag")) {
			//Spawning a flag.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This flag belongs to a team.
				Flag flagTemp = new Flag(objective, new Location(MapManager.currentMap, 
						objectiveSettings.getDouble("location.x"), //X location of the flag.
						objectiveSettings.getDouble("location.y"), //Y location of the flag.
						objectiveSettings.getDouble("location.z")),//Z location of the flag. 
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")), 
						objectiveSettings, 
						DyeColor.getByDyeData(((byte)objectiveSettings.getInt("flagColor"))));
				flags.add(flagTemp);
				objectives.add(flagTemp);
				Bukkit.getPluginManager().registerEvents(flagTemp, PvPGamesManager.instance);
			}
			else {
				//This flag doesn't belong to a team.
				Flag flagTemp = new Flag(objective, new Location(MapManager.currentMap, 
						objectiveSettings.getDouble("location.x"), //X location of the flag.
						objectiveSettings.getDouble("location.y"), //Y location of the flag.
						objectiveSettings.getDouble("location.z")),//Z location of the flag. 
						null, 
						objectiveSettings, 
						DyeColor.getByDyeData((byte)objectiveSettings.getInt("flagColor")));
				flags.add(flagTemp);
				objectives.add(flagTemp);
				Bukkit.getPluginManager().registerEvents(flagTemp, PvPGamesManager.instance);
			}
		}
		if(objectiveSettings.getString("type").equalsIgnoreCase("Core")) {
			//Spawning a core.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This core belongs to a team.				
				Core coreTemp = new Core(objective, new Location(MapManager.currentMap, 
						objectiveSettings.getDouble("location.x"), //X location of the flag.
						objectiveSettings.getDouble("location.y"), //Y location of the flag.
						objectiveSettings.getDouble("location.z")),//Z location of the flag.
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")), 
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(coreTemp, PvPGamesManager.instance);
				objectives.add(coreTemp);
			}
			else {
				//This core doesn't belong to a team.
				Core coreTemp = new Core(objective, new Location(MapManager.currentMap, 
						objectiveSettings.getDouble("location.x"), //X location of the flag.
						objectiveSettings.getDouble("location.y"), //Y location of the flag.
						objectiveSettings.getDouble("location.z")),//Z location of the flag. 
						null, 
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(coreTemp, PvPGamesManager.instance);
				objectives.add(coreTemp);
			}
		}
		if(objectiveSettings.getString("type").equalsIgnoreCase("Destruction")) {
			//Spawning a core.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This destruction area belongs to a team.
				Destruction destructionTemp =  new Destruction(objective, 
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")),
						new Area(
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.minLocation.x"), //X location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.z")),//Z location of the minLocation.
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.maxLocation.x"), //X location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.y"), //Y location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.z"))),//Z location of the maxLocation.
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(destructionTemp, PvPGamesManager.instance);
				objectives.add(destructionTemp);
			}
			else {
				//This destruction area doesn't belong to a team.
				Destruction destructionTemp =  new Destruction(objective, 
						null, 
						new Area(
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.minLocation.x"), //X location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.z")),//Z location of the minLocation.
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.maxLocation.x"), //X location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.y"), //Y location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.z"))),//Z location of the maxLocation.
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(destructionTemp, PvPGamesManager.instance);
				objectives.add(destructionTemp);
			}
		}
		if(objectiveSettings.getString("type").equalsIgnoreCase("CapturePoint")) {
			//Spawning a CapturePoint.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This CapturePoint belongs to a team.
				CapturePoint capturePointTemp = new CapturePoint(objective, 
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")),
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("location.x"), //X location of the minLocation.
										objectiveSettings.getDouble("location.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("location.z")),//Z location of the minLocation.
						objectiveSettings,
						objectiveSettings.getInt("captureTime"));
				Bukkit.getPluginManager().registerEvents(capturePointTemp, PvPGamesManager.instance);
				objectives.add(capturePointTemp);
			}
			else {
				//This CapturePoint doesn't belong to a team.
				CapturePoint capturePointTemp = new CapturePoint(objective, 
								null, 
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("location.x"), //X location of the minLocation.
										objectiveSettings.getDouble("location.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("location.z")),//Z location of the minLocation.
						objectiveSettings,
						objectiveSettings.getInt("captureTime"));
				Bukkit.getPluginManager().registerEvents(capturePointTemp, PvPGamesManager.instance);
				objectives.add(capturePointTemp);
			}
		}
		if(objectiveSettings.getString("type").equalsIgnoreCase("ScoreBox")) {
			//Spawning a core.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This scorebox belongs to a team.
				ScoreBox scoreboxTemp = new ScoreBox(objective, 
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")),
						new Area(
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.minLocation.x"), //X location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.z")),//Z location of the minLocation.
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.maxLocation.x"), //X location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.y"), //Y location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.z"))),//Z location of the maxLocation.
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(scoreboxTemp, PvPGamesManager.instance);
				objectives.add(scoreboxTemp);
			}
			else {
				//This scorebox doesn't belong to a team.
				ScoreBox scoreboxTemp = new ScoreBox(objective, 
						null, 
						new Area(
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.minLocation.x"), //X location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("area.minLocation.z")),//Z location of the minLocation.
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("area.maxLocation.x"), //X location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.y"), //Y location of the maxLocation.
										objectiveSettings.getDouble("area.maxLocation.z"))),//Z location of the maxLocation.
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(scoreboxTemp, PvPGamesManager.instance);
				objectives.add(scoreboxTemp);
			}
		}
		if(objectiveSettings.getString("type").equalsIgnoreCase("Collapse")) {
			//Spawning a core.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This scorebox belongs to a team.
				Collapse collapseTemp = new Collapse(objective,
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")),
						new Area(
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("collapeArea.minLocation.x"), //X location of the minLocation.
										objectiveSettings.getDouble("collapeArea.minLocation.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("collapeArea.minLocation.z")),//Z location of the minLocation.
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("collapeArea.maxLocation.x"), //X location of the maxLocation.
										objectiveSettings.getDouble("collapeArea.maxLocation.y"), //Y location of the maxLocation.
										objectiveSettings.getDouble("collapeArea.maxLocation.z"))),//Z location of the maxLocation.
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(collapseTemp, PvPGamesManager.instance);
				objectives.add(collapseTemp);
			}
			else {
				//This scorebox doesn't belong to a team.
				Collapse collapseTemp = new Collapse(objective,
						null, 
						new Area(
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("collapeArea.minLocation.x"), //X location of the minLocation.
										objectiveSettings.getDouble("collapeArea.minLocation.y"), //Y location of the minLocation.
										objectiveSettings.getDouble("collapeArea.minLocation.z")),//Z location of the minLocation.
								new Location(MapManager.currentMap, 
										objectiveSettings.getDouble("collapeArea.maxLocation.x"), //X location of the maxLocation.
										objectiveSettings.getDouble("collapeArea.maxLocation.y"), //Y location of the maxLocation.
										objectiveSettings.getDouble("collapeArea.maxLocation.z"))),//Z location of the maxLocation.
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(collapseTemp, PvPGamesManager.instance);
				objectives.add(collapseTemp);
			}
		}
		if(objectiveSettings.getString("type").equalsIgnoreCase("Bomb")) {
			//Spawning a core.
			if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
				//This bomb belongs to a team.				
				Bomb bombTemp = new Bomb(objective, new Location(MapManager.currentMap, 
						objectiveSettings.getDouble("location.x"), //X location of the flag.
						objectiveSettings.getDouble("location.y"), //Y location of the flag.
						objectiveSettings.getDouble("location.z")),//Z location of the flag.
						GameTeam.getTeam(objectiveSettings.getString("owningTeam")), 
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(bombTemp, PvPGamesManager.instance);
				objectives.add(bombTemp);
			}
			else {
				//This bomb belongs to a team.				
				Bomb bombTemp = new Bomb(objective, new Location(MapManager.currentMap, 
						objectiveSettings.getDouble("location.x"), //X location of the flag.
						objectiveSettings.getDouble("location.y"), //Y location of the flag.
						objectiveSettings.getDouble("location.z")),//Z location of the flag.
						null, 
						objectiveSettings);
				Bukkit.getPluginManager().registerEvents(bombTemp, PvPGamesManager.instance);
				objectives.add(bombTemp);
			}
		}
		//Logging the successful objective registration.
		if(objectiveSettings.isSet("owningTeam") && !objectiveSettings.getString("owningTeam").equals("")) {
			ChatManager.log("Successfully registered objective " + objective + " for team " + objectiveSettings.getString("owningTeam"));
		}
		else {
			ChatManager.log("Successfully registered objective " + objective);
		}
	}
	public static Objective getObjective (String objective) {
		for(Objective obj : objectives) {
			if(obj.getName().equals(objective)) {
				return obj;
			}
		}
		return null;
	}
	public static boolean finishedObjective(GameTeam t, String objective) {
    		//Checking if all the specified objectives are finished.
    		Objective toCheck = ObjectivesManager.getObjective(objective);
    		if(toCheck instanceof Collapse) {
    			//This objective can be finished.
    			Collapse col = (Collapse) toCheck;
    			if(col.getOwningTeam().equals(t)) {
    				//This objective belongs to this team.
    				if(col.isFinished()) {
    					return true;
    				}
    				else {
    					return false;
    				}
    			}
    		}
    		if(toCheck instanceof Core) {
    			//This objective can be finished.
    			Core col = (Core) toCheck;
    			if(col.getOwningTeam().equals(t)) {
    				//This objective belongs to this team.
    				if(col.isFinished()) {
    					return true;
    				}
    				else {
    					return false;
    				}
    			}
    		}
    		if(toCheck instanceof Destruction) {
    			//This objective can be finished.
    			Destruction col = (Destruction) toCheck;
    			if(col.getOwningTeam().equals(t)) {
    				//This objective belongs to this team.
    				if(col.isFinished()) {
    					return true;
    				}
    				else {
    					return false;
    				}
    			}
    		}	
		return false;
	}
}
