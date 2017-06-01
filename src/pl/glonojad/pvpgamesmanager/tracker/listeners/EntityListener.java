package pl.glonojad.pvpgamesmanager.tracker.listeners;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.player.StatsManager;
import pl.glonojad.pvpgamesmanager.tracker.util.AttackType;
import pl.glonojad.pvpgamesmanager.tracker.util.DeathMessagesManager;
import pl.glonojad.pvpgamesmanager.tracker.util.TrackerEntityType;
import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.EffectsManager;

import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;


public class EntityListener implements Listener {
	static JavaPlugin plugin;
	static final String UNKNOWN = "unknown";
	ConcurrentHashMap<String,Long> lastDamageTime = new ConcurrentHashMap<String,Long>();
	static HashSet<UUID> ignoreWorlds = new HashSet<UUID>();
	CSUtility crackshot;

	public EntityListener(){
		plugin = PvPGamesManager.instance;
		crackshot = new CSUtility();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		//Player died.
		//Firing the tracker.
		ede(event);
		//Canceling the death message.
		event.setDeathMessage(null);
		//Cancelling the exp.
		event.setDroppedExp(0);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent) {
			//Preventing event from firing twice.
			return;
		}
		//Mob died.
		//Firing the tracker.
		ede(event);
		//Cancelling the exp.
		event.setDroppedExp(0);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByWeapon(WeaponDamageEntityEvent event) {
		//Firing the tracker.
		Entity damager = null;
		if(event.getDamager() instanceof TNTPrimed) {
			TNTPrimed tnt = (TNTPrimed) event.getDamager();
			if(tnt.getSource() != null) {
				damager = tnt.getSource();
				ChatManager.log(event.getVictim().getName() + " shot by " + damager.getName());
			}
		}
		if(event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if(proj.getShooter() != null) {
				damager = (Entity) proj.getShooter();
				ChatManager.log(event.getVictim().getName() + " shot by " + damager.getName());
			}
		}
		//TODO: Do kill tracking
	}

	private void ede(EntityDeathEvent event) {
		//Target and it's killer.
		Entity target = null, killer = null;
		//The type of target and killer entities.
		TrackerEntityType targetType = null, killerType = null;
		//The type of attack.
		AttackType attackType = null;
		//A weapon used to attack.
		ItemStack killingWeapon = null;

		// Getting the Target \\
		if (event.getEntity() instanceof Player){
			target = event.getEntity();
			targetType = TrackerEntityType.PLAYER;
		} else if (event.getEntity() instanceof Tameable){
			target = event.getEntity();
			targetType = TrackerEntityType.TAMED;
		} else {
			if(event.getEntity() instanceof Monster) {
				target = event.getEntity();
				targetType = TrackerEntityType.MONSTER;	
			}
			else if (!(event.getEntity() instanceof Monster)) {
				target = event.getEntity();
				targetType = TrackerEntityType.ANIMAL;	
			}
		}

		// Getting the Killer \\
		EntityDamageEvent lastDamageCause = event.getEntity().getLastDamageCause();
		//Checking DamageCauses.
		if(lastDamageCause instanceof EntityDamageEvent){
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.LAVA)) {
				killer = null;
				killerType = TrackerEntityType.LAVA;
				attackType = AttackType.SWIMMING;
				killingWeapon = new ItemStack(Material.LAVA);
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.DROWNING)) {
				killer = null;
				killerType = TrackerEntityType.WATER;
				attackType = AttackType.DROWNING;
				killingWeapon = new ItemStack(Material.WATER);
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.SUICIDE)) {
				killer = null;
				killerType = TrackerEntityType.PLAYER;
				attackType = AttackType.SUICIDE;
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.VOID)) {
				killer = null;
				killerType = TrackerEntityType.VOID;
				attackType = AttackType.SUFFOCATION;
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.CONTACT)) {
				if(((EntityDamageByBlockEvent)lastDamageCause).getDamager().getType().equals(Material.CACTUS)) {
					killer = null;
					killerType = TrackerEntityType.CACTUS;
					attackType = AttackType.PIERCING;
					killingWeapon = new ItemStack(Material.CACTUS);
				}
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.FALL)) {
				//TODO: Check who doomed to fall.
				killer = null;
				killerType = TrackerEntityType.PLAYER;
				attackType = AttackType.FALL;
				//TODO: Check the killing weapon.
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.FALLING_BLOCK)) {
				//TODO:Check who placed the block!
				killer = null;
				killerType = TrackerEntityType.BLOCK;
				attackType = AttackType.SUFFOCATION;
				killingWeapon = new ItemStack(((EntityDamageByBlockEvent)lastDamageCause).getDamager().getType());
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.FIRE)) {
				//TODO: Check who placed the fire.
				killer = null;
				killerType = TrackerEntityType.FIRE;
				attackType = AttackType.BURNING;
				killingWeapon = new ItemStack(Material.FIRE);
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.FIRE_TICK)) {
				//TODO: Check who placed the fire.
				killer = null;
				killerType = TrackerEntityType.FIRE;
				attackType = AttackType.BURNING;
				killingWeapon = new ItemStack(Material.FIRE);
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.LIGHTNING)) {
				killer = null;
				killerType = TrackerEntityType.LIGHTNING;
				attackType = AttackType.SHOCK;
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.MELTING)) {
				killer = null;
				killerType = TrackerEntityType.SNOWMAN;
				attackType = AttackType.FREEZING;
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.STARVATION)) {
				killer = null;
				killerType = TrackerEntityType.HUNGER;
				attackType = AttackType.STARVATION;
			}
			if(((EntityDamageEvent)lastDamageCause).getCause().equals(DamageCause.SUFFOCATION)) {
				killer = null;
				killerType = TrackerEntityType.BLOCK;
				attackType = AttackType.SUFFOCATION;
				killingWeapon = new ItemStack(((EntityDamageByBlockEvent)lastDamageCause).getDamager().getType());
			}
		}
		if (lastDamageCause instanceof EntityDamageByEntityEvent && killer == null){
			//Getting the damager.
			Entity damager = ((EntityDamageByEntityEvent) lastDamageCause).getDamager();
			if (damager instanceof Player) { /// Damager is a player.
				attackType = AttackType.MELEE;
				killer = damager;
				killerType = TrackerEntityType.PLAYER;
				killingWeapon = ((Player) killer).getItemInHand();
				
			}
			else if (damager instanceof Projectile) { // Damager is a projectile.
				attackType = AttackType.PROJECTILE;
				Projectile proj = (Projectile) damager;
				if (proj.getShooter() instanceof Player){ // Projectile was shot by a player.
					killerType = TrackerEntityType.PLAYER;
					killer = (Entity) proj.getShooter();
					killingWeapon = ((Player)killer).getItemInHand();
				} else if (proj.getShooter() != null){ // Projectile shot by some mob, or other source.
					if((Entity)proj.getShooter() instanceof Monster) {
						killer = (Entity)proj.getShooter();
						killerType = TrackerEntityType.MONSTER;
					}
					else if (!((Entity)proj.getShooter() instanceof Monster)) {
						killer = (Entity)proj.getShooter();
						killerType = TrackerEntityType.ANIMAL;	
					}
				} else {
					killer = null; // Projectile has no owner?
				}
			} else if (damager instanceof Tameable && ((Tameable) damager).isTamed()) { //Damager is a tamed animal.
				killer = damager;
				killerType = TrackerEntityType.TAMED;
				attackType = AttackType.PIERCING;
			} else if (damager instanceof Explosive) { //Damager is an explosive entity.
				if(damager instanceof TNTPrimed) {
					TNTPrimed tnt = (TNTPrimed) damager;
					Entity source = tnt.getSource();
					if(source != null) {
						if(source instanceof Player) {
							killer = source;	
							killerType = TrackerEntityType.PLAYER;
						}
						if(source instanceof Projectile) {
							Projectile proj = (Projectile) source;
							killer = (Entity) proj.getShooter();
							killerType = TrackerEntityType.PLAYER;
						}
						if(source instanceof TNTPrimed) {
							TNTPrimed tntOther = (TNTPrimed) source;
							killer = (Entity) tntOther.getSource();
							killerType = TrackerEntityType.PLAYER;
						}
						attackType = AttackType.TNT;
						killingWeapon = new ItemStack(Material.TNT);
					}
					else {
						killer = null;
						attackType = AttackType.TNT;
						killingWeapon = new ItemStack(Material.TNT);
					}
				}
				if(damager instanceof Fireball) {
					Fireball fireball = (Fireball) damager;
					Entity source = (Entity) fireball.getShooter();
					if(source != null) {
						if(source instanceof Player) {
							killer = source;	
							killerType = TrackerEntityType.PLAYER;
							killingWeapon = ((Player) source).getItemInHand();
						}
						else {
							//A mob launched the projectile.
							killer = source;	
							killerType = TrackerEntityType.MONSTER;
						}
						attackType = AttackType.FIREBALL;
					}
					else {
						//The projectile came out from nowhere.
						killer = null;
						attackType = AttackType.FIREBALL;
					}
				}
				else if(damager instanceof Animals) {
					killer = damager;
					killerType = TrackerEntityType.ANIMAL;
					attackType = AttackType.MELEE;
				}
				else if (damager instanceof Monster) {
					killer = damager;
					killerType = TrackerEntityType.MONSTER;
					attackType = AttackType.MELEE;
				}
			}
			else { /// Killer is not a player
				killer = damager;
				killerType = TrackerEntityType.OTHER;
			}
		}else {
			if (lastDamageCause == null || lastDamageCause.getCause() == null) {
				killer = null;
			}
			else {
				killer = null;
			}			
		}
		/// Decide what to do
		if (targetType == TrackerEntityType.PLAYER && target != null && killerType == TrackerEntityType.PLAYER && killer != null){
			//Player killed another player.
			//Add player a kill.
			StatsManager.addKill(GamePlayer.getGamePlayer(killer.getName()));
			//Broadcasting a message.
			DeathMessagesManager.broadcastDeathMessage(target, killer, attackType, killerType, targetType, killingWeapon);
			//Spawning experience to killer.
			if(killer != null) {
				EffectsManager.spawnExperience(killer.getLocation(), 10);	
			}
		} else if (targetType == TrackerEntityType.MONSTER && killerType == TrackerEntityType.MONSTER || targetType == TrackerEntityType.ANIMAL && killerType == TrackerEntityType.ANIMAL){ //A mob killed another mob.
			//Do nothing
		} else { //There is one player and one other entity.
			//Spawning experience to killer.
			if(killer != null) {
				EffectsManager.spawnExperience(killer.getLocation(), 5);	
			}
			//Check message sending
			if (targetType == TrackerEntityType.PLAYER && event instanceof PlayerDeathEvent){
				DeathMessagesManager.broadcastDeathMessage(target, killer, attackType, killerType, targetType, killingWeapon);
			}
		}
	}
}