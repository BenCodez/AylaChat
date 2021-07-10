package com.bencodez.aylachat.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import com.bencodez.advancedcore.api.yml.YMLFile;
import com.bencodez.advancedcore.api.yml.annotation.AnnotationHandler;
import com.bencodez.advancedcore.api.yml.annotation.ConfigDataBoolean;
import com.bencodez.advancedcore.api.yml.annotation.ConfigDataString;
import com.bencodez.aylachat.AylaChatMain;

public class Config extends YMLFile {

	/** The plugin. */
	private AylaChatMain plugin;

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

	public String formatMessageRewards = "Format.Message.Rewards";

	public Config(AylaChatMain plugin) {
		super(plugin, new File(plugin.getDataFolder(), "Config.yml"), true);
		this.plugin = plugin;
	}

	public Set<String> getChannels() {
		return getData().getConfigurationSection("Channels").getKeys(false);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getJsonButtonGUIKeyCommands(String key) {
		return (ArrayList<String>) getData().getList("JsonButtonGUI." + key + ".Commands", new ArrayList<String>());
	}

	public Set<String> JsonButtonGUI() {
		return getData().getConfigurationSection("JsonButtonGUI").getKeys(false);
	}

	public void loadValues() {
		new AnnotationHandler().load(getData(), this);
	}

	@Override
	public void onFileCreation() {
		plugin.saveResource("Config.yml", true);
	}
}
