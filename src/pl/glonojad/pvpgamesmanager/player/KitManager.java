package pl.glonojad.pvpgamesmanager.player;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.game.GameTeam;
import pl.glonojad.pvpgamesmanager.objects.IconMenu;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.WeaponsManager;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;

public class KitManager {
	public static void openKitMenu(final GamePlayer p) {
		GameTeam playerTeam = GameTeam.getPlayerTeam(p);
		if(playerTeam.getSettings().getBoolean("player.enableKits")) {
			//Open the kit menu if kits are enabled.
			if(p.canChooseKit()) {
				//Open the menu if player can choose a kit.
			     IconMenu kitMenu = new IconMenu(PvPGamesManager.language.get(p.getVanillaPlayer(), "KIT_Select-Menu-Title", ChatColor.BLUE + "" + ChatColor.BOLD + "Kit selector"), 9, new IconMenu.OptionClickEventHandler() {
			    	 public void onOptionClick(IconMenu.OptionClickEvent clickEvent) {
			    		 KitManager.givePlayerKit(GamePlayer.getGamePlayer(clickEvent.getPlayer().getName()), clickEvent.getName());
			    		 p.setCanChooseKit(false);
			    		 clickEvent.setWillClose(true);
			    	 }
			     	}, PvPGamesManager.instance);
				//Setting up the kit menu.
				kitMenu.setSpecificTo(p.getVanillaPlayer());
				for(String kit : playerTeam.getSettings().getConfigurationPart("player.kits").getKeys(false)) {
					ConfigurationPart kitSettings = playerTeam.getSettings().getConfigurationPart("player.kits." + kit);
					kitMenu.setOption(kitSettings.getInt("itemMenu.position"), kitSettings.getItemStack("itemMenu.icon"),kitSettings.getName(), ChatColor.translateAlternateColorCodes('$', kitSettings.getString("itemMenu.description")));
				}
				//Opening the menu.
				kitMenu.open(p.getVanillaPlayer());
				ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), "KIT_Choose-Kit", ChatColor.GREEN + "Choose your " + ChatColor.YELLOW + "kit" + ChatColor.GREEN + "!"));	
			}
			else {
				ChatManager.sendMessageToPlayer(p, PvPGamesManager.language.get(p.getVanillaPlayer(), 
					"KIT_Already-Chosen", ChatColor.RED + "You have already chosen a kit for this round!"));
			}
		}
	}
	public static void givePlayerKit(GamePlayer gp, String kitName) {
		ConfigurationPart kitSettings = GameTeam.getPlayerTeam(gp).getSettings().getConfigurationPart("player.kits." + kitName);
		Player p = gp.getVanillaPlayer();
		//Clearing player's old inventory.
		p.getInventory().clear();
		p.setHealth(20);
		p.setFoodLevel(20);
		if(kitSettings.isSet("helmet")) {
			//Giving player a helmet.
			ItemStack helmet = kitSettings.getItemStack("helmet");
			p.getInventory().setHelmet(helmet);
		}
		if(kitSettings.isSet("chestplate")) {
			//Giving player a chestplate.
			ItemStack chestplate = kitSettings.getItemStack("chestplate");
			p.getInventory().setChestplate(chestplate);
		}
		if(kitSettings.isSet("leggings")) {
			//Giving player leggings.
			ItemStack leggings = kitSettings.getItemStack("leggings");
			p.getInventory().setLeggings(leggings);
		}
		if(kitSettings.isSet("boots")) {
			//Giving player boots.
			ItemStack boots = kitSettings.getItemStack("boots");
			p.getInventory().setBoots(boots);
		}
		for(String item : kitSettings.getConfigurationPart("inventory").getKeys(false)){
			ConfigurationPart inventorySettings = kitSettings.getConfigurationPart("inventory");
			if(item.contains("item")) {
				p.getInventory().addItem(inventorySettings.getItemStack(item));
			}
			if(item.contains("weapon")) {
				String[] nameAndAmount = inventorySettings.getString(item).split(":");
				if(nameAndAmount.length == 2) {
					WeaponsManager.givePlayerGun(gp, nameAndAmount[0], Integer.parseInt(nameAndAmount[1]));	
				}
				else {
					WeaponsManager.givePlayerGun(gp, nameAndAmount[0], 1);	
				}
			}
		}
		//Give player potion effects.
		for(String effect : kitSettings.getConfigurationPart("effects").getKeys(false)) {
			ConfigurationPart effectsSettings = kitSettings.getConfigurationPart("effects");
			if(effect.equalsIgnoreCase("ABSORPTION")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("BLINDNESS")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("CONFUSION")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("DAMAGE_RESISTANCE")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("FAST_DIGGING")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("FIRE_RESISTANCE")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("HARM")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HARM, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("HEAL")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("HEALTH_BOOST")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("HUNGER")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("INCREASE_DAMAGE")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("INVISIBILITY")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("JUMP")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("NIGHT_VISION")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("POISON")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("REGENERATION")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("SATURATION")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("SLOW")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("SLOW_DIGGING")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("SPEED")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("WATER_BREATHING")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("WEAKNESS")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
			if(effect.equalsIgnoreCase("WITHER")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, effectsSettings.getInt(effect + ".duration"), effectsSettings.getInt(effect + ".amplifier"), effectsSettings.getBoolean(effect + ".ambient"), effectsSettings.getBoolean(effect + ".particles")));
			}
		}	
	}
	public static Color getColorByName(String colorName) {
		if(colorName.equalsIgnoreCase("AQUA")) {
			return Color.AQUA;
		}
		if(colorName.equalsIgnoreCase("BLACK")) {
			return Color.BLACK;
		}
		if(colorName.equalsIgnoreCase("FUCHSIA")) {
			return Color.FUCHSIA;
		}
		if(colorName.equalsIgnoreCase("GRAY")) {
			return Color.GRAY;
		}
		if(colorName.equalsIgnoreCase("GREEN")) {
			return Color.GREEN;
		}
		if(colorName.equalsIgnoreCase("LIME")) {
			return Color.LIME;
		}
		if(colorName.equalsIgnoreCase("MAROON")) {
			return Color.MAROON;
		}
		if(colorName.equalsIgnoreCase("NAVY")) {
			return Color.NAVY;
		}
		if(colorName.equalsIgnoreCase("OLIVE")) {
			return Color.OLIVE;
		}
		if(colorName.equalsIgnoreCase("ORANGE")) {
			return Color.ORANGE;
		}
		if(colorName.equalsIgnoreCase("PURPLE")) {
			return Color.PURPLE;
		}
		if(colorName.equalsIgnoreCase("RED")) {
			return Color.RED;
		}
		if(colorName.equalsIgnoreCase("SILVER")) {
			return Color.SILVER;
		}
		if(colorName.equalsIgnoreCase("TEAL")) {
			return Color.TEAL;
		}
		if(colorName.equalsIgnoreCase("WHITE")) {
			return Color.WHITE;
		}
		if(colorName.equalsIgnoreCase("YELLOW")) {
			return Color.YELLOW;
		}
		return null;
	}
}
