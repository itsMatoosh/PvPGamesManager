package pl.glonojad.pvpgamesmanager.game.objective.Collapse;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class CollapseCauseArea {

	private Area area;
	@SuppressWarnings("unused")
	private String name;
	private ConfigurationPart settings;
	public ArrayList<Location> blocks = new ArrayList<Location>();
	private float destructionPercent;
	
	public CollapseCauseArea(String name, Area area, ConfigurationPart areaSettings) {
		this.area = area;
		this.settings = areaSettings;
		this.name = name;
		for(Block b : area) {
			if(!b.getType().equals(Material.AIR)) {
				blocks.add(b.getLocation());	
			}
		}
		this.destructionPercent = 0f;
	}	
	public Area getArea() {
		return area;
	}
	public float getDestructionPercent() {
		return destructionPercent;
	}
	public ConfigurationPart getSettings() {
		return settings;
	}
	public void setDestructionPercent(float newPercent) {
		destructionPercent = newPercent;
	}
}
