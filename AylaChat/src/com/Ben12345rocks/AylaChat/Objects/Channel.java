package com.Ben12345rocks.AylaChat.Objects;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.massivecraft.factions.entity.MPlayer;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;

public class Channel {

	private String channelName;
	private String format;
	private String permission;
	private boolean bungeecoord;
	private int distance;
	private boolean autojoin;
	private boolean defaultChannel;
	private ArrayList<String> aliases;
	private boolean loadMainChannelCommand;
	private boolean loadAliasChannelCommands;

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
		loadMainChannelCommand = data.getBoolean("LoadMainChannelCommand", true);
		loadAliasChannelCommands = data.getBoolean("LoadAliasChannelCommands", true);
	}

	/**
	 * @return the loadMainChannelCommand
	 */
	public boolean isLoadMainChannelCommand() {
		return loadMainChannelCommand;
	}

	/**
	 * @return the loadAliasChannelCommands
	 */
	public boolean isLoadAliasChannelCommands() {
		return loadAliasChannelCommands;
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
		if (p == null) {
			return false;
		}
		if (p.hasPermission(permission) || permission.isEmpty()) {
			return true;
		} else {
			// no permission
			p.sendMessage(StringUtils.getInstance().colorize(AdvancedCoreHook.getInstance().getFormatNoPerms()));
		}

		User user = UserManager.getInstance().getAylaChatUser(p);
		if (user.getMuted()) {
			p.sendMessage("Muted");
			return false;
		}

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
			if (ChannelHandler.getInstance().getSocialSpyPlayers().contains(reciever.getUniqueId().toString())) {
				return true;
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
	@Deprecated
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

	@SuppressWarnings("deprecation")
	public ArrayList<Player> getPlayers(Player player) {
		ArrayList<Player> players = new ArrayList<Player>();

		if (player == null) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				players.add(p);
			}
		} else if (getChannelName().equalsIgnoreCase("Town")) {
			try {
				Resident res = Towny.plugin.getTownyUniverse().getResident(player.getName());
				if (res.hasTown()) {
					for (Resident r : res.getTown().getResidents()) {
						Player p = Bukkit.getPlayer(r.getName());
						if (p != null) {
							players.add(p);
						}
					}
				} else {
					players.add(player);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (getChannelName().equalsIgnoreCase("Nation")) {
			try {
				Resident res = Towny.plugin.getTownyUniverse().getResident(player.getName());
				if (res.hasNation()) {
					for (Resident r : res.getTown().getNation().getResidents()) {
						Player p = Bukkit.getPlayer(r.getName());
						if (p != null) {
							players.add(p);
						}
					}
				} else {
					players.add(player);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (getChannelName().equalsIgnoreCase("Factions")) {
			try {
				MPlayer mplayer = MPlayer.get(player.getUniqueId().toString());
				if (mplayer.hasFaction()) {
					for (MPlayer mp : mplayer.getFaction().getMPlayers()) {
						Player p = mp.getPlayer().getPlayer();
						if (p != null) {
							players.add(p);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				players.add(p);
			}
		}
		return players;
	}
}
