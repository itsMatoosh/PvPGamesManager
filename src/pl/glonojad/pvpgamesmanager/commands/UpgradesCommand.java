package pl.glonojad.pvpgamesmanager.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.objects.IconMenu;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.WeaponsManager;

public class UpgradesCommand implements Runnable{
	public CommandSender sender;
	public String[] args;
	
	public UpgradesCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	public void run() {
		if(sender instanceof Player) {
			final GamePlayer p = GamePlayer.getGamePlayer(sender.getName());
			//Open the menu.
		     IconMenu upgradesMenu = new IconMenu(PvPGamesManager.language.get(p.getVanillaPlayer(), "UPGRADES_Menu-Title", ChatColor.GREEN + "" + ChatColor.BOLD + "Upgrades"), 45, new IconMenu.OptionClickEventHandler() {
		    	 public void onOptionClick(IconMenu.OptionClickEvent clickEvent) {
		    		 
		    	 }
		     	}, PvPGamesManager.instance);
		     int index = 0;
			 for(String weapon : WeaponsManager.gunsUpgrades.getConfigurationPart("Guns").getKeys(false)) {
				 int upgradesIndex = index;
				for(String upgradeName : WeaponsManager.gunsUpgrades.getConfigurationPart("Guns." + weapon).getKeys(false)) {
					//Formatting upgrade description.
					List<String> description = WeaponsManager.gunsUpgrades.getStringList("Guns." + weapon + "." + upgradeName + ".description");
					int i = 0;
					for(String line : description) {
						description.set(i, ChatColor.translateAlternateColorCodes('$', line));
						i++;
					}
					//Figuring out how to display missing textures.
					if(WeaponsManager.crackshot == null) {
						//Player currently has this upgrade.
						upgradesMenu.setOption(upgradesIndex, 
								new ItemStack(Material.GLASS), 
								ChatColor.translateAlternateColorCodes('$', WeaponsManager.gunsUpgrades.getString("Guns." + weapon + "." + upgradeName + ".displayName")),
								description.toArray(new String[description.size()]));
							upgradesIndex = upgradesIndex + 9;
					}
					else {
						if(!WeaponsManager.getPlayerGun(p, weapon).equals(WeaponsManager.crackshot.generateWeapon(upgradeName))) {
							//Player currently has this upgrade.
							upgradesMenu.setOption(upgradesIndex, 
									WeaponsManager.crackshot.generateWeapon(upgradeName), 
									ChatColor.translateAlternateColorCodes('$', WeaponsManager.gunsUpgrades.getString("Guns." + weapon + "." + upgradeName + ".displayName")),
									description.toArray(new String[description.size()]));
								upgradesIndex = upgradesIndex + 9;
						}
						else {
							upgradesMenu.setOption(upgradesIndex, 
									WeaponsManager.crackshot.generateWeapon(upgradeName), 
									ChatColor.YELLOW + ">> " + ChatColor.translateAlternateColorCodes('$', WeaponsManager.gunsUpgrades.getString("Guns." + weapon + "." + upgradeName + ".displayName")) + ChatColor.YELLOW + " <<",
									description.toArray(new String[description.size()]));
								upgradesIndex = upgradesIndex + 9;		
						}
					}
				}
				index++;
			 }
			 upgradesMenu.setSpecificTo(p.getVanillaPlayer());
			 upgradesMenu.open(p.getVanillaPlayer());
			 return;
		}
		else {
			ChatManager.logWarning("Console can't use the upgrades menu!");
		}
	}

}
