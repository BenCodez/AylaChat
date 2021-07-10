package com.bencodez.aylachat.objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.bencodez.advancedcore.api.user.UUID;
import com.bencodez.aylachat.AylaChatMain;

// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class AylaChatUser extends com.bencodez.advancedcore.api.user.AdvancedCoreUser {

	/** The plugin. */
	static AylaChatMain plugin = AylaChatMain.plugin;

	/**
	 * Instantiates a new user.
	 *
	 * @param player the player
	 */
	@Deprecated
	public AylaChatUser(Player player) {
		super(AylaChatMain.plugin, player);
	}

	/**
	 * Instantiates a new user.
	 *
	 * @param playerName the player name
	 */
	@Deprecated
	public AylaChatUser(String playerName) {
		super(AylaChatMain.plugin, playerName);

	}

	/**
	 * Instantiates a new user.
	 *
	 * @param uuid the uuid
	 */
	@Deprecated
	public AylaChatUser(UUID uuid) {
		super(AylaChatMain.plugin, uuid);

	}

	/**
	 * Instantiates a new user.
	 *
	 * @param uuid     the uuid
	 * @param loadName the load name
	 */
	@Deprecated
	public AylaChatUser(UUID uuid, boolean loadName) {
		super(AylaChatMain.plugin, uuid, loadName);
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
		return getData().getString("Channel", true);
	}

	public String getlastMessageSender() {
		return getData().getString("LastMessageSender", true);
	}

	private boolean getMuted() {
		return Boolean.valueOf(getData().getString("Muted", true));
	}

	public boolean getSocialSpyEnabled() {
		return Boolean.valueOf(getData().getString("SocialSpy", true));
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