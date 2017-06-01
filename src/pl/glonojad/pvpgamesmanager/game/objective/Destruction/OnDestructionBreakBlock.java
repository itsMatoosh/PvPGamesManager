package pl.glonojad.pvpgamesmanager.game.objective.Destruction;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.threads.sidebar.SidebarManager;

public class OnDestructionBreakBlock implements Runnable {
	
	public Destruction destruction;
	public float totalBlocksBrokenInDestructionArea = 0;
	
	public OnDestructionBreakBlock (Destruction destruction, float totalBlocksBrokenInDestructionArea, Location location) {
		this.destruction = destruction;
		this.totalBlocksBrokenInDestructionArea = totalBlocksBrokenInDestructionArea;
	}
	public OnDestructionBreakBlock (Destruction destruction, ArrayList<Location> brokenBlocks) {
		this.destruction = destruction;
		this.totalBlocksBrokenInDestructionArea = brokenBlocks.size();
	}
	public void run() {
		//Settings the destruction percent.
		//Adding the current destruction percent to existing percents,
		destruction.destructionPercent = destruction.destructionPercent + (totalBlocksBrokenInDestructionArea * 100) / destruction.totalBlocks;
		//Updating the percentage in the sidebar.
		int displayPercentage = (int) destruction.destructionPercent;
		SidebarManager.setObjectiveState(destruction, displayPercentage + "");
		//Removing block from blocks list.
		
		//Running all configured actions.
		if(destruction.settings.getString("destructionMode").equals("BREAK")) {
			//Firing all configured actions.
			if(destruction.settings.isList("actions.onBreak")) {
				for (Object key : destruction.settings.getList("actions.onBreak")) {
					//Firing each action.
					//Adding the arguments to the action.
					HashMap<String, Object> arguments=new HashMap<String, Object>();
					if(destruction.lastPlayerBroken == null) {
						//Adding TEAM_BROKE.
						arguments.put("TEAM_SOURCE", null);
					}
					else {
						//Adding TEAM_BROKE.
						arguments.put("TEAM_SOURCE", GamePlayer.getGamePlayer(destruction.lastPlayerBroken).getTeam().getName());
					}
					//Adding TEAM_VICTIM.
					arguments.put("TEAM_VICTIM", destruction.owningTeam.getName());
					//Adding PLAYER_BROKE.
					arguments.put("PLAYER_SOURCE", destruction.lastPlayerBroken);
					//Adding DESTRUCTION_NAME
					arguments.put("OBJECTIVE_NAME", destruction.name);
					ActionsManager.trigger((String) key, arguments);
				}
			}
		}
		if(destruction.settings.getString("destructionMode").equals("REACH_PERCENT")) {
			//Firing all configured actions.
			if(destruction.settings.isList("actions.onReachPercent")) {
				int destructionPercentToConfig = (int) destruction.destructionPercent;
				
				if(destructionPercentToConfig == destruction.settings.getInt("requiredPercent") || destructionPercentToConfig > destruction.settings.getInt("requiredPercent")) {
					//Firing all ONREACHPERCENT.
					for (Object key : destruction.settings.getList("actions.onReachPercent")) {
						//Firing each action.
						//Adding the arguments to the action.
						HashMap<String, Object> arguments=new HashMap<String, Object>();
						if(destruction.lastPlayerBroken == null) {
							//Adding TEAM_BROKE.
							arguments.put("TEAM_SOURCE", null);
						}
						else {
							//Adding TEAM_BROKE.
							arguments.put("TEAM_SOURCE", GamePlayer.getGamePlayer(destruction.lastPlayerBroken).getTeam().getName());
						}
						//Adding TEAM_VICTIM.
						arguments.put("TEAM_VICTIM", destruction.owningTeam.getName()); //There is no TEAM_VICTIM.
						//Adding PLAYER_GRABBED.
						arguments.put("PLAYER_SOURCE", destruction.lastPlayerBroken);
						//Adding FLAG_NAME
						arguments.put("OBJECTIVE_NAME", destruction.name);
						destruction.isDestructed = true;
						HandlerList.unregisterAll(destruction);
						ActionsManager.trigger((String) key, arguments);
					}
				}
			}
			//Firing all configured actions.
			if(destruction.settings.isList("actions.onBreak")) {
				for (Object key : destruction.settings.getList("actions.onBreak")) {
					//Firing each action.
					//Adding the arguments to the action.
					HashMap<String, Object> arguments=new HashMap<String, Object>();
					if(destruction.lastPlayerBroken == null) {
						//Adding TEAM_BROKE.
						arguments.put("TEAM_SOURCE", null);
					}
					else {
						//Adding TEAM_BROKE.
						arguments.put("TEAM_SOURCE", GamePlayer.getGamePlayer(destruction.lastPlayerBroken).getTeam().getName());
					}
					//Adding TEAM_VICTIM.
					arguments.put("TEAM_VICTIM", destruction.owningTeam.getName());
					//Adding PLAYER_BROKE.
					arguments.put("PLAYER_SOURCE", destruction.lastPlayerBroken);
					//Adding DESTRUCTION_NAME
					arguments.put("OBJECTIVE_NAME", destruction.name);
					ActionsManager.trigger((String) key, arguments);
				}
			}
		}
	}
}
