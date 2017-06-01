package pl.glonojad.pvpgamesmanager.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.configuration.Configuration;

import com.shampaggon.crackshot.CSUtility;

public class WeaponsManager {
	
	public static CSUtility crackshot;
	public static Boolean gunsMode;
	public static Configuration gunsUpgrades;
	
	public static void enableGuns() {
		//Disabling actual guns' plugin.
		Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin("CrackShot"));
		crackshot = new CSUtility();
		gunsMode = true;
		initialize();
	}
	public static void initialize() {
		gunsUpgrades = FileManager.getGunsUpgrades();
		
	}
	public static void disableGuns() {
		//Enabling actual guns' plugin.
		Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("CrackShot"));
		gunsMode = false;
	}
	public static void givePlayerGun(GamePlayer p, String gunName, int amount) {
		boolean foundGun = false;
		for(String gun : gunsUpgrades.getConfigurationPart("Guns").getKeys(false)) {
			if(gun.equals(gunName)) {
				foundGun = true;
				//Checking which upgrade to give player.
				String chosenUpgrade = null;
				for(String upgradeName : gunsUpgrades.getConfigurationPart("Guns." + gunName).getKeys(false)) {
					if(gunsUpgrades.getInt("Guns." + gunName + "." + upgradeName + ".requiredLevel") < p.getLevel() ||
							gunsUpgrades.getInt("Guns." + gunName + "." + upgradeName + ".requiredLevel") == p.getLevel()) {
						chosenUpgrade = upgradeName;
					}
				}
				//Giving player an upgraded upgrade.
				crackshot.giveWeapon(Bukkit.getPlayer(p.getName()), chosenUpgrade, amount);
				return;
			}
		}
		if(foundGun == false) {
			crackshot.giveWeapon(p.getVanillaPlayer(), gunName, amount);
		}
	}
	public static ItemStack getPlayerGun(GamePlayer p, String gunName) {
		boolean foundGun = false;
		for(String gun : gunsUpgrades.getConfigurationPart("Guns").getKeys(false)) {
			if(gun.equals(gunName)) {
				foundGun = true;
				//Checking which upgrade to give player.
				String chosenUpgrade = null;
				for(String upgradeName : gunsUpgrades.getConfigurationPart("Guns." + gunName).getKeys(false)) {
					if(gunsUpgrades.getInt("Guns." + gunName + "." + upgradeName + ".requiredLevel") < p.getLevel() ||
							gunsUpgrades.getInt("Guns." + gunName + "." + upgradeName + ".requiredLevel") == p.getLevel()) {
						chosenUpgrade = upgradeName;
					}
				}
				//Giving player an upgraded upgrade.
				return crackshot.generateWeapon(chosenUpgrade);
			}
		}
		if(foundGun == false) {
			return crackshot.generateWeapon(gunName);
		}
		return null;
	}
	public static ArrayList<String> checkPlayerNewUpgrades(GamePlayer p) {
		ArrayList<String> unlockedGuns = new ArrayList<String>();
		for(String gun : gunsUpgrades.getConfigurationPart("Guns").getKeys(false)) {
				for(String upgradeName : gunsUpgrades.getConfigurationPart("Guns." + gun).getKeys(false)) {
					if(gunsUpgrades.getInt("Guns." + gun + "." + upgradeName + ".requiredLevel") == p.getLevel()) {
						//Player just unlocked this weapon!
						//Adding it to the unlockedGuns.
						unlockedGuns.add(ChatColor.translateAlternateColorCodes('$', gunsUpgrades.getString("Guns." + gun + "." + upgradeName + ".displayName")));
					}
				}
			}
		return unlockedGuns;
		}
	}
