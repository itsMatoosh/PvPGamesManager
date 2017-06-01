package pl.glonojad.pvpgamesmanager.util.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import pl.glonojad.pvpgamesmanager.util.ChatManager;
import pl.glonojad.pvpgamesmanager.util.FileManager;

public class Configuration {
	
	public YamlConfiguration configurationFile;
	public ConfigurationType type;
	
	public Configuration (YamlConfiguration config, ConfigurationType type) {
		this.configurationFile = config;
		this.type = type;
	}
	public int getInt(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.getInt(path);
		}
		else {
			configurationFile.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return 0;
		}
	}
	public String getString(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.getString(path);
		}
		else {
			configurationFile.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return "";
		}
	}
	public double getDouble(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.getDouble(path);
		}
		else {
			configurationFile.set(path, 0d);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return 0;
		}
	}
	public List<?> getList(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.getList(path);
		}
		else {
			configurationFile.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return null;
		}
	}
	public boolean getBoolean(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.getBoolean(path);
		}
		else {
			configurationFile.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return false;
		}
	}
	public List<String> getStringList(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.getStringList(path);
		}
		else {
			ArrayList<String> sampleList = new ArrayList<String>();
			sampleList.add("");
			configurationFile.set(path, sampleList);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return sampleList;
		}
	}
	public boolean isSet(String path) {
		if(configurationFile.contains(path)) {
			return configurationFile.isSet(path);
		}
		else {
			configurationFile.set(path, "");
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return false;
		}
	}
	public ConfigurationPart getConfigurationPart(String path) {
		if(configurationFile.contains(path)) {
			return new ConfigurationPart(configurationFile.getConfigurationSection(path), this);
		}
		else {
			configurationFile.createSection(path);
			ChatManager.logWarning("Configuration path: " + path + " in file " + configurationFile.getName() + " didn't exist and was created with a default value!");
			FileManager.saveOtherConfig(configurationFile, type);
			return new ConfigurationPart(configurationFile.getConfigurationSection(path), this);
		}
	}
	public void set(String path, Object object) {
		configurationFile.set(path, object);
		FileManager.saveOtherConfig(configurationFile, type);
	}
}
