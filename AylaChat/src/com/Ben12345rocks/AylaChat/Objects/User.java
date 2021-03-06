package com.Ben12345rocks.AylaChat.Objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.UserManager.UUID;
import com.Ben12345rocks.AylaChat.Main;

// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User extends com.Ben12345rocks.AdvancedCore.UserManager.User {

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

	public void checkChannels() {
		if (getCurrentChannel().isEmpty()) {
			Channel d = ChannelHandler.getInstance().getChannel(ChannelHandler.getInstance().getDefaultChannelName());
			if (d.canTalk(getPlayer())) {
				setCurrentChannel(d.getChannelName());
				return;
			}
			for (Channel ch : ChannelHandler.getInstance().getChannels()) {
				if (ch.canTalk(getPlayer())) {
					setCurrentChannel(ch.getChannelName());
					return;
				}
			}
		}

		if (getSocialSpyEnabled()) {
			if (!ChannelHandler.getInstance().getSocialSpyPlayers().contains(getUUID())) {
				ChannelHandler.getInstance().getSocialSpyPlayers().add(getUUID());
			}
		} else {
			if (ChannelHandler.getInstance().getSocialSpyPlayers().contains(getUUID())) {
				ChannelHandler.getInstance().getSocialSpyPlayers().remove(getUUID());
			}
		}
	}

	public ArrayList<String> getChannelsLeft() {
		return getData().getStringList("ChannelsLeft");
	}

	public String getCurrentChannel() {
		return getData().getString("Channel");
	}

	public String getlastMessageSender() {
		return getData().getString("LastMessageSender");
	}

	private boolean getMuted() {
		return Boolean.valueOf(getData().getString("Muted"));
	}

	public boolean getSocialSpyEnabled() {
		return Boolean.valueOf(getData().getString("SocialSpy"));
	}

	public boolean isMuted() {
		return getMuted();
	}

	public void mute() {
		setMuted(true);
	}

	public void setChannelsLeft(ArrayList<String> channels) {
		getData().setStringList("ChannelsLeft", channels);
	}

	public void setCurrentChannel(String channel) {
		getData().setString("Channel", channel);
	}

	public void setlastMessageSender(String value) {
		getData().setString("LastMessageSender", value);
	}

	private void setMuted(boolean value) {
		getData().setString("Muted", "" + value);
	}

	public void setSocialSpyEnabled(boolean value) {
		getData().setString("SocialSpy", "" + value);
		checkChannels();
	}

	public void unMute() {
		setMuted(false);
	}

}