package pl.glonojad.pvpgamesmanager.util.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;

public class ConfigurationPart {
	public ConfigurationSection configurationSection;
	public Configuration parent;
	
	public ConfigurationPart (ConfigurationSection section, Configuration parent) {
		this.configurationSection = section;
		this.parent = parent;
	}
	public int getInt(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getInt(path);
		}
		else {
			configurationSection.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return 0;
		}
	}
	public String getString(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getString(path);
		}
		else {
			configurationSection.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return "";
		}
	}
	public String getName() {
		return configurationSection.getName();
	}
	public boolean getBoolean(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getBoolean(path);
		}
		else {
			configurationSection.set(path, false);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return false;
		}
	}
	public double getDouble(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getDouble(path);
		}
		else {
			configurationSection.set(path, 0d);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return 0;
		}
	}
	public long getLong(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getLong(path);
		}
		else {
			configurationSection.set(path, 0L);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return 0L;
		}
	}
	public Set<String> getKeys(boolean deep) {
		return configurationSection.getKeys(deep);
	}
	public ItemStack getItemStack(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getItemStack(path);
		}
		else {
			configurationSection.set(path, new ItemStack(Material.AIR, 1));
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return configurationSection.getItemStack(path);
		}
	}
	public List<?> getList(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getList(path);
		}
		else {
			configurationSection.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return null;
		}
	}
	public List<String> getStringList(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.getStringList(path);
		}
		else {
			configurationSection.set(path, new ArrayList<String>());
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return null;
		}
	}
	public boolean isSet(String path) {
		if(configurationSection.contains(path)) {
			return configurationSection.isSet(path);
		}
		else {
			configurationSection.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return false;
		}
	}
	public boolean isList(String path) {
		return configurationSection.isList(path);
	}
	public ConfigurationPart getConfigurationPart(String path) {
		if(configurationSection.contains(path)) {
			return new ConfigurationPart(configurationSection.getConfigurationSection(path), this.parent);
		}
		else {
			configurationSection.createSection(path);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationSection.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(parent.configurationFile, parent.type);
			return new ConfigurationPart(configurationSection.getConfigurationSection(path), this.parent);
		}
	}
	public void set(String path, Object object) {
		configurationSection.set(path, object);
		FileManager.saveOtherConfig(parent.configurationFile, parent.type);
	}
	public ConfigurationPart createPart(String path) {
		ConfigurationPart tmp = new ConfigurationPart(configurationSection.createSection(path), this.parent);
		FileManager.saveOtherConfig(parent.configurationFile, parent.type);
		return tmp;
	}
}
