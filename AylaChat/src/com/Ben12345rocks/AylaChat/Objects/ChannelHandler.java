package com.Ben12345rocks.AylaChat.Objects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCorePlugin;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AdvancedCore.Util.PluginMessage.PluginMessage;
import com.Ben12345rocks.AdvancedCore.Util.PluginMessage.PluginMessageHandler;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Commands.Executors.ChannelCommands;
import com.Ben12345rocks.AylaChat.Config.Config;

import net.md_5.bungee.api.chat.TextComponent;

public class ChannelHandler {

	private static ChannelHandler instance = new ChannelHandler();

	/**
	 * @return the instance
	 */
	public static ChannelHandler getInstance() {
		return instance;
	}

	private Main plugin = Main.plugin;

	private ArrayList<String> socialSpyPlayers = new ArrayList<String>();

	private ArrayList<Channel> channels;

	private Object ob = new Object();

	private LinkedHashMap<Integer, MessageData> messageHistory = new LinkedHashMap<Integer, MessageData>();

	public ChannelHandler() {
	}

	public TextComponent addJsonButton(Player p, String message, int hash) {
		if (p.hasPermission("AylaChat.Button")) {
			return StringUtils.getInstance()
					.parseJson(message += " [Text=\""
							+ StringUtils.getInstance().colorize(Config.getInstance().formatJsonButton)
							+ "\",command=\"/aylachat button " + hash + "\"]");
		} else {
			return new TextComponent(message);
		}
	}

	public void clearChat(Player player) {
		if (player != null) {
			ArrayList<String> blank = new ArrayList<String>();
			for (int i = 0; i < 200; i++) {
				blank.add("");
			}
			player.sendMessage(ArrayUtils.getInstance().convert(blank));
		}
	}

	public void clearChatAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			clearChat(player);
		}
	}

	public void clearChatAll(Player player, Channel channel) {
		for (Player p : channel.getPlayers(player)) {
			clearChat(p);
		}
	}

	/*
	 * public void removedMessage(Player player, Channel channel) {
	 * clearChatAll(player, channel); ArrayList<String> messages = new
	 * ArrayList<String>(); for (Entry<Integer, MessageData> entry :
	 * messageHistory.entrySet()) { for (Player p : channel.getPlayers(player)) {
	 * p.sendMessage(entry.getValue().getMessage()); } }
	 * }
	 */

	public void create(String value) {
		Config.getInstance().getData().createSection("Channels." + value);
		Config.getInstance().saveData();
	}

	public void forceChat(String playerName, Channel ch, String msg, int hash) {
		Player player = Bukkit.getPlayer(playerName);
		synchronized (ob) {
			messageHistory.put(hash, new MessageData(playerName, ch.getChannelName(), msg));
			if (messageHistory.size() > 300) {
				messageHistory.remove(messageHistory.keySet().iterator().next());
			}
		}
		ArrayList<Player> players = ch.getPlayers(player);
		if (players != null && !players.isEmpty()) {
			for (Player p : players) {
				if (p != null) {
					if (ch.canHear(p, p.getLocation())) {
						AdvancedCorePlugin.getInstance().getServerHandle().sendMessage(p, addJsonButton(p, msg, hash));
					}
				}
			}
		} else {
			if (player != null) {
				player.sendMessage(StringUtils.getInstance().colorize(Config.getInstance().formatNoOneListening));
			}
		}

		Bukkit.getConsoleSender().sendMessage(msg);
	}

	public String format(String msg, Channel ch, Player player, int hash) {
		HashMap<String, String> placeholders = new HashMap<String, String>();
		if (player != null) {
			placeholders.put("player", getPlayerJson(player.getName()));
			placeholders.put("nickname", getPlayerJson(player.getDisplayName()));
			placeholders.put("group", AdvancedCorePlugin.getInstance().getPerms().getPrimaryGroup(player));
		}
		placeholders.put("message", msg);

		String message = StringUtils.getInstance().replacePlaceHolder(ch.getFormat(), placeholders, false);

		message = StringUtils.getInstance().replaceJavascript(message);
		message = StringUtils.getInstance().replacePlaceHolders(player, message);
		message = StringUtils.getInstance().colorize(message);

		return message;
	}

	private int generateHash() {
		int i = ThreadLocalRandom.current().nextInt(400);

		if (messageHistory.containsKey(i)) {
			return generateHash();
		}

		return i;
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

	/**
	 * @return the channels
	 */
	public ArrayList<Channel> getChannels() {
		return channels;
	}

	@SuppressWarnings("deprecation")
	public String getDefaultChannelName() {
		String defaultChannel = Config.getInstance().getData().getString("DefaultChanne", "");

		for (Channel ch : getChannels()) {
			if (ch.isDefaultChannel()) {
				defaultChannel = ch.getChannelName();
			}
		}
		if (defaultChannel.isEmpty()) {
			if (getChannels().size() > 0) {
				defaultChannel = getChannels().get(0).getChannelName();
			}
		}
		return defaultChannel;
	}

	public LinkedHashMap<Integer, MessageData> getMessageHistory() {
		return messageHistory;
	}

	private String getPlayerJson(String name) {
		return "[Text=\"" + name + "\",suggest_command=\"/msg " + name + "\"]";
	}

	/**
	 * @return the socialSpyPlayers
	 */
	public ArrayList<String> getSocialSpyPlayers() {
		return socialSpyPlayers;
	}

	public void load() {
		messageHistory = new LinkedHashMap<Integer, MessageData>();

		// implement channel load
		channels = new ArrayList<Channel>();

		for (String ch : Config.getInstance().getChannels()) {
			Channel channel = new Channel(Config.getInstance().getData().getConfigurationSection("Channels." + ch), ch);
			channels.add(channel);

			if (!ch.equalsIgnoreCase("town") && !ch.equalsIgnoreCase("nation")) {
				if (channel.isLoadMainChannelCommand()) {
					loadChannelCommand(ch, channel);
				}
			}

			if (channel.isLoadAliasChannelCommands()) {
				for (String aliases : channel.getAliases()) {
					loadChannelCommand(aliases, channel);
				}
			}
		}

		PluginMessage.getInstance().add(new PluginMessageHandler("Chat") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {

				String chatchannel = messageData.get(0);
				String msg = messageData.get(1);
				String name = messageData.get(2);
				String h = messageData.get(3);
				int hash = Integer.parseInt(h);

				Channel ch = ChannelHandler.getInstance().getChannel(chatchannel);
				if (ch == null) {
					plugin.debug("Channel doesn't exist: " + chatchannel);
					return;
				}
				if (ch.isBungeecoord()) {
					ChannelHandler.getInstance().forceChat(name, ch, msg, hash);
				} else {
					plugin.debug(ch.getChannelName() + " isn't bungeecoord, error?");
				}

			}

		});

		PluginMessage.getInstance().add(new PluginMessageHandler("ClearChat") {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				String players = messageData.get(0);
				if (players.equals("All")) {
					ChannelHandler.getInstance().clearChatAll();
				} else {
					ChannelHandler.getInstance().clearChat(Bukkit.getPlayer(players));
				}
			}
		});
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
			plugin.debug("Player " + player.getName() + " can't talk in " + ch.getChannelName() + ": " + message);
			return;
		}

		int h = generateHash();

		String msg = format(message, ch, player, h);

		if (Config.getInstance().useBungeeCoord && ch.isBungeecoord()) {
			plugin.sendPluginMessage(player, "Chat", ch.getChannelName(), msg, player.getName(), "" + h);
			messageHistory.put(h, new MessageData(player.getName(), ch.getChannelName(), msg));
		} else {
			forceChat(player.getName(), ch, msg, h);
		}
	}

	public void setValue(String channelName, String key, Object value) {
		Config.getInstance().getData().set("Channels." + channelName + "." + key, value);
		Config.getInstance().saveData();
	}

	public void socialSpyMessage(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			User user = UserManager.getInstance().getAylaChatUser(p);
			if (user.getSocialSpyEnabled()) {
				String format = Config.getInstance().formatMessageSocialSpy;
				format = StringUtils.getInstance().replacePlaceHolder(format, "msg", msg);
				user.sendMessage(format);
			}
		}
	}

}
