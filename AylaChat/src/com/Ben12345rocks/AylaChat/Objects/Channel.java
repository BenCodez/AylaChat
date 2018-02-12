package com.Ben12345rocks.AylaChat.Objects;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;

public class Channel {

	private String channelName;
	private String format;
	private String permission;
	private boolean bungeecoord;
	private int distance;
	private boolean autojoin;
	private boolean defaultChannel;
	private ArrayList<String> aliases;

	@SuppressWarnings("unchecked")
	public Channel(ConfigurationSection data, String channelName) {
		this.channelName = channelName;
		format = data.getString("Format");
		permission = data.getString("Permission");
		bungeecoord = data.getBoolean("Bungeecoord");
		distance = data.getInt("Distance", -1);
		autojoin = data.getBoolean("AutoJoin", true);
		defaultChannel = data.getBoolean("Default", false);
		aliases = (ArrayList<String>) data.getList("Aliases", new ArrayList<String>());
	}

	/**
	 * @return the aliases
	 */
	public ArrayList<String> getAliases() {
		return aliases;
	}

	/**
	 * @param aliases
	 *            the aliases to set
	 */
	public void setAliases(ArrayList<String> aliases) {
		this.aliases = aliases;
	}

	public boolean canTalk(Player p) {
		if (p.hasPermission(permission) || permission.isEmpty()) {
			return true;
		}
		p.sendMessage(StringUtils.getInstance().colorize(AdvancedCoreHook.getInstance().getFormatNoPerms()));
		return false;
	}

	/**
	 * See if player can listen to channel
	 * 
	 * @param reciever
	 * @param loc
	 * @return
	 */
	public boolean canHear(Player reciever, Location loc) {
		if (reciever.hasPermission(permission) || permission.isEmpty()) {
			if (UserManager.getInstance().getAylaChatUser(reciever).getChannelsLeft().contains(channelName)) {
				return false;
			}
			if (distance > 0) {
				if (loc.distance(reciever.getLocation()) < distance) {
					return true;
				}
			} else if (distance == 0) {
				if (loc.getWorld().getName().equals(reciever.getWorld().getName())) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName
	 *            the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * @param permission
	 *            the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * @return the bungeecoord
	 */
	public boolean isBungeecoord() {
		return bungeecoord;
	}

	/**
	 * @param bungeecoord
	 *            the bungeecoord to set
	 */
	public void setBungeecoord(boolean bungeecoord) {
		this.bungeecoord = bungeecoord;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	/**
	 * @return the autojoin
	 */
	public boolean isAutojoin() {
		return autojoin;
	}

	/**
	 * @param autojoin
	 *            the autojoin to set
	 */
	public void setAutojoin(boolean autojoin) {
		this.autojoin = autojoin;
	}

	/**
	 * @return the defaultChannel
	 */
	public boolean isDefaultChannel() {
		return defaultChannel;
	}

	/**
	 * @param defaultChannel
	 *            the defaultChannel to set
	 */
	public void setDefaultChannel(boolean defaultChannel) {
		this.defaultChannel = defaultChannel;
	}
}
