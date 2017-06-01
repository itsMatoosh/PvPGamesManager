package pl.glonojad.pvpgamesmanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandManager {
	public static boolean execute(CommandSender sender, Command command,String label, String[] args) {
		//Join Command\\
		if(command.getName().equalsIgnoreCase("join")) {
			new Thread(new JoinCommand(sender, args)).run();
			return true;
		}
		//Kit command\\
		if(command.getName().equalsIgnoreCase("kit")) {
			new Thread(new KitCommand(sender, args)).run();
			return true;
		}
		if(command.getName().equalsIgnoreCase("leave")) {
			new Thread(new LeaveCommand(sender, args)).run();
			return true;
		}
		//Rotation command\\
		if(command.getName().equalsIgnoreCase("rotation")) {
			new Thread(new RotationCommand(sender, args)).run();
			return true;
		}
		//Game command\\
		if(command.getName().equalsIgnoreCase("game")) {
			new Thread(new GameCommand(sender, args)).run();
			return true;
		}
		//Teams command\\
		if(command.getName().equalsIgnoreCase("teams")) {
			new Thread(new TeamsCommand(sender, args)).run();
			return true;
		}
		//Upgrades command\\
		if(command.getName().equalsIgnoreCase("upgrades")) {
			new Thread(new UpgradesCommand(sender, args)).run();
			return true;
		}
		//Stats command\\
		if(command.getName().equalsIgnoreCase("stats")) {
			new Thread(new StatsCommand(sender, args)).run();
			return true;
		}
		return false;
	}
}
