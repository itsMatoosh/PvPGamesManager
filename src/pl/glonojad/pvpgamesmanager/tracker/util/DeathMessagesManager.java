package pl.glonojad.pvpgamesmanager.tracker.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.glonojad.pvpgamesmanager.util.ChatManager;

public class DeathMessagesManager {
	
	public static void broadcastDeathMessage(Entity target, Entity killer, AttackType attackType, TrackerEntityType killerType, TrackerEntityType targetType, ItemStack killingWeapon) {
		if(attackType.equals(AttackType.MELEE)) {
			//Player was killed by an entity.
			if(killerType.equals(TrackerEntityType.PLAYER)) {
				//Player was killed by a player.
				if(killingWeapon.hasItemMeta() && killingWeapon.getItemMeta().hasDisplayName()) {
					//This is a special item.
					ChatManager.broadcastMessage("PLAYER-Death-From-Entity-Special-Item", "{0}" + ChatColor.GRAY + " was killed by " + "{1}" + ChatColor.GRAY + " using " + "{2}", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName(), killingWeapon.getItemMeta().getDisplayName());
				}
				else {
					//This is an ordinary item.
					ChatManager.broadcastMessage("PLAYER-Death-From-Entity", "{0}" + ChatColor.GRAY + " was killed by " + "{1}", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName());
				}
				return;
			}
			else {
				if(killerType.equals(TrackerEntityType.MONSTER)) {
					//Player was killed by a monster.
					ChatManager.broadcastMessage("PLAYER-Death-From-Monster", "{0}" + ChatColor.GRAY + " was killed by a " + ChatColor.DARK_GREEN + "{1}", ((Player)target).getDisplayName(), killer.getName());	
				}
				if(killerType.equals(TrackerEntityType.ANIMAL)) {
					//Player was killed by a animal.
					ChatManager.broadcastMessage("PLAYER-Death-From-Animal", "{0}" + ChatColor.GRAY + " was killed by a " + ChatColor.YELLOW + "{1}", ((Player)target).getDisplayName(), killer.getName());	
				}
			}
		}
		if(attackType.equals(AttackType.PROJECTILE)) {
			//Player was shot by an entity.
			if(killerType.equals(TrackerEntityType.PLAYER)) {
				//Player was killed by a player.
				if(killingWeapon.hasItemMeta() && killingWeapon.getItemMeta().hasDisplayName()) {
					//This is a special item.
					ChatManager.broadcastMessage("PLAYER-Shot-By-Player-Special-Item", "{0}" + ChatColor.GRAY + " was shot by " + "{1}" + ChatColor.GRAY + " using " + "{2}", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName(), killingWeapon.getItemMeta().getDisplayName());
				}
				else {
					//This is an ordinary item.
					ChatManager.broadcastMessage("PLAYER-Shot-By-Player", "{0}" + ChatColor.GRAY + " was shot by " + "{1}", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName());
				}
				return;
			}
			else {
				if(killerType.equals(TrackerEntityType.MONSTER)) {
					//Player was killed by a monster.
					ChatManager.broadcastMessage("PLAYER-Shot-By-Monster", "{0}" + ChatColor.GRAY + " was shot by a " + ChatColor.DARK_GREEN + "{1}", ((Player)target).getDisplayName(), killer.getName());	
				}
				if(killerType.equals(TrackerEntityType.ANIMAL)) {
					//Player was killed by a animal.
					ChatManager.broadcastMessage("PLAYER-Shot-By-Animal", "{0}" + ChatColor.GRAY + " was killed by a " + ChatColor.YELLOW + "{1}", ((Player)target).getDisplayName(), killer.getName());	
				}
			}
		}
		if(attackType.equals(AttackType.EXPLOSION)) {
			if(killer != null) {
				//This Explosion belongs to a Player.
				//Broadcasting a message.
				if(killingWeapon.hasItemMeta() && killingWeapon.getItemMeta().hasDisplayName()) {
					//This is a special item.
					ChatManager.broadcastMessage("PLAYER_Death-From-Players-Explosion", "{0}" + ChatColor.GRAY + " was blown up by " + "{1}" + ChatColor.GRAY + " using " + "{2}", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName(), killingWeapon.getItemMeta().getDisplayName());
				}
				else {
					//This is an ordinary item.
					ChatManager.broadcastMessage("PLAYER_Death-From-Players-Explosion", "{0}" + ChatColor.GRAY + " was blown up by " + "{1}", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName());	
				}
				return;
			}
			else {
				//This Explosion doesn't belong to anyone.
				//Broadcasting a message.
				ChatManager.broadcastMessage("PLAYER_Death-From-Explosion", "{0}" + ChatColor.GRAY + " was blown up", ((Player)target).getDisplayName());
				return;
			}
		}
		if(attackType.equals(AttackType.TNT)) {
			if(killer != null) {
				//This TNT belongs to a Player.
				//Broadcasting a message.
				ChatManager.broadcastMessage("PLAYER_Death-From-Players-TNT", "{0}" + ChatColor.GRAY + " was blown up by " + "{1}" + ChatColor.GRAY + "'s TNT", ((Player)target).getDisplayName(), ((Player)killer).getDisplayName());
				return;
			}
			else {
				//This TNT doesn't belong to anyone.
				//Broadcasting a message.
				ChatManager.broadcastMessage("PLAYER_Death-From-TNT", "{0}" + ChatColor.GRAY + " was blown up by a TNT", ((Player)target).getDisplayName());
				return;
			}
		}
		if(attackType.equals(AttackType.PIERCING)) {
			ChatManager.broadcastMessage("PLAYER-Death-From-Piercing-Block", "{0}" + ChatColor.GRAY + " was pierced to death by a " + "{1}", ((Player)target).getDisplayName(), killingWeapon.getItemMeta().getDisplayName());
			return;
		}
		if(attackType.equals(AttackType.DROWNING)) {
			//Player drowned.
			ChatManager.broadcastMessage("PLAYER-Death-From-Drowning", "{0}" + ChatColor.GRAY + " drowned", ((Player)target).getDisplayName());
			return;
		}
		if(attackType.equals(AttackType.BURNING)) {
			//Player drowned.
			ChatManager.broadcastMessage("PLAYER-Death-From-Burning", "{0}" + ChatColor.GRAY + " burned to a crisp", ((Player)target).getDisplayName());
			return;
		}
		if(attackType.equals(AttackType.SWIMMING)) {
			//Player swam in a wrong place.
			ChatManager.broadcastMessage("PLAYER-Death-From-Swimming", "{0}" + ChatColor.GRAY + " tried to swim in " + "{1}", ((Player)target).getDisplayName(), killingWeapon.getType());
			return;
		}
		if(attackType.equals(AttackType.SUICIDE)) {
			//Player swam in a wrong place.
			ChatManager.broadcastMessage("PLAYER-Death-From-Suicide", "{0}" + ChatColor.GRAY + " commited a suicide", ((Player)target).getDisplayName());
			return;
		}
		if(attackType.equals(AttackType.SUFFOCATION)) {
			//Player suffocated.
			if(killerType.equals(TrackerEntityType.VOID)) {
				ChatManager.broadcastMessage("PLAYER-Death-From-Void", "{0}" + ChatColor.GRAY + " fell out of the world", ((Player)target).getDisplayName());
				return;
			}
			if(killingWeapon == null) {
				//We don't know in what player suffocated.
				ChatManager.broadcastMessage("PLAYER-Death-From-Suffocation", "{0}" + ChatColor.GRAY + " suffocated", ((Player)target).getDisplayName());
				return;
			} else {
				//We know in what player suffocated.
				ChatManager.broadcastMessage("PLAYER-Death-From-Suffocation-Known-Block", "{0}" + ChatColor.GRAY + " suffocated in " + "{1}", ((Player)target).getDisplayName(), killingWeapon.getType().toString());
				return;
			}
		}
		if(attackType.equals(AttackType.FALL)) {
			//Player swam in a wrong place.
			if(killer == null) {
				ChatManager.broadcastMessage("PLAYER-Death-From-Fall", "{0}" + ChatColor.GRAY + " fell from a high place", ((Player)target).getDisplayName());	
			}
			return;
		}
		if(attackType.equals(AttackType.SHOCK)) {
			//Player swam in a wrong place.
			ChatManager.broadcastMessage("PLAYER-Death-From-Shock", "{0}" + ChatColor.GRAY + " was kicked by a high voltage", ((Player)target).getDisplayName());
			return;
		}
		if(attackType.equals(AttackType.STARVATION)) {
			//Player swam in a wrong place.
			ChatManager.broadcastMessage("PLAYER-Death-From-Starvation", "{0}" + ChatColor.GRAY + " starved", ((Player)target).getDisplayName());
			return;
		}
		if(attackType.equals(AttackType.FREEZING)) {
			//Player swam in a wrong place.
			ChatManager.broadcastMessage("PLAYER-Death-From-Freezing", "{0}" + ChatColor.GRAY + " died from cold", ((Player)target).getDisplayName());
			return;
		}
	}
}
