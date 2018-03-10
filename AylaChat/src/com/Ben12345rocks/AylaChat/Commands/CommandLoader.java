package com.Ben12345rocks.AylaChat.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Ben12345rocks.AdvancedCore.Objects.CommandHandler;
import com.Ben12345rocks.AdvancedCore.Objects.TabCompleteHandle;
import com.Ben12345rocks.AdvancedCore.Objects.TabCompleteHandler;
import com.Ben12345rocks.AdvancedCore.Util.Misc.ArrayUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.PlayerUtils;
import com.Ben12345rocks.AdvancedCore.Util.Misc.StringUtils;
import com.Ben12345rocks.AylaChat.Main;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAliasHandle;
import com.Ben12345rocks.AylaChat.Commands.Executors.CommandAliases;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AliasHandleTabCompleter;
import com.Ben12345rocks.AylaChat.Commands.TabComplete.AliasesTabCompleter;
import com.Ben12345rocks.AylaChat.Config.Config;
import com.Ben12345rocks.AylaChat.Objects.ChannelHandler;
import com.Ben12345rocks.AylaChat.Objects.PluginMessageHandler;
import com.Ben12345rocks.AylaChat.Objects.User;
import com.Ben12345rocks.AylaChat.Objects.UserManager;
import com.google.common.collect.Iterables;

import net.md_5.bungee.api.chat.TextComponent;

public class CommandLoader {

	/** The instance. */
	static CommandLoader instance = new CommandLoader();

	/** The plugin. */
	static Main plugin = Main.plugin;

	/**
	 * Gets the single instance of CommandLoader.
	 *
	 * @return single instance of CommandLoader
	 */
	public static CommandLoader getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new command loader.
	 */
	private CommandLoader() {
	}

	public void load() {
		plugin.commands = new ArrayList<CommandHandler>();

		plugin.commands.addAll(
				com.Ben12345rocks.AdvancedCore.Commands.CommandLoader.getInstance().getBasicCommands("AylaChat"));
		plugin.commands.addAll(
				com.Ben12345rocks.AdvancedCore.Commands.CommandLoader.getInstance().getBasicAdminCommands("AylaChat"));

		plugin.commands.add(new CommandHandler(new String[] { "Help" }, "AylaChat.Help", "View help information") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ArrayList<TextComponent> texts = new ArrayList<TextComponent>();
				HashMap<String, TextComponent> unsorted = new HashMap<String, TextComponent>();
				texts.add(StringUtils.getInstance().stringToComp(Config.getInstance().formatHelpTitle));

				boolean requirePerms = Config.getInstance().formatHelpRequirePermission;

				for (CommandHandler cmdHandle : plugin.commands) {
					if (cmdHandle.hasPerm(sender)) {
						unsorted.put(cmdHandle.getHelpLineCommand("/aylachat"), cmdHandle.getHelpLine("/aylachat"));
					} else if (!requirePerms) {
						unsorted.put(cmdHandle.getHelpLineCommand("/aylachat"), cmdHandle.getHelpLine("/aylachat"));
					}
				}

				ArrayList<String> unsortedList = new ArrayList<String>();
				unsortedList.addAll(unsorted.keySet());
				Collections.sort(unsortedList, String.CASE_INSENSITIVE_ORDER);
				for (String cmd : unsortedList) {
					texts.add(unsorted.get(cmd));
				}
				sendMessageJson(sender, texts);
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "Reload" }, "AylaChat.Reload", "Reload the plugin") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				plugin.reload();
				sendMessage(sender, "&cReloaded plugin");
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "SetChannel", "(Channel)" }, "AylaChat.SetChannel",
				"Set your channel", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				UserManager.getInstance().getAylaChatUser((Player) sender).setCurrentChannel(args[1]);
				sendMessage(sender, StringUtils.getInstance().replacePlaceHolder(Config.getInstance().formatChannelSet,
						"channel", ChannelHandler.getInstance().getChannel(args[1]).getChannelName()));
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "ClearChat" }, "AylaChat.ClearChat", "Clear Chat") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				ChannelHandler.getInstance().clearChatAll();
				plugin.sendPluginMessage(PlayerUtils.getInstance().getRandomPlayer(), "ClearChat", "All");
				sendMessage(sender, "&cChat cleared");
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "ClearChat", "(Player)" }, "AylaChat.ClearChat.Player",
				"Clear Chat for a specific player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				Player p = Bukkit.getPlayer(args[1]);
				if (p != null) {
					ChannelHandler.getInstance().clearChat(p);
					sendMessage(sender, "&cChat cleared for " + p.getName());
				} else {
					if (Config.getInstance().useBungeeCoord) {
						plugin.sendPluginMessage(PlayerUtils.getInstance().getRandomPlayer(), "ClearChat", args[1]);
						sendMessage(sender, "&cChat cleared for " + args[1]);
						return;
					}
					sendMessage(sender, "&cPlayer " + args[1] + " not found/online");
				}
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "SocialSpy", "(Boolean)" }, "AylaChat.SocialSpy",
				"Set whether or not social spy is enabled", false) {

			@Override
			public void execute(CommandSender sender, String[] args) {
				UserManager.getInstance().getAylaChatUser((Player) sender)
						.setSocialSpyEnabled(Boolean.valueOf(args[1]));
				sendMessage(sender, "Set Socialspy to " + args[1]);
			}
		});

		plugin.commands.add(new CommandHandler(new String[] { "Mute", "(Player)" }, "AylaChat.Mute", "Mute player") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				User user = UserManager.getInstance().getAylaChatUser(args[1]);
				boolean muted = !user.getMuted();
				user.setMuted(muted);
				sendMessage(sender, "&cSet muted for " + args[1] + " to " + muted);
			}
		});

		plugin.commands.add(
				new CommandHandler(new String[] { "Msg", "(Player)", "(List)" }, "AylaChat.Msg", "Msg other players") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						CommandLoader.this.sendMessage(sender, args[1], ArrayUtils.getInstance().makeString(2, args));
					}
				});

		plugin.commands
				.add(new CommandHandler(new String[] { "Reply", "(List)" }, "AylaChat.Msg", "Reply to last message") {

					@Override
					public void execute(CommandSender sender, String[] args) {
						String toSend = "";
						if (sender instanceof Player) {
							toSend = UserManager.getInstance().getAylaChatUser(sender.getName()).getlastMessageSender();
						}
						CommandLoader.this.sendMessage(sender, toSend, ArrayUtils.getInstance().makeString(1, args));
					}
				});

		TabCompleteHandler.getInstance().addTabCompleteOption(
				new TabCompleteHandle("(Channel)", ChannelHandler.getInstance().getChannelNames()) {

					@Override
					public void updateReplacements() {

					}

					@Override
					public void reload() {
						setReplace(ChannelHandler.getInstance().getChannelNames());
					}
				});

		loadAliases();

		plugin.pluginMessages.add(new PluginMessageHandler() {

			@Override
			public void onRecieve(String subChannel, ArrayList<String> messageData) {
				if (subChannel.equals("Msg")) {
					String sender = messageData.get(0);
					String toSend = messageData.get(1);
					String msg = messageData.get(2);

					messageReceived(sender, toSend, msg);
				}
			}
		});
	}

	public void loadAliases() {
		for (CommandHandler cmdHandle : plugin.commands) {
			if (cmdHandle.getArgs().length > 0) {
				String[] args = cmdHandle.getArgs()[0].split("&");
				for (String arg : args) {
					try {
						plugin.getCommand("aylachat" + arg).setExecutor(new CommandAliases(cmdHandle));

						plugin.getCommand("aylachat" + arg)
								.setTabCompleter(new AliasesTabCompleter().setCMDHandle(cmdHandle));

						// special commands
						if (arg.equalsIgnoreCase("msg")) {
							setCommand("msg", cmdHandle);
						} else if (arg.equalsIgnoreCase("reply")) {
							setCommand("reply", cmdHandle);
						} else if (arg.equalsIgnoreCase("socialspy")) {
							setCommand("socialspy", cmdHandle);
						} else if (arg.equalsIgnoreCase("clearchat")) {
							setCommand("clearchat", cmdHandle);
						}

					} catch (Exception ex) {
						plugin.debug("Failed to load command and tab completer for /aylachat" + arg);
					}
				}

			}
		}
	}

	private void setCommand(String command, CommandHandler cmdHandle) {
		CommandExecutor handle = plugin.getCommand(command).getExecutor();
		if (handle == null) {
			plugin.getCommand(command).setExecutor(new CommandAliasHandle(cmdHandle));
			plugin.getCommand(command).setTabCompleter(new AliasHandleTabCompleter().add(cmdHandle));
		} else {
			CommandAliasHandle exec = (CommandAliasHandle) plugin.getCommand(command).getExecutor();
			exec.add(cmdHandle);
			AliasHandleTabCompleter tab = (AliasHandleTabCompleter) plugin.getCommand(command).getTabCompleter();
			tab.add(cmdHandle);
		}

	}

	public void sendMessage(CommandSender player, String toSend, String msg) {
		if (toSend.equals("")) {
			player.sendMessage(StringUtils.getInstance().colorize(Config.getInstance().formatMessageNoReply));
			return;
		}

		String sender = player.getName();
		if (!(player instanceof Player)) {
			sender = "CONSOLE";
		} else {
			UserManager.getInstance().getAylaChatUser(player.getName()).setlastMessageSender(toSend);
		}
		toSend = com.Ben12345rocks.AdvancedCore.UserManager.UserManager.getInstance().getProperName(toSend);
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", sender);
		placeholders.put("toSend", toSend);
		placeholders.put("message", msg);
		String format = StringUtils.getInstance().replacePlaceHolder(Config.getInstance().formatMessageSend,
				placeholders);

		player.sendMessage(StringUtils.getInstance().colorize(format));

		if (Config.getInstance().useBungeeCoord) {
			plugin.sendPluginMessage(Iterables.getFirst(Bukkit.getOnlinePlayers(), null), "Msg", sender, toSend, msg);
		} else {
			messageReceived(sender, toSend, msg);
		}

	}

	public void messageReceived(String sender, String toSend, String msg) {
		HashMap<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("player", toSend);
		placeholders.put("sender", sender);
		placeholders.put("message", msg);
		String format = StringUtils.getInstance().replacePlaceHolder(Config.getInstance().formatMessageReceive,
				placeholders);

		Player p = Bukkit.getPlayer(toSend);
		if (p != null) {
			p.sendMessage(format);
			UserManager.getInstance().getAylaChatUser(p).setlastMessageSender(sender);
		}

		ChannelHandler.getInstance().socialSpyMessage(format);

	}
}
