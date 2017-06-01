package pl.glonojad.pvpgamesmanager.util;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.glonojad.pvpgamesmanager.PvPGamesManager;
import pl.glonojad.pvpgamesmanager.player.GamePlayer;
import pl.glonojad.pvpgamesmanager.util.configuration.ConfigurationPart;


public class ResourcePackManager{
	
	public static HashMap<Player,String> PlayerTexturePacks = new HashMap<Player,String>(); //<player name, texture pack name>
	public static HashMap<String,String> TexturePacksbyName = new HashMap<String,String>(); //<tpack name, tpack url>
	
	public static void Initialize() {
		ConfigurationPart TexturesConfig = FileManager.getResourcePacks().getConfigurationPart("Textures.");

		Set<String> tPackNames = TexturesConfig.getKeys(false);
		
		for (String key: tPackNames) {		
			TexturePacksbyName.put(key,FileManager.getResourcePacks().getString("Textures."+key+".url"));			
		}
		
		//then load the players choice of texture
		ConfigurationPart playerConfig = FileManager.getResourcePacks().getConfigurationPart("Players.");

		Set<String> players = playerConfig.getKeys(false);
		
		for (String key: players) 	{
			PlayerTexturePacks.put(Bukkit.getPlayer(key),FileManager.getResourcePacks().getString("Players."+key+".texture"));
		}	
	}
	
	public static void preChange(GamePlayer player, String texturePackName) {
        texturePackName = texturePackName.replace('_', ' ');
		if (TexturePacksbyName.containsKey(texturePackName) || texturePackName == "Default") {
			PlayerTexturePacks.put(player.getVanillaPlayer(), texturePackName);
			ChatManager.sendMessageToPlayer(player,(PvPGamesManager.language.get(player.getVanillaPlayer(), "PLAYER-ResourcePack-Current", ChatColor.GREEN + "You are currently using: " + ChatColor.RED + "{0}" + ChatColor.GREEN + " as your texture pack", ResourcePackManager.PlayerTexturePacks.get(player.getVanillaPlayer().getName()))));
		}
		else {
			ChatManager.sendMessageToPlayer(player, "Sorry, I couldn't find that texture pack. Please notify your server administrator.");
			return;
		}
        String tpackUrl;
        if (texturePackName == "Default") {
            tpackUrl = "https://dl.dropboxusercontent.com/u/71263486/MC%20Texture%20Packs/Default.zip";  //"https://dl.dropbox.com/u/12637402/CellCraft.zip";
        } else {
		    tpackUrl = TexturePacksbyName.get(PlayerTexturePacks.get(player));
        }
		if (tpackUrl == null) ChatManager.sendMessageToPlayer(player, "I'm sorry, I couldn't change your texture pack because the texture pack you chose hadn't been configured properly. There is no url for it set.");
		else changeResourcePack(tpackUrl, player);
	}

    public static void changeResourcePack(String url, GamePlayer player) {
    	player.getVanillaPlayer().setResourcePack(url);
    }

}
