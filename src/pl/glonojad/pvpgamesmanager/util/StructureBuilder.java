package pl.glonojad.pvpgamesmanager.util;

import java.io.IOException;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.objects.Area;
import pl.glonojad.pvpgamesmanager.objects.Schematic;

public class StructureBuilder {
	public static Block createFlag(Location origin, DyeColor flagColor) {
		Block flagBlock = origin.getBlock();
		flagBlock.setType(Material.STANDING_BANNER);
		Banner flag = (Banner) flagBlock.getState();
		flag.setBaseColor(flagColor);
		flag.update();
		Location glowstoneBlock = origin;
		glowstoneBlock.setY(glowstoneBlock.getY() - 1);
		glowstoneBlock.getBlock().setType(Material.GLOWSTONE);
		return flagBlock;
	}
	public static Area[] createCore(Location origin) {
		Location pasteTo = new Location(MapManager.currentMap, origin.getX() - 3, origin.getY() - 3, origin.getZ() - 3);
		Schematic core;
		//Loading the core schematic.
			try {
				core = SchematicLoader.loadSchematic(FileManager.getSchematic("Core.schematic"));
				SchematicLoader.pasteSchematic(MapManager.currentMap, pasteTo, core, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		//Points for coreArea.
		Location maxBlock = new Location(MapManager.currentMap, origin.getBlockX() + 3, origin.getBlockY() + 3, origin.getBlockZ() + 3);
		Location minBlock = new Location(MapManager.currentMap, origin.getBlockX() - 3, origin.getBlockY() - 3, origin.getBlockZ() - 3);
		//Points for leakArea.
		Location firstBlockLeakArea = new Location(MapManager.currentMap, minBlock.getBlockX(), minBlock.getBlockY() - 3, minBlock.getBlockZ());
		Location secondBlockLeakArea = new Location(MapManager.currentMap, minBlock.getBlockX() + 6, minBlock.getBlockY() - 3, minBlock.getBlockZ() + 6);
		//Areas.
		Area coreArea = new Area(minBlock, maxBlock);
		Area leakArea = new Area(firstBlockLeakArea, secondBlockLeakArea);
		Area[] areas = new Area[2];
		areas[0] = coreArea;
		areas[1] = leakArea;
		return areas;
	}
	public static Area createBomb(Location origin) {
		Location pasteTo = new Location(MapManager.currentMap, origin.getX() - 1, origin.getY() - 1, origin.getZ() - 1);
		Schematic bomb;
		//Loading the core schematic.
			try {
				bomb = SchematicLoader.loadSchematic(FileManager.getSchematic("Bomb.schematic"));
				SchematicLoader.pasteSchematic(MapManager.currentMap, pasteTo, bomb, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		//Points for bombArea.
		Location maxBlock = new Location(MapManager.currentMap, origin.getBlockX() + 1, origin.getBlockY() + 1, origin.getBlockZ() + 1);
		Location minBlock = new Location(MapManager.currentMap, origin.getBlockX() - 1, origin.getBlockY() - 1, origin.getBlockZ() - 1);
		return new Area(minBlock, maxBlock);
	}
	public static Area createCapturePoint(Location origin) {
		Location pasteTo = new Location(MapManager.currentMap, origin.getX() - 4, origin.getY() - 3, origin.getZ() - 4);
		Schematic capturePoint;
		//Loading the core schematic.
			try {
				capturePoint = SchematicLoader.loadSchematic(FileManager.getSchematic("CapturePoint.schematic"));
				SchematicLoader.pasteSchematic(MapManager.currentMap, pasteTo, capturePoint, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Points for captureArea.
			Location maxBlock = new Location(MapManager.currentMap, origin.getBlockX() + 3, origin.getBlockY() + 3, origin.getBlockZ() + 3);
			Location minBlock = new Location(MapManager.currentMap, origin.getBlockX() - 3, origin.getBlockY(), origin.getBlockZ() - 3);
			//Areas.
			Area captureArea = new Area(minBlock, maxBlock);
			return captureArea;
	}
}