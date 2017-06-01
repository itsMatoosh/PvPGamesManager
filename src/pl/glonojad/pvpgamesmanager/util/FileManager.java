package pl.glonojad.pvpgamesmanager.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.map.MapManager;
import pl.glonojad.pvpgamesmanager.util.configuration.Configuration;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationType;


public class FileManager {
	
	//Files' Variables
	private static File pluginFolder = PvPGamesManager.instance.getDataFolder();
	private static File config = new File(pluginFolder,"config.yml");
	private static File rules = new File(pluginFolder,"rules.yml");
	private static File book = new File(pluginFolder,"book.yml");
	private static File serverMOTD = new File(pluginFolder,"serverMOTD.yml");
	private static File broadcastMessages = new File(pluginFolder,"messagesToBroadcast.yml");
	private static File stats = new File(pluginFolder,"stats.yml");
	public static File rotation = new File(pluginFolder,"rotation.yml");
	private static File resourcePacks = new File(pluginFolder,"resourcePacks.yml");
	private static File gunsUpgrades = new File(pluginFolder,"gunsUpgrades.yml");
	private static File mapFolder = new File(pluginFolder,"maps");
	private static File schematicsFolder = new File(pluginFolder,"schematics");
	public static File loadedMap;
	private static YamlConfiguration configYml;
	private static YamlConfiguration rulesYml;
	private static YamlConfiguration bookYml;
	private static YamlConfiguration serverMOTDYml;
	private static YamlConfiguration broadcastMessagesYml;
	private static YamlConfiguration statsYml;
	private static YamlConfiguration rotationYml;
	private static YamlConfiguration resourcePacksYml;
	private static YamlConfiguration gunsUpgradesYml;

	//Method for loading all configuration files.
	public static void loadData() {
		//Checking if plugin's folder exist.
		if(!pluginFolder.exists()) {
			pluginFolder.mkdir();
		}
		//Checking if maps folder exist.
		if(!mapFolder.exists()) {
			mapFolder.mkdir();
		}
		//Checking if schematics folder exist.
		if(!schematicsFolder.exists()) {
			schematicsFolder.mkdir();
		}
		//Loading PvPGamesManager configuration file.
		if(!config.exists()) {
			PvPGamesManager.instance.saveDefaultConfig();
		}
		configYml = YamlConfiguration.loadConfiguration(config);
		//Loading rules configuration file.
		if(!rules.exists()) {
			PvPGamesManager.instance.saveResource("rules.yml", true);
		}
		rulesYml = YamlConfiguration.loadConfiguration(rules);
		//Loading book configuration file.
		if(!book.exists()) {
			PvPGamesManager.instance.saveResource("book.yml", true);
		}
		bookYml = YamlConfiguration.loadConfiguration(book);
		//Loading server MOTD configuration file.
		if(!serverMOTD.exists()) {
			PvPGamesManager.instance.saveResource("serverMOTD.yml", true);
		}
		serverMOTDYml = YamlConfiguration.loadConfiguration(serverMOTD);
		//Loading messages configuration file.
		if(!broadcastMessages.exists()) {
			PvPGamesManager.instance.saveResource("messagesToBroadcast.yml", true);
		}
		broadcastMessagesYml = YamlConfiguration.loadConfiguration(broadcastMessages);
		//Loading statistics file.
		if(!stats.exists()) {
			PvPGamesManager.instance.saveResource("stats.yml", true);
		}
		statsYml = YamlConfiguration.loadConfiguration(stats);
		//Loading nextgame file.
		if(!rotation.exists()) {
			PvPGamesManager.instance.saveResource("rotation.yml", true);
		}
		rotationYml = YamlConfiguration.loadConfiguration(rotation);
		//Loading resourcepacks file.
		if(!resourcePacks.exists()) {
			PvPGamesManager.instance.saveResource("resourcePacks.yml", true);
		}
		resourcePacksYml = YamlConfiguration.loadConfiguration(resourcePacks);
		//Loading gunsUpgrades file.
		if(!gunsUpgrades.exists()) {
			PvPGamesManager.instance.saveResource("gunsUpgrades.yml", true);
		}
		gunsUpgradesYml = YamlConfiguration.loadConfiguration(gunsUpgrades);
	}
	//Method for reloading all the configuration files.
	public static void reloadAllConfigurtions() {
		configYml = YamlConfiguration.loadConfiguration(config);
		rulesYml = YamlConfiguration.loadConfiguration(rules);
		bookYml = YamlConfiguration.loadConfiguration(book);
		serverMOTDYml = YamlConfiguration.loadConfiguration(serverMOTD);
		broadcastMessagesYml = YamlConfiguration.loadConfiguration(broadcastMessages);
		statsYml = YamlConfiguration.loadConfiguration(stats);
		rotationYml = YamlConfiguration.loadConfiguration(rotation);
		resourcePacksYml = YamlConfiguration.loadConfiguration(resourcePacks);
		gunsUpgradesYml = YamlConfiguration.loadConfiguration(gunsUpgrades);
	}
	//Methods for saving each configuration file.
	public static void saveConfig() {
		try {
			configYml.save(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveRules() {
		try {
			rulesYml.save(rules);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveBook() {
		try {
			bookYml.save(book);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveServerMOTD() {
		try {
			serverMOTDYml.save(serverMOTD);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveBroadcastMessages() {
		try {
			broadcastMessagesYml.save(broadcastMessages);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveStats() {
		try {
			statsYml.save(stats);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveRotation() {
		try {
			rotationYml.save(rotation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveResourcePacks() {
		try {
			resourcePacksYml.save(resourcePacks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveGunsUpgrades() {
		try {
			gunsUpgradesYml.save(gunsUpgrades);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveOtherConfig(YamlConfiguration config, ConfigurationType type) {
		if(type == ConfigurationType.CONFIG) {
			saveConfig();
		}
		if(type == ConfigurationType.RULES) {
			saveRules();
		}
		if(type == ConfigurationType.BOOK) {
			saveBook();
		}
		if(type == ConfigurationType.SERVERMOTD) {
			saveServerMOTD();
		}
		if(type == ConfigurationType.MESSAGES_TO_BROADCAST) {
			saveBroadcastMessages();
		}
		if(type == ConfigurationType.STATS) {
			saveStats();
		}
		if(type == ConfigurationType.ROTATION) {
			saveRotation();
		}
		if(type == ConfigurationType.RESOURCEPACKS) {
			saveResourcePacks();
		}
		if(type == ConfigurationType.GUNS_UPGRADES) {
			saveGunsUpgrades();
		}
		if(type == ConfigurationType.MAP) {
			for(File folder : mapFolder.listFiles()) {
				if(folder.getName().equals(MapManager.currentMapFolder.getName()) && folder.isDirectory()) {
					for(File f : folder.listFiles()) {
						if(f.getName().equals("map.yml")) {
							try {
								config.save(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}		
					}
				}
			}
		}
	}
	//Methods for getting data.
	public static Configuration getConfig() {
		return new Configuration(configYml, ConfigurationType.CONFIG);
	}
	public static Configuration getRules() {
		return new Configuration(rulesYml, ConfigurationType.RULES);
	}
	public static Configuration getBook() {
		return new Configuration(bookYml, ConfigurationType.BOOK);
	}
	public static Configuration getServerMOTD() {
		return new Configuration(serverMOTDYml, ConfigurationType.SERVERMOTD);
	}
	public static Configuration getBroadcastMessages() {
		return new Configuration(broadcastMessagesYml, ConfigurationType.MESSAGES_TO_BROADCAST);
	}
	public static Configuration getStats() {
		return new Configuration(statsYml, ConfigurationType.STATS);
	}
	public static Configuration getRotation() {
		return new Configuration(rotationYml, ConfigurationType.ROTATION);
	}
	public static Configuration getResourcePacks() {
		return new Configuration(resourcePacksYml, ConfigurationType.RESOURCEPACKS);
	}
	public static Configuration getGunsUpgrades() {
		return new Configuration(gunsUpgradesYml, ConfigurationType.GUNS_UPGRADES);
	}
	public static File getPluginMap(String mapName) {
		for(File f : mapFolder.listFiles()) {
			if(f.isDirectory()) {
				if(f.getName().equals(mapName)) {
					return f;
				}
			}
		}
		return null;
	}
	public static File getServerMap(String mapName) {
		ChatManager.log("Searching for map " + mapName + "!");
		for(File f : Bukkit.getWorldContainer().listFiles()) {
			if(f.isDirectory()) {
				if(f.getName().equals(mapName)) {
					ChatManager.log("Successfully found " + f.getName());
					return f;
				}
			}
		}
		ChatManager.logWarning("Couldn't find " + mapName + " in the PvPGamesManager server directory!");
		return null;
	}
	public static YamlConfiguration getMapYml(String mapName) {
		for(File f : getPluginMap(mapName).listFiles()) {
			if (f.getName().equals("map.yml")) {
				YamlConfiguration mapYml = YamlConfiguration.loadConfiguration(f);
				return mapYml;
			}
		}
		return null;
	}
	public static File getMapsFolder() {
		return mapFolder;
	}
	public static File getSchematicsFolder() {
		return schematicsFolder;
	}
	public static File getSchematic(String schematicName) {
		for(File f : getSchematicsFolder().listFiles()) {
			if(f.getName().equals(schematicName)) {
				return f;
			}
		}
		return null;
	}
	//Methods for manipulating the files.
	public static void copyMapToServerFolder(File map) {
		File copyDestination = new File(Bukkit.getWorldContainer(),map.getName());
		loadedMap = copyDestination;
        try{
        	copyFolder(map,copyDestination);
        }catch(IOException e){
        	e.printStackTrace();
        }
	}
	public static void removeMapFromMainServerFolder(File map) {
		ChatManager.log("Removing map " + map.getName() + " from the main server folder.");
		deleteFolder(map);
    }
    public static void copyFolder(File src, File dest)
        	throws IOException{
     
        	if(src.isDirectory()){
     
        		//if directory not exists, create it
        		if(!dest.exists()){
        		   dest.mkdir();
        		   /*ChatManager.log("Directory copied from " 
                                  + src + "  to " + dest);*/
        		}
     
        		//list all the directory contents
        		String files[] = src.list();
     
        		for (String file : files) {
        		   //construct the src and dest file structure
        		   File srcFile = new File(src, file);
        		   File destFile = new File(dest, file);
        		   //recursive copy
        		   copyFolder(srcFile,destFile);
        		}
     
        	}else{
        		//if file, then copy it
        		//Use bytes stream to support all file types
        		InputStream in = new FileInputStream(src);
        	        OutputStream out = new FileOutputStream(dest); 
     
        	        byte[] buffer = new byte[1024];
     
        	        int length;
        	        //copy the file content in bytes 
        	        while ((length = in.read(buffer)) > 0){
        	    	   out.write(buffer, 0, length);
        	        }
     
        	        in.close();
        	        out.close();
        	        //ChatManager.log("File copied from " + src + " to " + dest);
        	}
        }
    public static boolean deleteFolder(File dir) {
            if(! dir.exists() || !dir.isDirectory())    {
                return false;
            }

            String[] files = dir.list();
            for(int i = 0, len = files.length; i < len; i++)    {
                File f = new File(dir, files[i]);
                if(f.isDirectory()) {
                    deleteFolder(f);
                }else   {
                    f.delete();
                }
            }
            return dir.delete();
        }
}