package com.Ben12345rocks.AylaChat.Objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Objects.UUID;
import com.Ben12345rocks.AylaChat.Main;

// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User extends com.Ben12345rocks.AdvancedCore.Objects.User {

	/** The plugin. */
	static Main plugin = Main.plugin;

	/**
	 * Instantiates a new user.
	 *
	 * @param player
	 *            the player
	 */
	@Deprecated
	public User(Player player) {
		super(Main.plugin, player);
	}

	/**
	 * Instantiates a new user.
	 *
	 * @param playerName
	 *            the player name
	 */
	@Deprecated
	public User(String playerName) {
		super(Main.plugin, playerName);

	}

	/**
	 * Instantiates a new user.
	 *
	 * @param uuid
	 *            the uuid
	 */
	@Deprecated
	public User(UUID uuid) {
		super(Main.plugin, uuid);

	}

	/**
	 * Instantiates a new user.
	 *
	 * @param uuid
	 *            the uuid
	 * @param loadName
	 *            the load name
	 */
	@Deprecated
	public User(UUID uuid, boolean loadName) {
		super(Main.plugin, uuid, loadName);
	}

	public void setCurrentChannel(String channel) {
		getData().setString("Channel", channel);
	}

	public String getCurrentChannel() {
		return getData().getString("Channel");
	}

	public void setChannelsLeft(ArrayList<String> channels) {
		getData().setStringList("ChannelsLeft", channels);
	}

	public ArrayList<String> getChannelsLeft() {
		return getData().getStringList("ChannelsLeft");
	}
	
	public void checkChannels() {
		// for future use
	}

}