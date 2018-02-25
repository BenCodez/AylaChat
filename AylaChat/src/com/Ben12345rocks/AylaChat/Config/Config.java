package com.Ben12345rocks.AylaChat.Config;

import java.io.File;
import java.util.Set;

import com.Ben12345rocks.AdvancedCore.Util.Annotation.AnnotationHandler;
import com.Ben12345rocks.AdvancedCore.Util.Annotation.ConfigDataBoolean;
import com.Ben12345rocks.AdvancedCore.Util.Annotation.ConfigDataString;
import com.Ben12345rocks.AdvancedCore.YML.YMLFile;
import com.Ben12345rocks.AylaChat.Main;

public class Config extends YMLFile {

	static Config instance = new Config();

	/** The plugin. */
	static Main plugin = Main.plugin;

	/**
	 * Gets the single instance of Config.
	 *
	 * @return single instance of Config
	 */
	public static Config getInstance() {
		return instance;
	}

	public Config() {
		super(new File(Main.plugin.getDataFolder(), "Config.yml"));
	}

	public void loadValues() {
		new AnnotationHandler().load(getData(), this);
	}

	@Override
	public void onFileCreation() {
		Main.plugin.saveResource("Config.yml", true);
	}

	public Set<String> getChannels() {
		return getData().getConfigurationSection("Channels").getKeys(false);
	}

	@ConfigDataString(path = "Format.Message.Send", defaultValue = "%sender% -> %toSend%: %message%")
	public String formatMessageSend = "";

	@ConfigDataString(path = "Format.Message.Receive", defaultValue = "%sender% -> %toSend%: %message%")
	public String formatMessageReceive = "";

	@ConfigDataString(path = "Format.Message.PlayerName", defaultValue = "You")
	public String formatMessagePlayerName = "";

	@ConfigDataBoolean(path = "UseBungeeCoord", defaultValue = false)
	public boolean useBungeeCoord = false;
}
