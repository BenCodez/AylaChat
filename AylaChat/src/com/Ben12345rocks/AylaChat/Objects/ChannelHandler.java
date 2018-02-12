package com.Ben12345rocks.AylaChat.Objects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Commands.Executors.ChannelCommands;
import com.Ben12345rocks.AylaChat.Config.Config;

public class ChannelHandler {

	private static ChannelHandler instance = new ChannelHandler();

	@SuppressWarnings("unused")
	private Main plugin = Main.plugin;

	private ArrayList<Channel> channels;

	/**
	 * @return the instance
	 */
	public static ChannelHandler getInstance() {
		return instance;
	}

	public ChannelHandler() {
	}

	/**
	 * @return the channels
	 */
	public ArrayList<Channel> getChannels() {
		return channels;
	}

	public void load() {
		// implement channel load
		channels = new ArrayList<Channel>();

		for (String ch : Config.getInstance().getChannels()) {
			Channel channel = new Channel(Config.getInstance().getData().getConfigurationSection("Channels." + ch), ch);
			channels.add(channel);

			loadChannelCommand(ch, channel);

			for (String aliases : channel.getAliases()) {
				loadChannelCommand(aliases, channel);
			}
		}
	}

	private void loadChannelCommand(String cmd, Channel channel) {
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.register(cmd, new ChannelCommands(cmd, channel));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onChat(Player player, String channel, String message) {
		if (channel == null || channel.isEmpty()) {
			channel = getDefaultChannelName();
		}
		Channel ch = getChannel(channel);
		if (!ch.canTalk(player)) {
			plugin.debug("Player " + player.getName() + " can't talk in " + ch.getChannelName());
			return;
		}
		String msg = format(message, ch, player);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != null) {
				if (ch.canHear(p, player.getLocation())) {
					p.sendMessage(msg);
				}
			}
		}

		Bukkit.getConsoleSender().sendMessage(msg);
	}

	public String getDefaultChannelName() {
		for (Channel ch : getChannels()) {
			if (ch.isDefaultChannel()) {
				return ch.getChannelName();
			}
		}
		return null;
	}

	public String format(String msg, Channel ch, Player player) {
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", player.getName());
		placeholders.put("nickname", player.getDisplayName());
		placeholders.put("message", msg);
		placeholders.put("group", AdvancedCoreHook.getInstance().getPerms().getPrimaryGroup(player));

		String message = StringUtils.getInstance().replacePlaceHolder(ch.getFormat(), placeholders);
		message = StringUtils.getInstance().replaceJavascript(message);
		message = StringUtils.getInstance().replacePlaceHolders(player, message);
		message = StringUtils.getInstance().colorize(message);

		return message;

	}

	public Channel getChannel(String channel) {
		for (Channel ch : getChannels()) {
			if (ch.getChannelName().equalsIgnoreCase(channel)) {
				return ch;
			}
			for (String aliases : ch.getAliases()) {
				if (aliases.equalsIgnoreCase(channel)) {
					return ch;
				}
			}
		}
		return null;
	}

	public ArrayList<String> getChannelNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Channel ch : getChannels()) {
			names.add(ch.getChannelName());
		}
		return names;
	}

}
