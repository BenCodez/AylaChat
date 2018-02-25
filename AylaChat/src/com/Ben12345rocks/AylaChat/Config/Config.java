package com.Ben12345rocks.AylaChat.Config;

import java.io.File;
import java.util.Set;

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

	@Override
	public void onFileCreation() {
		Main.plugin.saveResource("Config.yml", true);
	}

	public Set<String> getChannels() {
		return getData().getConfigurationSection("Channels").getKeys(false);
	}

	@ConfigDataString(path = "Format.Message.Send", defaultValue = "%sender% -> %toSend%: %message%")
	private String formatMessageSend = "";
	
	@ConfigDataString(path = "Format.Message.Receive", defaultValue = "%sender% -> %toSend%: %message%")
	private String formatMessageReceive = "";

	@ConfigDataString(path = "Format.Message.PlayerName", defaultValue = "You")
	private String formatMessagePlayerName = "";

	/**
	 * @return the formatMessageSend
	 */
	public String getFormatMessageSend() {
		return formatMessageSend;
	}

	/**
	 * @return the formatMessageReceive
	 */
	public String getFormatMessageReceive() {
		return formatMessageReceive;
	}

	/**
	 * @return the formatMessageSenderName
	 */
	public String getFormatMessagePlayerName() {
		return formatMessagePlayerName;
	}
}
