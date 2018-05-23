package com.Ben12345rocks.AylaChat.Objects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.AdvancedCoreHook;
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

	@SuppressWarnings("unused")
	private Main plugin = Main.plugin;

	private ArrayList<String> socialSpyPlayers = new ArrayList<String>();

	/**
	 * @return the socialSpyPlayers
	 */
	public ArrayList<String> getSocialSpyPlayers() {
		return socialSpyPlayers;
	}

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
				String h = messageData.get(3);
				int hash = Integer.parseInt(h);

				Channel ch = ChannelHandler.getInstance().getChannel(chatchannel);
				if (ch == null) {
					plugin.debug("Channel doesn't exist: " + chatchannel);
					return;
				}
				if (ch.isBungeecoord()) {
					ChannelHandler.getInstance().forceChat(null, ch, msg, hash);
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

	private Object ob = new Object();

	public void forceChat(Player player, Channel ch, String msg, int hash) {
		synchronized (ob) {
			messageHistory.put(hash, new MessageData(player.getName(), ch.getChannelName(), msg));
			if (messageHistory.size() > 300) {
				messageHistory.remove(messageHistory.keySet().iterator().next());
			}
		}
		ArrayList<Player> players = ch.getPlayers(player);
		if (players != null && !players.isEmpty()) {
			for (Player p : players) {
				if (p != null) {
					if (ch.canHear(p, p.getLocation())) {
						AdvancedCoreHook.getInstance().getServerHandle().sendMessage(p, addJsonButton(p, msg, hash));
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

	private int generateHash() {
		int i = ThreadLocalRandom.current().nextInt(400);

		if (messageHistory.containsKey(i)) {
			return generateHash();
		}

		return i;
	}

	private LinkedHashMap<Integer, MessageData> messageHistory = new LinkedHashMap<Integer, MessageData>();

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
			forceChat(player, ch, msg, h);
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

	public String format(String msg, Channel ch, Player player, int hash) {
		HashMap<String, String> placeholders = new HashMap<String, String>();
		if (player != null) {
			placeholders.put("player", player.getName());
			placeholders.put("nickname", player.getDisplayName());
			placeholders.put("group", AdvancedCoreHook.getInstance().getPerms().getPrimaryGroup(player));
		}
		placeholders.put("message", msg);

		String message = StringUtils.getInstance().replacePlaceHolder(ch.getFormat(), placeholders, false);

		message = StringUtils.getInstance().replaceJavascript(message);
		message = StringUtils.getInstance().replacePlaceHolders(player, message);
		message = StringUtils.getInstance().colorize(message);

		return message;

	}

	public TextComponent addJsonButton(Player p, String message, int hash) {
		if (p.hasPermission("AylaChat.Button")) {
			return StringUtils.getInstance().parseJson(message += "[Text=\"" + Config.getInstance().formatJsonButton
					+ "\",command=\"aylachat button " + hash + "\"]");
		} else {
			return new TextComponent(message);
		}
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
