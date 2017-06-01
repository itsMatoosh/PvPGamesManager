package pl.glonojad.pvpgamesmanager.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.game.win.Winner;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.map.Spawn;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class GamePlayer implements Winner{
	
	public static ArrayList<GamePlayer> gamePlayers = new ArrayList<GamePlayer>();
	public static HashMap<GameTeam, ConfigurationPart> playerSettings = new HashMap<GameTeam, ConfigurationPart>();
	
	private Player vanillaPlayer;
	private String name;
	private String displayName;
	private boolean canJoinGame = true;
	private boolean canChooseKit = true;
	private boolean canBreakBlocks = true;
	private boolean canPlaceBlocks = true;
	private boolean invincible = false;
	private boolean canAttack = true;
	private int lives = -1;
	private int points = 0;
	
	//Stats
	private int overallKills = 0;
	private int overallDeaths = 0;
	private HashMap<String, String> upgradedGuns = new HashMap<String, String>();
	
	public GamePlayer(Player p) {
		this.vanillaPlayer = p;
		this.name = p.getName();
		this.displayName = p.getDisplayName();
		gamePlayers.add(this);
	}
	public static GamePlayer getGamePlayer(String name) {
		for(GamePlayer player : gamePlayers) {
			if(player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	public GameTeam getTeam() {
		return GameTeam.getPlayerTeam(this);
	}
	public Player getVanillaPlayer() {
		return vanillaPlayer;
	}
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public boolean canJoinGame() {
		return canJoinGame;
	}
	public int getLives() {
		return lives;
	}
	public int getPoints() {
		return points;
	}
	public int getLevel() {
		return this.vanillaPlayer.getLevel();
	}
	public int getOverallKills() {
		return overallKills;
	}
	public int getOverallDeaths() {
		return overallDeaths;
	}
	public HashMap<String, String> getUpgradedGuns() {
		return upgradedGuns;
	}
	public boolean canBreakBlocks() {
		return canBreakBlocks;
	}
	public boolean canPlaceBlocks() {
		return canPlaceBlocks;
	}
	public boolean canChooseKit() {
		return this.canChooseKit;
	}
	public boolean isInvincible() {
		return invincible;
	}
	public boolean canAttack() {
		return canAttack;
	}
	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}
	public void setVanillaPlayer(Player p) {
		this.vanillaPlayer = p;
	}
	public void setLives(int lives) {
		this.lives = lives;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public void setCanJoinGame(boolean canJoin) {
		this.canJoinGame = canJoin;
	}
	public void setCanChooseKit(boolean canChooseKit) {
		this.canChooseKit = canChooseKit;
	}
	public void setInvincible(boolean invincibility) {
		this.invincible = invincibility;
	}
	public void setCanBreakBlocks(boolean canBreakBlocks) {
		this.canBreakBlocks = canBreakBlocks;
	}
	public void setCanPlaceBlocks(boolean canPlaceBlocks) {
		this.canPlaceBlocks = canPlaceBlocks;
	}
	public void setOverallKills(int overallKills) {
		this.overallKills = overallKills;
	}
	public void setOverallDeaths(int overallDeaths) {
		this.overallDeaths = overallDeaths;
	}
	public void setUpgradedGuns(HashMap<String, String> upgradedGuns) {
		this.upgradedGuns = upgradedGuns;
	}
	public void addPoints(int points) {
		this.points = this.points + points;
		ChatManager.log("Player " + this.name + " gained " + points + " points!");
		return;
	}
	public void kick (String reason) {
		vanillaPlayer.kickPlayer(reason);
		StatsManager.savePlayerStatsToDisk(this);
	}
	public void spawnPlayer() {
		//Teleporting player to spawn.
		//Searching for spawn.
		boolean found = false;
		Spawn foundSpawn = null;
		while (!found) {
			Spawn s = this.getTeam().getRandomSpawn();
			if(s.maxPlayers != 0) {
				int playersInRange = 0;
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getLocation().toVector().normalize().equals(s.location.toVector().normalize())) {
						playersInRange++;
					}
				}
				if(playersInRange < s.maxPlayers) {
					found = true;
					foundSpawn = s;
				}
			}
			else {
				found = true;
				foundSpawn = s;
			}
		}
		getVanillaPlayer().teleport(foundSpawn.location);
		//Setting player gamemode.
		if(GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(this)).getString("gamemode").equals("SURVIVAL")) {
			this.getVanillaPlayer().setGameMode(GameMode.SURVIVAL);	
		}
		if(GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(this)).getString("gamemode").equals("CREATIVE")) {
			this.getVanillaPlayer().setGameMode(GameMode.CREATIVE);	
		}
		if(GamePlayer.playerSettings.get(GameTeam.getPlayerTeam(this)).getString("gamemode").equals("ADVENTURE")) {
			this.getVanillaPlayer().setGameMode(GameMode.ADVENTURE);	
		}
		//Healing and feeding the player.
		getVanillaPlayer().setHealth(20);
		getVanillaPlayer().setFoodLevel(20);
		//Opening kit selection menu.
		this.setCanChooseKit(true);
		KitManager.openKitMenu(this);
		ChatManager.sendMessageToPlayer(this, PvPGamesManager.language.get(this.getVanillaPlayer(),"RESPAWN_Just-Respawned",ChatColor.AQUA + "Respawn!"));
	}
	/** Resets inventory of the GamePlayer. */
	public void resetInventory() {
		//Clearing the inventory.
		vanillaPlayer.getInventory().clear();
		ItemStack empty = new ItemStack(Material.AIR);
		vanillaPlayer.getInventory().setHelmet(empty);
		vanillaPlayer.getInventory().setChestplate(empty);
		vanillaPlayer.getInventory().setLeggings(empty);
		vanillaPlayer.getInventory().setBoots(empty);
	}
	public static void refreshByPlayer(Player p) {
		//Searching for a suitable GamePlayer.
		for(GamePlayer gp : gamePlayers) {
			if(gp.getVanillaPlayer().getName().equals(p.getName())) {
				gp.setVanillaPlayer(p);
				return;
			}
		}
	}
}
