package pl.glonojad.pvpgamesmanager.game.objective.Collapse;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import pl.glonojad.pvpgamesmanager.game.actions.ActionsManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;

public class OnCollapseBreakBlock implements Runnable{
	
	public Collapse collapse;
	public CollapseCauseArea causeArea;
	public ArrayList<Location> blocksBroken = new ArrayList<Location>();
	
	public OnCollapseBreakBlock (Collapse collapse, Location brokenBlock, CollapseCauseArea causeArea) {
		this.collapse = collapse;
		this.blocksBroken.add(brokenBlock);
		this.causeArea = causeArea;
	}
	public OnCollapseBreakBlock (Collapse collapse, ArrayList<Location> brokenBlocks, CollapseCauseArea causeArea) {
		this.collapse = collapse;
		this.blocksBroken.addAll(brokenBlocks);
		this.causeArea = causeArea;
	}
	public void run() {
		//Setting the new destruction percentage.
		causeArea.setDestructionPercent(causeArea.getDestructionPercent() + (blocksBroken.size() * 100) / causeArea.blocks.size());
		//Removing block from blocks list.
		causeArea.blocks.removeAll(blocksBroken);
		//Checking if the structure should collapse.
		if(causeArea.getSettings().getInt("requiredPercent") < causeArea.getDestructionPercent()) {
			//Begining the collapse.
			collapse.BeginCollapse(new Vector(
					causeArea.getSettings().getDouble("collapseVector.x"),
					causeArea.getSettings().getDouble("collapseVector.y"),
					causeArea.getSettings().getDouble("collapseVector.z")));
			//Running all configured actions.
			if(collapse.settings.getString("collapseMode").equals("COLLAPSE")) {
				//Firing all configured actions.
				if(collapse.settings.isList("actions.onCollapse")) {
					for (Object key : collapse.settings.getList("actions.onCollapse")) {
						//Firing each action.
						//Adding the arguments to the action.
						HashMap<String, Object> arguments=new HashMap<String, Object>();
						if(collapse.lastPlayerBroken == null) {
							//Adding TEAM_BROKE.
							arguments.put("TEAM_SOURCE", null);
						}
						else {
							//Adding TEAM_BROKE.
							arguments.put("TEAM_SOURCE", GamePlayer.getGamePlayer(collapse.lastPlayerBroken).getTeam().getName());
						}
						//Adding TEAM_VICTIM.
						arguments.put("TEAM_VICTIM", collapse.owningTeam.getName());
						//Adding PLAYER_BROKE.
						arguments.put("PLAYER_SOURCE", collapse.lastPlayerBroken);
						//Adding collapse_NAME
						arguments.put("OBJECTIVE_NAME", collapse.name);
						ActionsManager.trigger((String) key, arguments);
					}
				}
			}	
		}
	}
}
