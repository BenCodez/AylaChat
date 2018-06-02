package com.Ben12345rocks.AylaChat.Config;

import java.io.File;
import java.util.ArrayList;
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

	@ConfigDataString(path = "Format.Message.Send", defaultValue = "%player% -> %toSend%: %message%")
	public String formatMessageSend = "";

	@ConfigDataString(path = "Format.Message.Receive", defaultValue = "%fromsender% -> %player%: %message%")
	public String formatMessageReceive = "";

	@ConfigDataBoolean(path = "UseBungeeCoord", defaultValue = false)
	public boolean useBungeeCoord = false;

	@ConfigDataString(path = "Format.NoOneListening", defaultValue = "&cNo one is listening to you")
	public String formatNoOneListening;

	@ConfigDataString(path = "Format.Message.NoReply", defaultValue = "&cNo one to reply to")
	public String formatMessageNoReply;

	@ConfigDataString(path = "Format.Message.SocialSpy", defaultValue = "[SC] %msg%")
	public String formatMessageSocialSpy;

	@ConfigDataString(path = "Format.Help.Title", defaultValue = "&3AylaChat Help")
	public String formatHelpTitle;

	@ConfigDataString(path = "Format.ChannelSet", defaultValue = "&cSet channel to %channel%")
	public String formatChannelSet;

	@ConfigDataBoolean(path = "Format.Help.RequirePermission", defaultValue = true)
	public boolean formatHelpRequirePermission;

	@ConfigDataString(path = "Format.Muted", defaultValue = "&cYou are currently muted")
	public String formatMuted;

	@ConfigDataString(path = "Format.JsonButton", defaultValue = "&c[x]")
	public String formatJsonButton;

	public Set<String> JsonButtonGUI() {
		return getData().getConfigurationSection("JsonButtonGUI").getKeys(false);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getJsonButtonGUIKeyCommands(String key) {
		return (ArrayList<String>) getData().getList("JsonButtonGUI." + key + ".Commands", new ArrayList<String>());
	}

	public String formatMessageRewards = "Format.Message.Rewards";
}
